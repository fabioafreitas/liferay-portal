/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Before;

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
		themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.fetchCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.US);
				setScopeGroupId(TestPropsValues.getGroupId());
			}
		};

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		projectObjectEntry = CMPTestUtil.addProjectObjectEntry();
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

}