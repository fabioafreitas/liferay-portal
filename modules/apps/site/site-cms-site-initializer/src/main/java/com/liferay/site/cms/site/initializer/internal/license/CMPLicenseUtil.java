/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.license;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

/**
 * @author Fábio Alves
 */
public class CMPLicenseUtil {

	public static void reconcile(
		long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		boolean appEnabled = LicenseManagerUtil.isAppEnabled(App.CMP);

		List<ObjectDefinition> objectDefinitions =
			objectDefinitionLocalService.getObjectDefinitions(
				companyId, WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (!_isCMPObjectDefinition(objectDefinition) ||
				(objectDefinition.isActive() == appEnabled)) {

				continue;
			}

			try {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Setting the active flag of object definition ",
							objectDefinition.getExternalReferenceCode(), " to ",
							appEnabled));
				}

				objectDefinition.setActive(appEnabled);

				objectDefinition =
					objectDefinitionLocalService.updateObjectDefinition(
						objectDefinition);

				if (appEnabled) {
					objectDefinitionLocalService.deployObjectDefinition(
						objectDefinition);
				}
				else {
					objectDefinitionLocalService.deployInactiveObjectDefinition(
						objectDefinition);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to update the active flag of object definition " +
						objectDefinition.getExternalReferenceCode(),
					exception);
			}
		}
	}

	private static boolean _isCMPObjectDefinition(
		ObjectDefinition objectDefinition) {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return false;
		}

		String externalReferenceCode =
			objectDefinition.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode) ||
			!externalReferenceCode.startsWith("L_CMP_")) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(CMPLicenseUtil.class);

}
