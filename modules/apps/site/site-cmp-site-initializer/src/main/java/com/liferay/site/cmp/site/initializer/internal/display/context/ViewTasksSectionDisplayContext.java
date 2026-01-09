/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ViewTasksSectionDisplayContext {

	public ViewTasksSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;

		_assetEntry = (AssetEntry)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(8);

		sb.append("emptySearch=true&filter=objectDefinitionId eq ");
		sb.append(_objectDefinition.getObjectDefinitionId());

		if (_assetEntry != null) {
			sb.append(" and scopeGroupId eq ");
			sb.append(_assetEntry.getGroupId());
		}

		sb.append("&nestedFields=cmpProjectToCMPTask,embedded,file.metadata,");
		sb.append("file.previewURL,file.thumbnailURL,");
		sb.append("numberOfObjectEntries,numberOfObjectEntryFolders,");
		sb.append("systemProperties.objectDefinitionBrief");

		return "/o/search/v1.0/search?" + sb.toString();
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"label",
					() -> {
						Layout layout = _themeDisplay.getLayout();

						if (layout == null) {
							return null;
						}

						return layout.getName(_themeDisplay.getLocale(), true);
					}
				))
		).put(
			"hideSpace", true
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return Collections.emptyList();
	}

	public CreationMenu getCreationMenu() {
		if (_assetEntry == null) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "createProject");
				dropdownItem.putData(
					"objectDefinitionId",
					String.valueOf(_objectDefinition.getObjectDefinitionId()));
				dropdownItem.putData(
					"redirect",
					StringBundler.concat(
						_themeDisplay.getPortalURL(),
						_themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/add_task?objectDefinitionId=",
						_objectDefinition.getObjectDefinitionId(), "&plid=",
						_themeDisplay.getPlid(), "&projectGroupId=",
						_assetEntry.getGroupId(), "&projectId=",
						_assetEntry.getClassPK(), "&redirect=",
						_themeDisplay.getURLCurrent()));
				dropdownItem.putData(
					"title",
					_objectDefinition.getLabel(_themeDisplay.getLocale()));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-task"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest, "click-new-to-create-your-first-task")
		).put(
			"image", "/states/cmp_empty_state_tasks.svg"
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-tasks-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		long projectId = 0;

		if (_assetEntry != null) {
			projectId = _assetEntry.getClassPK();
		}

		String url = StringBundler.concat(
			StringBundler.concat(
				_themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/e/edit-task/",
				PortalUtil.getClassNameId(_objectDefinition.getClassName()),
				StringPool.SLASH),
			"{embedded.id}?redirect=", _themeDisplay.getURLCurrent(),
			"&projectId=", projectId);

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				url, "pencil", "edit",
				LanguageUtil.get(_httpServletRequest, "edit"), "get", "update",
				null),
			new FDSActionDropdownItem(
				url, "view", "actionLink",
				LanguageUtil.get(_httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), null, "delete",
				null));
	}

	private final AssetEntry _assetEntry;
	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ThemeDisplay _themeDisplay;

}