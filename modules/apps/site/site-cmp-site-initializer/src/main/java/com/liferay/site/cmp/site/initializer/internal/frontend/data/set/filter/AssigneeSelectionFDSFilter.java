/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Fábio Alves
 */
public class AssigneeSelectionFDSFilter extends BaseSelectionFDSFilter {

	public AssigneeSelectionFDSFilter(
		ClassNameLocalService classNameLocalService,
		RoleLocalService roleLocalService, UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "cmpAssignTo";
	}

	@Override
	public String getLabel() {
		return "assignee";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		List<SelectionFDSFilterItem> items = new ArrayList<>();

		long userClassNameId = _classNameLocalService.getClassNameId(
			User.class.getName());

		for (User user :
				_userLocalService.getCompanyUsers(
					CompanyThreadLocal.getCompanyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)) {

			items.add(
				new SelectionFDSFilterItem(
					user.getFullName(),
					_getAssigneeKeyValue(userClassNameId, user.getUserId())));
		}

		long roleClassNameId = _classNameLocalService.getClassNameId(
			Role.class.getName());

		for (Role role :
				_roleLocalService.getRoles(CompanyThreadLocal.getCompanyId())) {

			items.add(
				new SelectionFDSFilterItem(
					role.getName(),
					_getAssigneeKeyValue(roleClassNameId, role.getRoleId())));
		}

		return items;
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	private String _getAssigneeKeyValue(long classNameId, long classPK) {
		return StringBundler.concat(classNameId, StringPool.UNDERLINE, classPK);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;

}