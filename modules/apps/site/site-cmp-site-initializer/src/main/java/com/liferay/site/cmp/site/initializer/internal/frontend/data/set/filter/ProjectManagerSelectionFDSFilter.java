/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;
import java.util.Locale;

/**
 * @author José Abelenda
 */
public class ProjectManagerSelectionFDSFilter
	extends BaseUserSelectionFDSFilter {

	public ProjectManagerSelectionFDSFilter(
		List<Long> groupIds, UserLocalService userLocalService) {

		super(groupIds, userLocalService);
	}

	@Override
	public String getId() {
		return "cmpProjectManagerUserId";
	}

	@Override
	public String getLabel() {
		return "manager";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return getSelectionFDSFilterItems();
	}

}