/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

/**
 * @author Pedro Leite
 */
public class SectionUtil {

	public static String getScopeGroupIdFilterString(List<Long> groupIds) {
		if (groupIds.isEmpty()) {
			return " and (scopeGroupId in (-1))";
		}

		return " and (scopeGroupId in (" +
			ListUtil.toString(groupIds, StringPool.BLANK, StringPool.COMMA) +
				"))";
	}

}