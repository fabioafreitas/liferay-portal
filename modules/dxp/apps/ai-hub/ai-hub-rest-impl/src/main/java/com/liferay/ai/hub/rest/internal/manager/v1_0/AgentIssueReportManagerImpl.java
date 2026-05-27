/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport;
import com.liferay.ai.hub.rest.manager.v1_0.AgentIssueReportManager;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

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

		ObjectEntry agentDefinitionObjectEntry =
			_objectEntryManager.getObjectEntry(
				company.getCompanyId(), dtoConverterContext,
				agentIssueReport.getAgentDefinitionExternalReferenceCode(),
				_objectDefinitionLocalService.getObjectDefinition(
					company.getCompanyId(), "AIHubAgentDefinition"),
				null);

		ObjectEntry objectEntry = new ObjectEntry();

		objectEntry.setProperties(
			() -> Map.of(
				"description",
				GetterUtil.getString(agentIssueReport.getDescription()),
				"r_accountToAIHubAgentIssueReports_accountEntryId",
				GetterUtil.getLong(
					agentDefinitionObjectEntry.getPropertyValue(
						"r_accountToAIHubAgentDefinitions_accountEntryId")),
				"r_agentToIssueReports_aiHubAgentDefinitionId",
				GetterUtil.getLong(agentDefinitionObjectEntry.getId()),
				"reason", GetterUtil.getString(agentIssueReport.getReason()),
				"surface", GetterUtil.getString(agentIssueReport.getSurface()),
				"traceId",
				GetterUtil.getString(agentIssueReport.getTraceId())));

		ObjectEntry auditIssueReportObjectEntry =
			_objectEntryManager.addObjectEntry(
				dtoConverterContext,
				_objectDefinitionLocalService.getObjectDefinition(
					company.getCompanyId(), "AIHubAgentIssueReport"),
				objectEntry, null);

		return new AgentIssueReport() {
			{
				setAgentDefinitionExternalReferenceCode(
					agentIssueReport::getAgentDefinitionExternalReferenceCode);
				setDateCreated(auditIssueReportObjectEntry::getDateCreated);
				setDescription(agentIssueReport::getDescription);
				setExternalReferenceCode(
					auditIssueReportObjectEntry::getExternalReferenceCode);
				setId(auditIssueReportObjectEntry::getId);
				setReason(agentIssueReport::getReason);
				setSurface(agentIssueReport::getSurface);
				setTraceId(agentIssueReport::getTraceId);
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

}