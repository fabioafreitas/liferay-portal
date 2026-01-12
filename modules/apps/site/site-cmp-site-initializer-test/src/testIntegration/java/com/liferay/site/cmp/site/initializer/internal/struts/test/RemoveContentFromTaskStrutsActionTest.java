/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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
public class RemoveContentFromTaskStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		SiteInitializerUtil.processBatchEngineUnits(
			RemoveContentFromTaskStrutsActionTest.class);

		_objectDefinition1 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());

		_objectDefinition2 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BLOG", TestPropsValues.getCompanyId());
	}

	@Test
	public void testExecute() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()));
				setSiteDefaultLocale(LocaleUtil.US);
				setUser(TestPropsValues.getUser());
			}
		};

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		AssetLibraryResource.Builder builder =
			_assetLibraryResourceFactory.create();

		AssetLibraryResource assetLibraryResource = builder.user(
			themeDisplay.getUser()
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

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				themeDisplay.getSiteDefaultLocale(), null,
				themeDisplay.getUser());

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition1.getCompanyId(),
				_objectDefinition1.getStorageType());

		String scopeKey = String.valueOf(assetLibrary.getSiteId());

		String keyword1 = StringUtil.randomString();

		ObjectEntry objectEntry1 = objectEntryManager.addObjectEntry(
			defaultDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					setKeywords(() -> new String[] {keyword1});
					setStatus(
						() -> new Status() {
							{
								setCode(() -> WorkflowConstants.STATUS_DRAFT);
							}
						});
				}
			},
			scopeKey);

		String keyword2 = StringUtil.randomString();

		ObjectEntry objectEntry2 = objectEntryManager.addObjectEntry(
			defaultDTOConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					setKeywords(() -> new String[] {keyword1, keyword2});
					setStatus(
						() -> new Status() {
							{
								setCode(() -> WorkflowConstants.STATUS_DRAFT);
							}
						});
				}
			},
			scopeKey);

		mockHttpServletRequest.setParameter(
			"keywords", StringUtil.merge(objectEntry1.getKeywords(), ","));

		mockHttpServletRequest.setParameter(
			"objectDefinitionExternalReferenceCode",
			String.valueOf(_objectDefinition2.getExternalReferenceCode()));

		mockHttpServletRequest.setParameter(
			"objectEntryExternalReferenceCode",
			String.valueOf(objectEntry2.getExternalReferenceCode()));

		mockHttpServletRequest.setParameter(
			"objectEntryId", String.valueOf(objectEntry1.getId()));

		mockHttpServletRequest.setParameter("scopeKey", scopeKey);

		_removeContentFromTaskStrutsAction.execute(
			mockHttpServletRequest, new MockHttpServletResponse());

		objectEntry2 = objectEntryManager.getObjectEntry(
			TestPropsValues.getCompanyId(), defaultDTOConverterContext,
			objectEntry2.getExternalReferenceCode(), _objectDefinition2,
			scopeKey);

		Assert.assertArrayEquals(
			new String[] {keyword2}, objectEntry2.getKeywords());
	}

	@Inject
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Inject(filter = "path=/cms/remove_content_from_task")
	private StrutsAction _removeContentFromTaskStrutsAction;

}