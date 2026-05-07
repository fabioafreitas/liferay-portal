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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.AssigneeSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.CreateDateFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.DueDateRangeFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.ProjectSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.StateSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.TagSelectionFDSFilter;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Fábio Alves
 */
public class ViewAllTasksSectionDisplayContext
	extends BaseTasksSectionDisplayContext {

	public ViewAllTasksSectionDisplayContext(
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
		StringBundler sb = new StringBundler(9);

		sb.append("/o/search/v1.0/search?emptySearch=true&entryClassNames=");
		sb.append(HtmlUtil.escapeURL(objectDefinition.getClassName()));
		sb.append(StringPool.COMMA);
		sb.append(KaleoTaskInstanceToken.class.getName());
		sb.append("&filter=(objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append(" or keywords/any(k:startswith(k, '");
		sb.append(objectDefinition.getExternalReferenceCode());
		sb.append("')))&nestedFields=cmpProjectToCMPTasks,embedded");

		return sb.toString();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				FDSActionDropdownItemList.of(
					FDSActionDropdownItemBuilder.setTarget(
						"modal-workflow-transition"
					).build(
						"workflow-transition"
					))
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"workflow-transitions"
			),
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				ListUtil.concat(
					sectionDisplayContextHelper.
						getProjectTasksFDSActionDropdownItems(
							objectDefinition.getClassName()),
					sectionDisplayContextHelper.
						getWorkflowTasksFDSActionDropdownItems())
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"other-actions"
			));
	}

	@Override
	public List<FDSFilter> getFDSFilters() {
		return ListUtil.fromArray(
			new AssigneeSelectionFDSFilter(
				classNameLocalService, projectObjectDefinition.getCompanyId(),
				roleService),
			new CreateDateFDSFilter(), new DueDateRangeFDSFilter(),
			new ProjectSelectionFDSFilter(projectObjectDefinition),
			new StateSelectionFDSFilter(),
			new TagSelectionFDSFilter(
				assetTagLocalService, depotEntryLocalService, assetEntry,
				projectObjectDefinition));
	}

}