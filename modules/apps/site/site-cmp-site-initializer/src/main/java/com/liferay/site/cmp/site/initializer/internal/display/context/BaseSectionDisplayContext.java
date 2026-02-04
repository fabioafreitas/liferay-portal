/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition,
		ObjectEntryService objectEntryService) {

		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;

		this.httpServletRequest = httpServletRequest;
		this.objectDefinition = objectDefinition;
		this.objectEntryService = objectEntryService;

		assetEntry = (AssetEntry)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);

		groupIds = _getDepotEntryGroupIds();

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public abstract String getAPIURL();

	public List<DropdownItem> getBulkActionDropdownItems() {
		return Collections.emptyList();
	}

	public abstract CreationMenu getCreationMenu() throws Exception;

	public abstract Map<String, Object> getEmptyState();

	public abstract List<FDSActionDropdownItem> getFDSActionDropdownItems();

	protected boolean hasAddObjectEntryPortletResourcePermission()
		throws Exception {

		long groupId = themeDisplay.getScopeGroupId();

		if (assetEntry != null) {
			groupId = assetEntry.getGroupId();
		}

		return objectEntryService.hasPortletResourcePermission(
			groupId, objectDefinition.getObjectDefinitionId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	protected final AssetEntry assetEntry;
	protected final List<Long> groupIds;
	protected final HttpServletRequest httpServletRequest;
	protected final ObjectDefinition objectDefinition;
	protected final ObjectEntryService objectEntryService;
	protected final ThemeDisplay themeDisplay;

	private List<Long> _getDepotEntryGroupIds() {
		List<Long> groupIds = new ArrayList<>();

		for (long groupId :
				_depotEntryLocalService.getDepotEntryGroupIds(
					CompanyThreadLocal.getCompanyId(),
					DepotConstants.TYPE_PROJECT)) {

			if (_isAssetLibraryAdminOrAssetLibraryMember(groupId)) {
				groupIds.add(groupId);
			}
		}

		return groupIds;
	}

	private boolean _isAssetLibraryAdminOrAssetLibraryMember(long groupId) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId)) {
			return true;
		}

		return _groupLocalService.hasUserGroup(
			PrincipalThreadLocal.getUserId(), groupId);
	}

	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;

}