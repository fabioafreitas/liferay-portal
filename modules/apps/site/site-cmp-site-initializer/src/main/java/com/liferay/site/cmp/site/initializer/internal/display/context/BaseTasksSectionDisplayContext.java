/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.cmp.site.initializer.internal.constants.CMPActionConstants;
import com.liferay.site.cmp.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Fábio Alves
 */
public abstract class BaseTasksSectionDisplayContext
	extends BaseSectionDisplayContext {

	public BaseTasksSectionDisplayContext(
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

		super(httpServletRequest, taskObjectDefinition, objectEntryService);

		this.assetTagLocalService = assetTagLocalService;
		this.classNameLocalService = classNameLocalService;
		this.depotEntryLocalService = depotEntryLocalService;
		this.listTypeEntryLocalService = listTypeEntryLocalService;
		this.objectFieldLocalService = objectFieldLocalService;
		this.objectStateFlowLocalService = objectStateFlowLocalService;
		this.objectStateLocalService = objectStateLocalService;
		this.projectObjectDefinition = projectObjectDefinition;
		this.roleService = roleService;

		sectionDisplayContextHelper = new SectionDisplayContextHelper(
			httpServletRequest, objectDefinition, themeDisplay);
	}

	public Map<String, Object> getAdditionalProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"projectId",
			() -> {
				if (assetEntry == null) {
					return null;
				}

				return assetEntry.getClassPK();
			}
		).put(
			"states",
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				ObjectField objectField =
					objectFieldLocalService.fetchObjectField(
						objectDefinition.getObjectDefinitionId(), "state");

				for (ListTypeEntry listTypeEntry :
						listTypeEntryLocalService.getListTypeEntries(
							objectField.getListTypeDefinitionId())) {

					jsonArray.put(
						JSONUtil.put(
							"key", listTypeEntry.getKey()
						).put(
							"name",
							listTypeEntry.getName(themeDisplay.getLocale())
						).put(
							"nextStates",
							_getNextStatesJSONArray(listTypeEntry, objectField)
						));
				}

				return jsonArray;
			}
		).build();
	}

	@Override
	public CreationMenu getCreationMenu() throws Exception {
		if (!hasAddObjectEntryPortletResourcePermission()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", CMPActionConstants.CREATE_TASK);
				dropdownItem.putData(
					"addProjectURL",
					StringBundler.concat(
						ActionUtil.getAddProjectURL(
							projectObjectDefinition, themeDisplay),
						"&action=",
						CMPActionConstants.CREATE_PROJECT_GLOBAL_TASK));
				dropdownItem.putData(
					"addTaskURL",
					StringBundler.concat(
						ActionUtil.getAddTaskURL(
							0, objectDefinition, 0, themeDisplay),
						"&action=", CMPActionConstants.CREATE_GLOBAL_TASK));
				dropdownItem.putData(
					"objectDefinitionId",
					String.valueOf(objectDefinition.getObjectDefinitionId()));

				if (assetEntry != null) {
					dropdownItem.putData(
						"redirect",
						ActionUtil.getAddTaskURL(
							assetEntry.getGroupId(), objectDefinition,
							assetEntry.getClassPK(), themeDisplay));
				}

				dropdownItem.putData(
					"title",
					objectDefinition.getLabel(themeDisplay.getLocale()));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						(assetEntry == null) ? "new" : "new-task"));
			}
		).build();
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-task")
		).put(
			"image", "/states/cmp_empty_state_tasks.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-tasks-yet")
		).build();
	}

	@Override
	public abstract List<FDSActionDropdownItem> getFDSActionDropdownItems();

	public abstract List<FDSFilter> getFDSFilters();

	protected final AssetTagLocalService assetTagLocalService;
	protected final ClassNameLocalService classNameLocalService;
	protected final DepotEntryLocalService depotEntryLocalService;
	protected final ListTypeEntryLocalService listTypeEntryLocalService;
	protected final ObjectFieldLocalService objectFieldLocalService;
	protected final ObjectStateFlowLocalService objectStateFlowLocalService;
	protected final ObjectStateLocalService objectStateLocalService;
	protected final ObjectDefinition projectObjectDefinition;
	protected final RoleService roleService;
	protected final SectionDisplayContextHelper sectionDisplayContextHelper;

	private JSONArray _getNextStatesJSONArray(
		ListTypeEntry currentListTypeEntry, ObjectField objectField) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		ObjectStateFlow objectStateFlow =
			objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		ObjectState objectState =
			objectStateLocalService.fetchObjectStateFlowObjectState(
				currentListTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		for (ObjectState nextObjectState :
				objectStateLocalService.getNextObjectStates(
					objectState.getObjectStateId())) {

			ListTypeEntry nextListTypeEntry =
				listTypeEntryLocalService.fetchListTypeEntry(
					nextObjectState.getListTypeEntryId());

			jsonArray.put(nextListTypeEntry.getKey());
		}

		return jsonArray;
	}

}