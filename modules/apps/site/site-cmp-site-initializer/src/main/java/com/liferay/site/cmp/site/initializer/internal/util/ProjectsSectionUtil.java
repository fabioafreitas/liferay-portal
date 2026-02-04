/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;

import java.util.List;

/**
 * @author Pedro Leite
 */
public class ProjectsSectionUtil {

	public static String getAPIURL(
		List<Long> groupIds, ObjectDefinition objectDefinition) {

		StringBundler sb = new StringBundler(5);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append(SectionUtil.getScopeGroupIdFilterString(groupIds));
		sb.append("&nestedFields=embedded");

		return sb.toString();
	}

}