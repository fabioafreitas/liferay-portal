/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.headless.cmp.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.dto.v1_0.FunnelStage;
import com.liferay.headless.cmp.dto.v1_0.MatrixCell;
import com.liferay.headless.cmp.dto.v1_0.Persona;
import com.liferay.headless.cmp.resource.v1_0.ContentCoverageResource;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.FilterAggregationResult;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.util.RelatedAssetTagProviderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Computes the per-project content coverage matrix: the distinct asset count
 * for every persona / funnel-stage combination, including the "No Persona" and
 * "No Funnel" buckets. The matrix is computed on read by aggregating the
 * project's in-scope assets; no coverage figure is persisted. Coverage %,
 * critical-gap counts, and color intensity are derived in the front end, so
 * this resource returns only facts.
 *
 * @author Fábio Alves
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-coverage.properties",
	scope = ServiceScope.PROTOTYPE, service = ContentCoverageResource.class
)
public class ContentCoverageResourceImpl
	extends BaseContentCoverageResourceImpl {

	@Override
	public ContentCoverage getProjectContentCoverage(
			Long projectId, Long cmsGroupId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-58677")) {

			throw new UnsupportedOperationException();
		}

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		List<Persona> personas = _getPersonas(cmsGroupId, locale);
		List<FunnelStage> funnelStages = _getFunnelStages(cmsGroupId, locale);

		Set<String> assetTagNames =
			RelatedAssetTagProviderUtil.getRelatedAssetTagNames(
				projectId, _PROJECT_TO_TASKS_RELATIONSHIP_ERC,
				_assetTagLocalService, _objectDefinitionLocalService,
				_objectEntryLocalService, _objectRelationshipLocalService);

		ContentCoverage contentCoverage = new ContentCoverage();

		contentCoverage.setPersonas(personas.toArray(new Persona[0]));
		contentCoverage.setFunnelStages(
			funnelStages.toArray(new FunnelStage[0]));

		if (assetTagNames.isEmpty()) {
			contentCoverage.setMatrixCells(new MatrixCell[0]);
			contentCoverage.setTotalAssetCount(0L);

			return contentCoverage;
		}

		SearchResponse searchResponse = _search(
				cmsGroupId, assetTagNames, personas, funnelStages);

		contentCoverage.setMatrixCells(
			_toMatrixCells(searchResponse, personas, funnelStages));
		contentCoverage.setTotalAssetCount(searchResponse.getCount());

		return contentCoverage;
	}

	private String _aggregationName(String personaId, String funnelStageId) {
		return "cell:" + personaId + ":" + funnelStageId;
	}

	private long[] _getCategoryIds(List<? extends Object> terms) {
		List<Long> categoryIds = new ArrayList<>();

		for (Object term : terms) {
			String id = _termId(term);

			if (!_isUncategorized(term)) {
				categoryIds.add(GetterUtil.getLong(id));
			}
		}

		long[] array = new long[categoryIds.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = categoryIds.get(i);
		}

		return array;
	}

	private Query _getCategoryQuery(
		String id, boolean uncategorized, long[] allCategoryIds) {

		if (uncategorized) {
			TermsQuery termsQuery = QueriesUtil.terms(Field.ASSET_CATEGORY_IDS);

			for (long categoryId : allCategoryIds) {
				termsQuery.addValue(categoryId);
			}

			BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

			return booleanQuery.addMustNotQueryClauses(termsQuery);
		}

		return QueriesUtil.term(
			Field.ASSET_CATEGORY_IDS, GetterUtil.getLong(id));
	}

	private <T> List<T> _getVocabularyTerms(
		Long groupId, String vocabularyErc,
		Function<AssetCategory, T> mapper, T uncategorizedTerm) {

		List<T> terms = new ArrayList<>();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					vocabularyErc, groupId);

		if (assetVocabulary != null) {
			for (AssetCategory assetCategory :
				_assetCategoryLocalService.getVocabularyCategories(
					assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

				terms.add(mapper.apply(assetCategory));
			}
		}

		terms.add(uncategorizedTerm);

		return terms;
	}

	private List<FunnelStage> _getFunnelStages(Long groupId, Locale locale) {
		FunnelStage noFunnelStage = new FunnelStage();

		noFunnelStage.setId(_NO_FUNNEL_STAGE_ID);
		noFunnelStage.setName("No Funnel");
		noFunnelStage.setUncategorized(true);

		return _getVocabularyTerms(
			groupId, _FUNNEL_STAGE_VOCABULARY_ERC,
			assetCategory -> {
				FunnelStage persona = new FunnelStage();

				persona.setDescription(assetCategory.getDescription(locale));
				persona.setExternalReferenceCode(
					assetCategory.getExternalReferenceCode());
				persona.setId(String.valueOf(assetCategory.getCategoryId()));
				persona.setName(assetCategory.getTitle(locale));
				persona.setUncategorized(false);

				return persona;
			},
			noFunnelStage);
	}

	private List<Persona> _getPersonas(Long groupId, Locale locale) {
		Persona noPersona = new Persona();

		noPersona.setId(_NO_PERSONA_ID);
		noPersona.setName("No Persona");
		noPersona.setUncategorized(true);

		return _getVocabularyTerms(
			groupId, _PERSONAS_VOCABULARY_ERC,
			assetCategory -> {
				Persona persona = new Persona();

				persona.setDescription(assetCategory.getDescription(locale));
				persona.setExternalReferenceCode(
					assetCategory.getExternalReferenceCode());
				persona.setId(String.valueOf(assetCategory.getCategoryId()));
				persona.setName(assetCategory.getTitle(locale));
				persona.setUncategorized(false);

				return persona;
			},
			noPersona);
	}


	private boolean _isUncategorized(Object term) {
		if (term instanceof Persona) {
			return GetterUtil.getBoolean(((Persona)term).getUncategorized());
		}

		return GetterUtil.getBoolean(((FunnelStage)term).getUncategorized());
	}

	private SearchResponse _search(
		Long cmsGroupId, Set<String> assetTagNames, List<Persona> personas,
		List<FunnelStage> funnelStages) {

		TermsQuery assetTagNamesQuery = QueriesUtil.terms(Field.ASSET_TAG_NAMES);

		for (String assetTagName : assetTagNames) {
			assetTagNamesQuery.addValue(assetTagName);
		}

		BooleanQuery scopeQuery = QueriesUtil.booleanQuery();

		scopeQuery.addFilterQueryClauses(assetTagNamesQuery);

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.companyId(
			contextCompany.getCompanyId()
		).groupIds(
			cmsGroupId
		).emptySearchEnabled(
			true
		).query(
			scopeQuery
		).size(
			0
		);

		long[] personaCategoryIds = _getCategoryIds(personas);
		long[] funnelStageCategoryIds = _getCategoryIds(funnelStages);

		for (Persona persona : personas) {
			Query personaQuery = _getCategoryQuery(
				persona.getId(), _isUncategorized(persona), personaCategoryIds);

			for (FunnelStage funnelStage : funnelStages) {
				Query funnelStageQuery = _getCategoryQuery(
					funnelStage.getId(), _isUncategorized(funnelStage),
					funnelStageCategoryIds);

				BooleanQuery cellQuery = QueriesUtil.booleanQuery();

				cellQuery.addMustQueryClauses(personaQuery, funnelStageQuery);

				Aggregation filterAggregation = _aggregations.filter(
					_aggregationName(persona.getId(), funnelStage.getId()),
					cellQuery);

				searchRequestBuilder.addAggregation(filterAggregation);
			}
		}

		return _searcher.search(searchRequestBuilder.build());
	}

	private String _termId(Object term) {
		if (term instanceof Persona) {
			return ((Persona)term).getId();
		}

		return ((FunnelStage)term).getId();
	}

	private MatrixCell[] _toMatrixCells(
		SearchResponse searchResponse, List<Persona> personas,
		List<FunnelStage> funnelStages) {

		List<MatrixCell> matrixCells = new ArrayList<>();

		for (Persona persona : personas) {
			for (FunnelStage funnelStage : funnelStages) {
				FilterAggregationResult filterAggregationResult =
					(FilterAggregationResult)
						searchResponse.getAggregationResult(
							_aggregationName(
								persona.getId(), funnelStage.getId()));

				if ((filterAggregationResult == null) ||
					(filterAggregationResult.getDocCount() == 0)) {

					continue;
				}

				MatrixCell matrixCell = new MatrixCell();

				matrixCell.setFunnelStageId(funnelStage.getId());
				matrixCell.setPersonaId(persona.getId());
				matrixCell.setTotalCount(
					filterAggregationResult.getDocCount());

				matrixCells.add(matrixCell);
			}
		}

		return matrixCells.toArray(new MatrixCell[0]);
	}

	private static final String _FUNNEL_STAGE_VOCABULARY_ERC =
		"L_CMP_FUNNEL_STAGE";

	private static final String _NO_FUNNEL_STAGE_ID = "no-funnel-stage";

	private static final String _NO_PERSONA_ID = "no-persona";

	private static final String _PERSONAS_VOCABULARY_ERC = "L_CMP_PERSONAS";

	private static final String _PROJECT_TO_TASKS_RELATIONSHIP_ERC =
		"L_CMP_PROJECT_TO_L_CMP_TASKS";

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}
