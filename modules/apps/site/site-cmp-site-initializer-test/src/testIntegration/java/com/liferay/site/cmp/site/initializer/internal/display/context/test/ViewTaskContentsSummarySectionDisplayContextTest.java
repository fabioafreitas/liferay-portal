/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.site.cmp.site.initializer.util.SiteInitializerUtil;

import jakarta.servlet.http.HttpServletRequest;

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
public class ViewTaskContentsSummarySectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		SiteInitializerUtil.processBatchEngineUnits(
			ViewTaskContentsSummarySectionDisplayContextTest.class);

		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());

		_themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()));
				setSiteDefaultLocale(LocaleUtil.US);
				setUser(TestPropsValues.getUser());
			}
		};
	}

	@Test
	public void testGetAPIURL() throws Exception {
		AssetLibraryResource.Builder builder =
			_assetLibraryResourceFactory.create();

		AssetLibraryResource assetLibraryResource = builder.user(
			_themeDisplay.getUser()
		).build();

		AssetLibrary assetLibrary = assetLibraryResource.postAssetLibrary(
			new AssetLibrary() {
				{
					setName(StringUtil::randomString);
					setSettings(
						() -> new Settings() {
							{
								setLogoColor(() -> "outline-0");
								setTrashEnabled(() -> false);
							}
						});
					setType(() -> Type.PROJECT);
				}
			});

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getCompanyId(),
				_objectDefinition.getStorageType());

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				_themeDisplay.getSiteDefaultLocale(), null,
				_themeDisplay.getUser());

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			defaultDTOConverterContext, _objectDefinition,
			new ObjectEntry() {
				{
					setStatus(
						() -> new Status() {
							{
								setCode(() -> WorkflowConstants.STATUS_DRAFT);
							}
						});
				}
			},
			String.valueOf(assetLibrary.getSiteId()));

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			_objectDefinition.getClassName(), objectEntry.getId());

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		Object displayContext =
			_getViewTaskContentsSummarySectionDisplayContext(
				httpServletRequest);

		ReflectionTestUtil.setFieldValue(
			displayContext, "_assetEntry", assetEntry);

		String apiURL = ReflectionTestUtil.invoke(
			displayContext, "getAPIURL", new Class<?>[0]);

		Assert.assertTrue(
			apiURL.contains(" and status eq " + WorkflowConstants.STATUS_ANY));

		String[] keywords = {
			StringUtil.randomString(), StringUtil.randomString()
		};

		objectEntry.setKeywords(keywords);

		objectEntryManager.partialUpdateObjectEntry(
			_themeDisplay.getCompanyId(), defaultDTOConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition,
			objectEntry, String.valueOf(assetLibrary.getSiteId()));

		apiURL = ReflectionTestUtil.invoke(
			displayContext, "getAPIURL", new Class<?>[0]);

		Assert.assertTrue(
			apiURL.contains(
				StringBundler.concat(
					" and keywords/any(k:k in ('",
					StringUtil.merge(keywords, "','"), "'))")));
	}

	private Object _getViewTaskContentsSummarySectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object viewTaskContentsSummarySectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cmp.site.initializer.internal.display." +
					"context.ViewTaskContentsSummarySectionDisplayContext");

		Assert.assertNotNull(viewTaskContentsSummarySectionDisplayContext);

		return viewTaskContentsSummarySectionDisplayContext;
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewTaskContentsSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	private ThemeDisplay _themeDisplay;

}