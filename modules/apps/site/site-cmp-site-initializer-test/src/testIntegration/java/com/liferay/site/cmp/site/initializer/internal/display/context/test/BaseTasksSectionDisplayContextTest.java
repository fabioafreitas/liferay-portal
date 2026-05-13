/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Fábio Alves
 *
 * Covers methods declared on the abstract BaseTasksSectionDisplayContext that
 * are not overridden by the concrete subclasses. Uses
 * ViewProjectTasksJSPSectionFragmentRenderer as the harness; the assertions
 * exercise the base implementation, not the project-tasks specialization.
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
@Sync
public class BaseTasksSectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		ObjectDefinition projectObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		ObjectEntry projectObjectEntry = CMPTestUtil.addProjectObjectEntry();

		projectObjectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), projectObjectEntry.getObjectEntryId(),
			projectObjectEntry.getObjectEntryFolderId(),
			projectObjectEntry.getValues(),
			ServiceContextTestUtil.getServiceContext());

		_assetEntry = _assetEntryLocalService.getEntry(
			projectObjectDefinition.getClassName(),
			projectObjectEntry.getObjectEntryId());
	}

	@Test
	public void testGetAdditionalProps() throws Exception {
		Map<String, Object> additionalProps = getAdditionalProps(_assetEntry);

		Assert.assertEquals(
			_assetEntry.getClassPK(), additionalProps.get("projectId"));
		Assert.assertNotNull(additionalProps.get("states"));

		additionalProps = getAdditionalProps(null);

		Assert.assertNull(additionalProps.get("projectId"));
		Assert.assertNotNull(additionalProps.get("states"));
	}

	@Test
	public void testGetEmptyState() throws Exception {
		Map<String, Object> emptyState = getEmptyState(null);

		Assert.assertEquals(
			"Click 'New' to create your first task.",
			emptyState.get("description"));
		Assert.assertEquals(
			"/states/cmp_empty_state_tasks.svg", emptyState.get("image"));
		Assert.assertEquals("No tasks yet", emptyState.get("title"));
	}

	@Test
	public void testGetFDSFiltersWithAssetEntryOmitsProjectFilter()
		throws Exception {

		List<FDSFilter> fdsFiltersWithoutAsset = getFDSFilters(null);

		Assert.assertEquals(
			fdsFiltersWithoutAsset.toString(), 6,
			fdsFiltersWithoutAsset.size());

		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "cmpTaskCMPProjectId", "project",
			fdsFiltersWithoutAsset.get(3));

		List<FDSFilter> fdsFiltersWithAsset = getFDSFilters(_assetEntry);

		Assert.assertEquals(
			fdsFiltersWithAsset.toString(), 5, fdsFiltersWithAsset.size());

		for (FDSFilter fdsFilter : fdsFiltersWithAsset) {
			Assert.assertNotEquals("project", fdsFilter.getId());
		}
	}

	@Override
	protected String getObjectDefinitionExternalReferenceCode() {
		return "L_CMP_TASK";
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectTasksSectionDisplayContext");
	}

	private AssetEntry _assetEntry;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectTasksJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}
