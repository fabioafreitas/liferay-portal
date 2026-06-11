/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.internal.util.ObjectEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Franca
 */
@Component(service = FragmentRenderer.class)
public class ContentCoverageMatrixComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "ContentGapMatrixCard";
	}

	@Override
	protected String getLabelKey() {
		return "content-coverage-matrix";
	}

	@Override
	protected String getModuleName() {
		return "site-cmp-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry projectObjectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (projectObjectEntry == null) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"cmsGroupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getScopeGroupId();
			}
		).put(
			"funnelStageVocabularyExternalReferenceCode", "CMP_FUNNEL_STAGE"
		).put(
			"personaVocabularyExternalReferenceCode", "CMP_PERSONAS"
		).put(
			"projectId", projectObjectEntry.getObjectEntryId()
		).build();
	}

}