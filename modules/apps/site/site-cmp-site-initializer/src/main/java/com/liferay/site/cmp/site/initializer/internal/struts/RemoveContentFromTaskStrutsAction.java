/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.struts;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(
	property = "path=/cms/remove_content_from_task",
	service = StrutsAction.class
)
public class RemoveContentFromTaskStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectDefinition objectDefinition =
			_objectDefinitionService.getObjectDefinitionByExternalReferenceCode(
				ParamUtil.getString(
					httpServletRequest,
					"objectDefinitionExternalReferenceCode"),
				themeDisplay.getCompanyId());

		if (!Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			return null;
		}

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				themeDisplay.getCompanyId(), objectDefinition.getStorageType());

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				themeDisplay.getSiteDefaultLocale(), null,
				themeDisplay.getUser());

		String objectEntryExternalReferenceCode = ParamUtil.getString(
			httpServletRequest, "objectEntryExternalReferenceCode");

		String scopeKey = ParamUtil.getString(httpServletRequest, "scopeKey");

		ObjectEntry objectEntry = objectEntryManager.getObjectEntry(
			themeDisplay.getCompanyId(), defaultDTOConverterContext,
			objectEntryExternalReferenceCode, objectDefinition, scopeKey);

		String[] objectEntryKeywords = objectEntry.getKeywords();

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		objectEntry.setKeywords(
			() -> ArrayUtil.filter(
				objectEntryKeywords,
				keyword -> !ArrayUtil.contains(keywords.split(","), keyword)));

		objectEntryManager.partialUpdateObjectEntry(
			themeDisplay.getCompanyId(), defaultDTOConverterContext,
			objectEntryExternalReferenceCode, objectDefinition, objectEntry,
			scopeKey);

		httpServletResponse.sendRedirect(
			ParamUtil.getString(httpServletRequest, "redirect"));

		return null;
	}

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

}