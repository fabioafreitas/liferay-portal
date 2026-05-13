/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchResultPermissionFilterFactory;
import com.liferay.portal.kernel.search.hits.HitsProcessorRegistry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.indexer.IndexerPermissionPostFilter;
import com.liferay.portal.search.indexer.IndexerQueryBuilder;
import com.liferay.portal.search.internal.searcher.helper.IndexSearcherHelper;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Fábio Alves
 */
public class IndexerSearcherImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_indexerPermissionPostFilter = Mockito.mock(
			IndexerPermissionPostFilter.class);
		_modelSearchSettings = Mockito.mock(ModelSearchSettings.class);

		_indexerSearcherImpl = new IndexerSearcherImpl<>(
			_modelSearchSettings, Collections.emptyList(),
			_indexerPermissionPostFilter,
			Mockito.mock(IndexerQueryBuilder.class),
			Mockito.mock(HitsProcessorRegistry.class),
			Mockito.mock(IndexSearcherHelper.class), Collections.emptyList(),
			Mockito.mock(SearchResultPermissionFilterFactory.class));
	}

	@Test
	public void testIsUseSearchResultPermissionFilter() throws Exception {

		// Case 1: post-filter not permission-aware -> false regardless of
		// model or attribute.

		_setPostFilterPermissionAware(false);
		_setModelPermissionFilterSuppressed(false);
		_setModelPermissionAware(true);

		SearchContext searchContextWithAttribute = new SearchContext();

		searchContextWithAttribute.setAttribute(
			_FORCE_PERMISSION_FILTER_ATTRIBUTE, Boolean.TRUE);

		Assert.assertFalse(
			"Post-filter not permission-aware should win",
			_invoke(searchContextWithAttribute));

		// Case 2: model declares result-permission filter suppressed ->
		// false regardless of attribute (hard opt-out wins, as commerce
		// models rely on).

		_setPostFilterPermissionAware(true);
		_setModelPermissionFilterSuppressed(true);
		_setModelPermissionAware(true);

		Assert.assertFalse(
			"isSearchResultPermissionFilterSuppressed=true should win",
			_invoke(searchContextWithAttribute));

		// Case 3: model is permission-aware -> true regardless of
		// attribute (existing default path).

		_setPostFilterPermissionAware(true);
		_setModelPermissionFilterSuppressed(false);
		_setModelPermissionAware(true);

		Assert.assertTrue(
			"isPermissionAware=true should enable the filter",
			_invoke(new SearchContext()));

		// Case 4: model is NOT permission-aware AND attribute is unset
		// -> false (the legacy bypass that LPS-197317 relies on for
		// non-/o/search callers).

		_setPostFilterPermissionAware(true);
		_setModelPermissionFilterSuppressed(false);
		_setModelPermissionAware(false);

		Assert.assertFalse(
			"No model opt-in and no attribute should disable the filter",
			_invoke(new SearchContext()));

		// Case 5: model is NOT permission-aware AND attribute is true
		// -> true (the new path c235959 added for /o/search?nestedFields=embedded).

		_setPostFilterPermissionAware(true);
		_setModelPermissionFilterSuppressed(false);
		_setModelPermissionAware(false);

		Assert.assertTrue(
			"Force attribute should enable the filter when the model " +
				"opted out",
			_invoke(searchContextWithAttribute));
	}

	private boolean _invoke(SearchContext searchContext) {
		return (boolean)ReflectionTestUtil.invoke(
			_indexerSearcherImpl, "_isUseSearchResultPermissionFilter",
			new Class<?>[] {SearchContext.class}, searchContext);
	}

	private void _setModelPermissionAware(boolean value) {
		Mockito.doReturn(
			value
		).when(
			_modelSearchSettings
		).isPermissionAware();
	}

	private void _setModelPermissionFilterSuppressed(boolean value) {
		Mockito.doReturn(
			value
		).when(
			_modelSearchSettings
		).isSearchResultPermissionFilterSuppressed();
	}

	private void _setPostFilterPermissionAware(boolean value) {
		Mockito.doReturn(
			value
		).when(
			_indexerPermissionPostFilter
		).isPermissionAware();
	}

	private static final String _FORCE_PERMISSION_FILTER_ATTRIBUTE =
		"search.permission.filter.forced";

	private IndexerPermissionPostFilter _indexerPermissionPostFilter;
	private IndexerSearcherImpl<?> _indexerSearcherImpl;
	private ModelSearchSettings _modelSearchSettings;

}
