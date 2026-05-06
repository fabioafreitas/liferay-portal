/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.AssigneeSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.CreateDateFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.DueDateRangeFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.ProjectSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.StateSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.TagSelectionFDSFilter;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ViewProjectTasksSectionDisplayContext
	extends BaseTasksSectionDisplayContext {

	public ViewProjectTasksSectionDisplayContext(
		AssetTagLocalService assetTagLocalService,
		ClassNameLocalService classNameLocalService,
		DepotEntryLocalService depotEntryLocalService,
		HttpServletRequest httpServletRequest,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectStateFlowLocalService objectStateFlowLocalService,
		ObjectStateLocalService objectStateLocalService,
		ObjectDefinition projectObjectDefinition, RoleService roleService,
		ObjectDefinition taskObjectDefinition) {

		super(
			assetTagLocalService, classNameLocalService, depotEntryLocalService,
			httpServletRequest, listTypeEntryLocalService, objectEntryService,
			objectFieldLocalService, objectStateFlowLocalService,
			objectStateLocalService, projectObjectDefinition, roleService,
			taskObjectDefinition);
	}

	@Override
	public String getAPIURL() {
		StringBundler sb = new StringBundler(6);

		sb.append("/o/search/v1.0/search?emptySearch=true");
		sb.append("&filter=(objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());

		if (assetEntry != null) {
			sb.append(" and scopeGroupId eq ");
			sb.append(assetEntry.getGroupId());
		}

		sb.append(")&nestedFields=cmpProjectToCMPTasks,embedded");

		return sb.toString();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return sectionDisplayContextHelper.
			getProjectTasksFDSActionDropdownItems(objectDefinition.getClassName());
	}

	@Override
	public List<FDSFilter> getFDSFilters() {
		List<FDSFilter> fdsFilters = new ArrayList<>();

		fdsFilters.add(
			new AssigneeSelectionFDSFilter(
				classNameLocalService, projectObjectDefinition.getCompanyId(),
				roleService));

		fdsFilters.add(new CreateDateFDSFilter());
		fdsFilters.add(new DueDateRangeFDSFilter());

		if (assetEntry == null) {
			fdsFilters.add(
				new ProjectSelectionFDSFilter(projectObjectDefinition));
		}

		fdsFilters.add(new StateSelectionFDSFilter());
		fdsFilters.add(
			new TagSelectionFDSFilter(
				assetTagLocalService, depotEntryLocalService, assetEntry,
				projectObjectDefinition));

		return fdsFilters;
	}

	public Map<String, Object> getTasksQuickFiltersProperties() {
		return HashMapBuilder.<String, Object>put(
			"projectId",
			() -> {
				if (assetEntry == null) {
					return null;
				}

				return assetEntry.getClassPK();
			}
		).build();
	}

}