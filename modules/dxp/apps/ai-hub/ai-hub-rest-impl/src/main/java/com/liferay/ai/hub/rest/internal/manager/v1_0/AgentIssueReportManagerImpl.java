/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport;
import com.liferay.ai.hub.rest.manager.v1_0.AgentIssueReportManager;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
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

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			dtoConverterContext.getUserId());

		ObjectEntry auditIssueReportObjectEntry =
			_objectEntryManager.addObjectEntry(
				dtoConverterContext,
				_objectDefinitionLocalService.getObjectDefinition(
					company.getCompanyId(), "AIHubAgentIssueReport"),
				new ObjectEntry() {
					{
						setProperties(
							() -> Map.of(
								"r_accountToAIHubAgentIssueReports_accountEntryId",
								accountEntry.getAccountEntryId(), "reason",
								GetterUtil.getString(
									agentIssueReport.getReason()),
								"surface",
								GetterUtil.getString(
									agentIssueReport.getSurface()),
								"userMessage",
								GetterUtil.getString(
									agentIssueReport.getUserMessage())));
					}
				},
				null);

		ObjectDefinition agentDefinitionObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				company.getCompanyId(), "AIHubAgentDefinition");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				agentDefinitionObjectDefinition.getObjectDefinitionId(),
				"agentDefinitionsToAgentIssueReports");

		for (String agentDefinitionExternalReferenceCode :
				agentIssueReport.getAgentDefinitionExternalReferenceCodes()) {

			com.liferay.object.model.ObjectEntry agentDefinitionObjectEntry =
				_objectEntryLocalService.getObjectEntry(
					agentDefinitionExternalReferenceCode, 0L,
					agentDefinitionObjectDefinition.getObjectDefinitionId());

			_objectRelationshipLocalService.
				addObjectRelationshipMappingTableValues(
					dtoConverterContext.getUserId(),
					objectRelationship.getObjectRelationshipId(),
					agentDefinitionObjectEntry.getObjectEntryId(),
					auditIssueReportObjectEntry.getId(), new ServiceContext());
		}

		return new AgentIssueReport() {
			{
				setAgentDefinitionExternalReferenceCodes(
					agentIssueReport::getAgentDefinitionExternalReferenceCodes);
				setChatbotExternalReferenceCode(
					agentIssueReport::getChatbotExternalReferenceCode);
				setDateCreated(auditIssueReportObjectEntry::getDateCreated);
				setExternalReferenceCode(
					auditIssueReportObjectEntry::getExternalReferenceCode);
				setId(auditIssueReportObjectEntry::getId);
				setReason(agentIssueReport::getReason);
				setSurface(agentIssueReport::getSurface);
				setUserMessage(agentIssueReport::getUserMessage);
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}