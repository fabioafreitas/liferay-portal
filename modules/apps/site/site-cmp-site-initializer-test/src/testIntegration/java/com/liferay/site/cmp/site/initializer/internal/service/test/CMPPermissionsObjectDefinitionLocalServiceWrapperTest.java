/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class CMPPermissionsObjectDefinitionLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			CMPPermissionsObjectDefinitionLocalServiceWrapperTest.class);
	}

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				"L_CMP_PROJECT_MANAGEMENT_DEFINITIONS",
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(), null, null, false, true,
				false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "Test", null, null, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				WorkflowConstants.STATUS_DRAFT, Collections.emptyList(),
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		objectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		Role role = _getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.DELETE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.VIEW));

		role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.GUEST);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));

		role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private Role _getOrAddCMSAdministratorRole(long companyId, long userId)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (role != null) {
			return role;
		}

		return _roleLocalService.addRole(
			null, userId, null, 0, RoleConstants.CMS_ADMINISTRATOR, null, null,
			RoleConstants.TYPE_REGULAR, null, null);
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}