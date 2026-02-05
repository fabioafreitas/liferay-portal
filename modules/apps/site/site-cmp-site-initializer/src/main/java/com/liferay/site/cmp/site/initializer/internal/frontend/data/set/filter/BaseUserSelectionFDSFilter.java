/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Pedro Leite
 */
public abstract class BaseUserSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	protected BaseUserSelectionFDSFilter(
		List<Long> groupIds, UserLocalService userLocalService) {

		_groupIds = groupIds;
		_userLocalService = userLocalService;
	}

	protected List<SelectionFDSFilterItem> getSelectionFDSFilterItems() {
		return TransformUtil.transform(
			getUsers(),
			user -> new SelectionFDSFilterItem(
				user.getFullName(), user.getUserId()));
	}

	protected List<User> getUsers() {
		if (_groupIds.isEmpty()) {
			return new ArrayList<>();
		}

		Set<User> users = new TreeSet<>(
			Comparator.comparing(User::getFullName));

		users.addAll(
			_userLocalService.searchBySocial(
				CompanyThreadLocal.getCompanyId(),
				ListUtil.toLongArray(_groupIds, Long::longValue), null, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		return ListUtil.fromCollection(users);
	}

	private final List<Long> _groupIds;
	private final UserLocalService _userLocalService;

}