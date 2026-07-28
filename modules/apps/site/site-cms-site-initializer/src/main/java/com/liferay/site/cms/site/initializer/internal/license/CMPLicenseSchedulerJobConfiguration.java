/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.license;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(service = SchedulerJobConfiguration.class)
public class CMPLicenseSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _reconcile(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			2, TimeUnit.MINUTE);
	}

	private boolean _isCMPObjectDefinition(ObjectDefinition objectDefinition) {
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

	private void _reconcile(long companyId) {
		boolean appEnabled = LicenseManagerUtil.isAppEnabled(App.CMP);

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
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

				_objectDefinitionLocalService.updateSystemObjectDefinition(
					objectDefinition.getExternalReferenceCode(),
					objectDefinition.getObjectDefinitionId(),
					objectDefinition.getAccountEntryRestrictedObjectFieldId(),
					objectDefinition.getDescriptionObjectFieldId(),
					objectDefinition.getObjectFolderId(),
					objectDefinition.getTitleObjectFieldId(),
					objectDefinition.isAccountEntryRestricted(), appEnabled,
					objectDefinition.getClassName(),
					objectDefinition.isEnableCategorization(),
					objectDefinition.isEnableComments(),
					objectDefinition.isEnableFormContainer(),
					objectDefinition.isEnableFriendlyURLCustomization(),
					objectDefinition.isEnableIndexSearch(),
					objectDefinition.isEnableObjectEntryDraft(),
					objectDefinition.isEnableObjectEntryHistory(),
					objectDefinition.isEnableObjectEntrySchedule(),
					objectDefinition.isEnableObjectEntrySubscription(),
					objectDefinition.isEnableObjectEntryVersioning(),
					objectDefinition.getFriendlyURLSeparator(),
					objectDefinition.getLabelMap(), objectDefinition.getName(),
					objectDefinition.getPanelAppOrder(),
					objectDefinition.getPanelCategoryKey(),
					objectDefinition.isPortlet(),
					objectDefinition.getPluralLabelMap(),
					objectDefinition.getScope(), objectDefinition.getStatus(),
					Collections.emptyList(), null,
					_workflowDefinitionLinkLocalService.
						getWorkflowDefinitionLinks(
							companyId, objectDefinition.getClassName()),
					new ServiceContext());
			}
			catch (Exception exception) {
				_log.error(
					"Unable to update the active flag of object definition " +
						objectDefinition.getExternalReferenceCode(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPLicenseSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}