/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
@Sync
public class ViewProjectSponsorAssigneeSectionDisplayContextTest
	extends BaseAssigneeSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProperties() throws Exception {
		Map<String, Object> properties = getProperties();

		Assert.assertEquals("Sponsor", properties.get("label"));
		Assert.assertEquals(
			"ObjectField_r_userToCMPProjectSponsor_userId",
			properties.get("name"));
		Assert.assertEquals(
			"/o/headless-cmp/v1.0/task-assignees/",
			properties.get("searchURL"));
		Assert.assertTrue((Boolean)properties.get("userOnly"));
		Assert.assertTrue((Boolean)properties.get("visible"));

		User user = _userLocalService.getUser(projectObjectEntry.getUserId());

		assertAssigneeFieldValue(
			user.getExternalReferenceCode(), user.getFullName(),
			user.getPortraitURL(themeDisplay),
			"r_userToCMPProjectSponsor_userId", user.getUserId());
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectSponsorAssigneeSectionDisplayContext");
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectSponsorAssigneeJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private UserLocalService _userLocalService;

}