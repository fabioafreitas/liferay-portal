/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Fábio Alves
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
@Sync
public class ViewProjectTasksSectionDisplayContextTest
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
	public void testGetAPIURL() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				"(objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				")&nestedFields=cmpProjectToCMPTasks,embedded"),
			getAPIURL(null));

		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				"(objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				" and scopeGroupId eq ", _assetEntry.getGroupId(),
				")&nestedFields=cmpProjectToCMPTasks,embedded"),
			getAPIURL(_assetEntry));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems(_assetEntry);

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 6,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit", "Edit", "get",
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLink", "View", null,
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-on", "subscribe", "Watch Task", "post",
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-off", "unsubscribe", "Stop Watching Task", "post",
			fdsActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assign-to", "Assign to...", "get",
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null,
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(5));
	}

	@Test
	public void testGetTasksQuickFiltersProperties() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.LAYOUT_ASSET_ENTRY, _assetEntry);
		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		Object displayContext = getSectionDisplayContext(httpServletRequest);

		Map<String, Object> tasksQuickFiltersProperties =
			ReflectionTestUtil.invoke(
				displayContext, "getTasksQuickFiltersProperties",
				new Class<?>[0]);

		Assert.assertEquals(
			_assetEntry.getClassPK(),
			tasksQuickFiltersProperties.get("projectId"));
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
