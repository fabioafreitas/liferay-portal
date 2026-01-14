/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cmp.site.initializer.internal.constants.CMPSiteInitializerFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = "frontend.data.set.name=" + CMPSiteInitializerFDSNames.CMP_TASK,
	service = FDSFilter.class
)
public class TaskStateSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "state";
	}

	@Override
	public String getLabel() {
		return "state";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem(
				_language.get(locale, "not-started"), "notStarted"),
			new SelectionFDSFilterItem(
				_language.get(locale, "in-progress"), "inProgress"),
			new SelectionFDSFilterItem(
				_language.get(locale, "blocked"), "blocked"),
			new SelectionFDSFilterItem(
				_language.get(locale, "overdue"), "overdue"),
			new SelectionFDSFilterItem(_language.get(locale, "done"), "done"));
	}

	@Reference
	private Language _language;

}