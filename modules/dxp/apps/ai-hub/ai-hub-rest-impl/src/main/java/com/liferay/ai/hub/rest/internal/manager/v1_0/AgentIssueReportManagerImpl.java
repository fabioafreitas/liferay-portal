/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport;
import com.liferay.ai.hub.rest.manager.v1_0.AgentIssueReportManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(service = AgentIssueReportManager.class)
public class AgentIssueReportManagerImpl implements AgentIssueReportManager {

	@Override
	public AgentIssueReport postAgentIssueReport(
			Company company, DTOConverterContext dtoConverterContext,
			AgentIssueReport agentIssueReport)
		throws Exception {

		String agentDefinitionExternalReferenceCode =
			agentIssueReport.getAgentDefinitionExternalReferenceCode();

		if (Validator.isNull(agentDefinitionExternalReferenceCode)) {
			throw new IllegalArgumentException(
				"Agent definition external reference code is required");
		}

		ObjectDefinition agentDefinitionObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				company.getCompanyId(), "AIHubAgentDefinition");

		ObjectEntry agentObjectEntry = _objectEntryManager.getObjectEntry(
			company.getCompanyId(), dtoConverterContext,
			agentDefinitionExternalReferenceCode,
			agentDefinitionObjectDefinition, null);

		long accountEntryId = GetterUtil.getLong(
			agentObjectEntry.getPropertyValue(
				"r_accountToAIHubAgentDefinitions_accountEntryId"));

		Map<String, Object> properties = new HashMap<>();

		properties.put("category", agentIssueReport.getCategoryAsString());
		properties.put(
			"description",
			GetterUtil.getString(agentIssueReport.getDescription()));
		properties.put(
			"r_accountToAIHubAgentIssueReports_accountEntryId", accountEntryId);
		properties.put(
			"r_agentDefinitionToAgentIssueReports_aiHubAgentDefinitionId",
			GetterUtil.getLong(agentObjectEntry.getId()));
		properties.put("surface", agentIssueReport.getSurfaceAsString());
		properties.put("traceId", agentIssueReport.getTraceId());

		ObjectDefinition agentIssueReportObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				company.getCompanyId(), "AIHubAgentIssueReport");

		ObjectEntry persistedObjectEntry = _objectEntryManager.addObjectEntry(
			dtoConverterContext, agentIssueReportObjectDefinition,
			new ObjectEntry() {
				{
					setProperties(() -> properties);
				}
			},
			null);

		_routeAuditMessage(
			accountEntryId, agentDefinitionExternalReferenceCode,
			persistedObjectEntry, agentIssueReport);

		return _toAgentIssueReport(
			agentDefinitionExternalReferenceCode, agentIssueReport,
			persistedObjectEntry);
	}

	private void _routeAuditMessage(
			long accountEntryId, String agentDefinitionExternalReferenceCode,
			ObjectEntry persistedObjectEntry, AgentIssueReport agentIssueReport)
		throws Exception {

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			accountEntryId, "com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport",
			GetterUtil.getLong(persistedObjectEntry.getId()), null,
			_EVENT_TYPE_AI_ISSUE_REPORT, null);

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		additionalInfoJSONObject.put(
			"agentDefinitionExternalReferenceCode",
			agentDefinitionExternalReferenceCode
		).put(
			"category", agentIssueReport.getCategoryAsString()
		).put(
			"surface", agentIssueReport.getSurfaceAsString()
		).put(
			"traceId", agentIssueReport.getTraceId()
		);

		_auditRouter.route(auditMessage);
	}

	private AgentIssueReport _toAgentIssueReport(
		String agentDefinitionExternalReferenceCode,
		AgentIssueReport agentIssueReport, ObjectEntry persistedObjectEntry) {

		return new AgentIssueReport() {
			{
				setAgentDefinitionExternalReferenceCode(
					() -> agentDefinitionExternalReferenceCode);
				setCategory(agentIssueReport::getCategory);
				setDateCreated(persistedObjectEntry::getDateCreated);
				setDescription(agentIssueReport::getDescription);
				setExternalReferenceCode(
					persistedObjectEntry::getExternalReferenceCode);
				setId(persistedObjectEntry::getId);
				setSurface(agentIssueReport::getSurface);
				setTraceId(agentIssueReport::getTraceId);
			}
		};
	}

	private static final String _EVENT_TYPE_AI_ISSUE_REPORT = "AI_ISSUE_REPORT";

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManager _objectEntryManager;

}