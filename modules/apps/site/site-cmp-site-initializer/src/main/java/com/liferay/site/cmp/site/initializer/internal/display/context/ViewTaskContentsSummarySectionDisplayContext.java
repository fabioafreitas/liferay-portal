/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Fábio Alves
 */
public class ViewTaskContentsSummarySectionDisplayContext {

	public ViewTaskContentsSummarySectionDisplayContext(
		AssetEntry assetEntry, AssetTagLocalService assetTagLocalService,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		_assetEntry = assetEntry;
		_assetTagLocalService = assetTagLocalService;
		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(10);

		sb.append("emptySearch=true&filter=");

		sb.append(_getCMPSectionFilterString());

		sb.append("&nestedFields=embedded,file.metadata,");
		sb.append("file.previewURL,file.thumbnailURL,");
		sb.append("numberOfObjectEntries,numberOfObjectEntryFolders,");
		sb.append("systemProperties.objectDefinitionBrief");

		return HttpComponentsUtil.addParameters(
			"/o/search/v1.0/search?" + sb, "sort", "dateModified:desc");
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description", ""
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-assets-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		try {
			return ListUtil.fromArray(
				new FDSActionDropdownItem(
					StringBundler.concat(
						_themeDisplay.getPortalURL(),
						_themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/remove_content_from_task?keywords=",
						StringUtil.merge(_getTags(), ","),
						"&objectEntryExternalReferenceCode=",
						"{embedded.externalReferenceCode}&objectEntryId=",
						_assetEntry.getClassPK(),
						"&scopeKey={embedded.scopeKey}",
						"&objectDefinitionExternalReferenceCode=",
						"{embedded.systemProperties.objectDefinitionBrief.",
						"externalReferenceCode}&redirect=",
						_themeDisplay.getURLCurrent()),
					"chain-broken", "remove-from-task",
					LanguageUtil.get(_httpServletRequest, "remove-from-task"),
					"delete", "delete", null));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private String _appendStatus(String filterString) {
		return StringBundler.concat(
			filterString, " and status in (", StringUtil.merge(_statuses, ", "),
			")");
	}

	private String _getCMPSectionFilterString() {
		String filterString =
			"cmsRoot eq true and (cmsSection eq 'contents' or cmsSection eq " +
				"'files')";

		String[] tags;

		try {
			tags = _getTags();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return filterString + " and status eq " +
				WorkflowConstants.STATUS_ANY;
		}

		if (ArrayUtil.isEmpty(tags)) {
			return filterString + " and status eq " +
				WorkflowConstants.STATUS_ANY;
		}

		return StringBundler.concat(
			_appendStatus(filterString), " and keywords/any(k:k in ('",
			StringUtil.merge(tags, "','"), "'))");
	}

	private String[] _getTags() throws PortalException {
		return TransformUtil.transformToArray(
			_assetTagLocalService.getTags(
				_assetEntry.getClassName(), _assetEntry.getClassPK()),
			AssetTagModel::getName, String.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewTaskContentsSummarySectionDisplayContext.class);

	private static final List<Integer> _statuses = Arrays.asList(
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_DRAFT,
		WorkflowConstants.STATUS_EXPIRED, WorkflowConstants.STATUS_PENDING,
		WorkflowConstants.STATUS_SCHEDULED);

	private final AssetEntry _assetEntry;
	private final AssetTagLocalService _assetTagLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ThemeDisplay _themeDisplay;

}