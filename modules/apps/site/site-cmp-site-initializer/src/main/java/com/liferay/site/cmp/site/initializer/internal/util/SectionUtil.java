/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pedro Leite
 */
public class SectionUtil {

	public static String getScopeGroupIdFilterString() {
		List<Long> groupIds = new ArrayList<>();

		for (long groupId :
				DepotEntryLocalServiceUtil.getDepotEntryGroupIds(
					CompanyThreadLocal.getCompanyId(),
					DepotConstants.TYPE_PROJECT)) {

			if (_isAssetLibraryAdminOrAssetLibraryMember(groupId)) {
				groupIds.add(groupId);
			}
		}

		if (groupIds.isEmpty()) {
			return " and (scopeGroupId in (-1))";
		}

		return " and (scopeGroupId in (" +
			ListUtil.toString(groupIds, StringPool.BLANK, StringPool.COMMA) +
				"))";
	}

	private static boolean _isAssetLibraryAdminOrAssetLibraryMember(
		long groupId) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId)) {
			return true;
		}

		return GroupLocalServiceUtil.hasUserGroup(
			PrincipalThreadLocal.getUserId(), groupId);
	}

}