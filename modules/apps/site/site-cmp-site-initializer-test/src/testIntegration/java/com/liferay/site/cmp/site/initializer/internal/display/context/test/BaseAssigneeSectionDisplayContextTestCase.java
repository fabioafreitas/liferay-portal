/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.Before;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
public abstract class BaseAssigneeSectionDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			BaseAssigneeSectionDisplayContextTestCase.class);

		httpServletRequest = new MockHttpServletRequest();

		projectObjectEntry = CMPTestUtil.addProjectObjectEntry();

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, projectObjectEntry);

		themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.fetchCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.US);
				setPathImage(_portal.getPathImage());
				setScopeGroupId(TestPropsValues.getGroupId());
			}
		};

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
	}

	protected void assertAssigneeFieldValue(
			String expectedExternalReferenceCode, String expectedName,
			String expectedPortrait, String objectFieldName, long userId)
		throws Exception {

		projectObjectEntry = objectEntryLocalService.partialUpdateObjectEntry(
			projectObjectEntry.getUserId(),
			projectObjectEntry.getObjectEntryId(),
			projectObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, userId
			).build(),
			ServiceContextTestUtil.getServiceContext());

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, projectObjectEntry);

		JSONObject jsonObject = jsonFactory.createJSONObject(
			jsonFactory.looseSerializeDeep(getProperties()));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", expectedExternalReferenceCode
			).put(
				"id", userId
			).put(
				"name", expectedName
			).put(
				"portrait", expectedPortrait
			).put(
				"type", "user"
			).toString(),
			String.valueOf(jsonObject.getJSONObject("value")), true);
	}

	protected Map<String, Object> getProperties() throws Exception {
		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(httpServletRequest), "getProperties",
			new Class<?>[0]);
	}

	protected abstract Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception;

	protected HttpServletRequest httpServletRequest;

	@Inject
	protected JSONFactory jsonFactory;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	protected ObjectEntry projectObjectEntry;
	protected ThemeDisplay themeDisplay;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Portal _portal;

}