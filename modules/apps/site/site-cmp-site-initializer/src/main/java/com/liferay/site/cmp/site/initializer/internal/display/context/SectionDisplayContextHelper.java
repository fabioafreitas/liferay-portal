/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.site.cmp.site.initializer.internal.util.ActionUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Fábio Alves
 */
public class SectionDisplayContextHelper {

	public SectionDisplayContextHelper(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition, ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;
		_themeDisplay = themeDisplay;
	}

	public List<FDSActionDropdownItem> getWorkflowTasksFDSActionDropdownItems() {
		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						PortletKeys.MY_WORKFLOW_TASK,
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_workflow_task.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"workflowTaskId", "{embedded.id}"
				).buildString()
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"entryClassName",
					KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"actionLinkWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-to-me")
			).setPermissionKey(
				"assignToMe"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", false
				).put(
					"embedded.completed", false
				).put(
					"entryClassName",
					KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"assignToMeWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-to-...")
			).setPermissionKey(
				"assignToUser"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName",
					KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"assignToWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "update-due-date")
			).setPermissionKey(
				"updateDueDate"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName",
					KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"updateDueDateWorkflowTask"
			));
	}

	public List<FDSActionDropdownItem> getProjectTasksFDSActionDropdownItems(String entryClassName) {
		Map<String, Object> visibilityFilters = HashMapBuilder.<String, Object>put(
			"entryClassName", entryClassName
		).build();

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					ActionUtil.getBaseEditTaskURL(
						_objectDefinition, _themeDisplay),
					"{embedded.id}?redirect=", _themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					ActionUtil.getBaseViewTaskURL(
						_objectDefinition, _themeDisplay),
					"{embedded.id}?redirect=", _themeDisplay.getURLCurrent())
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"actionLink"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					"/o", _objectDefinition.getRESTContextPath(),
					"/scopes/{embedded.scopeId}/by-external-reference-code",
					"/{embedded.externalReferenceCode}/subscribe")
			).setIcon(
				"bell-on"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "watch-task")
			).setMethod(
				"post"
			).setPermissionKey(
				"subscribe"
			).setTarget(
				"async"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"subscribe"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					"/o", _objectDefinition.getRESTContextPath(),
					"/scopes/{embedded.scopeId}/by-external-reference-code",
					"/{embedded.externalReferenceCode}/unsubscribe")
			).setIcon(
				"bell-off"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "stop-watching-task")
			).setMethod(
				"post"
			).setPermissionKey(
				"unsubscribe"
			).setTarget(
				"async"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"unsubscribe"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-to-...")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"assign-to"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "delete")
			).setPermissionKey(
				"delete"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"delete"
			));
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ThemeDisplay _themeDisplay;

}
