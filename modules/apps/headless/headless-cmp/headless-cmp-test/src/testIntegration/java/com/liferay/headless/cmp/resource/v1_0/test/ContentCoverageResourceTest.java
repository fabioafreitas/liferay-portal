/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cmp.client.dto.v1_0.Cell;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverage;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fábio Alves
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class ContentCoverageResourceTest
	extends BaseContentCoverageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = CMPTestUtil.getOrAddGroup(ContentCoverageResourceTest.class);

		_projectObjectEntry = CMPTestUtil.addProjectObjectEntry();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_objectEntryLocalService.deleteObjectEntry(_projectObjectEntry);
	}

	@Override
	@Test
	public void testGetProjectContentCoverage() throws Exception {
		AssetCategory assetCategory1 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					"L_CMP_FUNNEL_STAGE_AWARENESS", _group.getGroupId());
		AssetCategory assetCategory2 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					"L_CMP_PERSONAS_CHAMPION", _group.getGroupId());

		long[] assetCategoryIds = {
			assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
		};

		_partialUpdateObjectEntry(
			assetCategoryIds, new String[0], _projectObjectEntry);

		String assetTagName = "L_CMP_TASK_" + RandomTestUtil.randomString(10);

		_partialUpdateObjectEntry(
			new long[0], new String[] {assetTagName},
			CMPTestUtil.addTaskObjectEntry(_projectObjectEntry));

		_testGetProjectContentCoverage(_toContentCoverage(0L));

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry.getGroupId(), _depotEntry.getCompanyId());

		_partialUpdateObjectEntry(
			assetCategoryIds, new String[] {assetTagName},
			_addObjectEntry(objectDefinition, objectEntryFolder));
		_partialUpdateObjectEntry(
			new long[0], new String[] {assetTagName},
			_addObjectEntry(objectDefinition, objectEntryFolder));

		_testGetProjectContentCoverage(
			_toContentCoverage(
				2L, _toCell("-1", "-1", 1L),
				_toCell(
					String.valueOf(assetCategory2.getCategoryId()),
					String.valueOf(assetCategory1.getCategoryId()), 1L)));
	}

	@Override
	@Test
	public void testGraphQLGetProjectContentCoverage() {
	}

	@Override
	@Test
	public void testGraphQLGetProjectContentCoverageNotFound() {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"cells", "totalAssetCount"};
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				(Serializable)HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));
	}

	private void _partialUpdateObjectEntry(
			long[] assetCategoryIds, String[] assetTagNames,
			ObjectEntry objectEntry)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(objectEntry.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetTagNames(assetTagNames);

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), new HashMap<>(),
			serviceContext);
	}

	private void _testGetProjectContentCoverage(
			ContentCoverage expectedContentCoverage)
		throws Exception {

		assertEquals(
			expectedContentCoverage,
			contentCoverageResource.getProjectContentCoverage(
				_projectObjectEntry.getObjectEntryId()));
	}

	private Cell _toCell(
		String personaId, String funnelStageId, long totalCount) {

		Cell cell = new Cell();

		cell.setFunnelStageId(funnelStageId);
		cell.setPersonaId(personaId);
		cell.setTotalCount(totalCount);

		return cell;
	}

	private ContentCoverage _toContentCoverage(
		long totalAssetCount, Cell... cells) {

		ContentCoverage contentCoverage = new ContentCoverage();

		contentCoverage.setCells(cells);
		contentCoverage.setTotalAssetCount(totalAssetCount);

		return contentCoverage;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectEntry _projectObjectEntry;

}