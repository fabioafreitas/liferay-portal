/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport;
import com.liferay.ai.hub.rest.manager.v1_0.AgentIssueReportManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.List;
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

		String[] agentDefinitionExternalReferenceCodes =
			agentIssueReport.getAgentDefinitionExternalReferenceCodes();

		if (ArrayUtil.isEmpty(agentDefinitionExternalReferenceCodes)) {
			throw new IllegalArgumentException(
				"\"agentDefinitionExternalReferenceCodes\" is required and " +
					"must not be empty");
		}

		ObjectDefinition agentDefinitionObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				company.getCompanyId(), "AIHubAgentDefinition");

		List<ObjectEntry> agentDefinitionObjectEntries = new ArrayList<>();
		Long sharedAccountEntryId = null;

		for (String externalReferenceCode :
				agentDefinitionExternalReferenceCodes) {

			ObjectEntry agentDefinitionObjectEntry =
				_objectEntryManager.getObjectEntry(
					company.getCompanyId(), dtoConverterContext,
					externalReferenceCode, agentDefinitionObjectDefinition,
					null);

			long accountEntryId = GetterUtil.getLong(
				agentDefinitionObjectEntry.getPropertyValue(
					"r_accountToAIHubAgentDefinitions_accountEntryId"));

			if (sharedAccountEntryId == null) {
				sharedAccountEntryId = accountEntryId;
			}
			else if (sharedAccountEntryId.longValue() != accountEntryId) {
				throw new IllegalArgumentException(
					"All supplied agents must belong to the same account");
			}

			agentDefinitionObjectEntries.add(agentDefinitionObjectEntry);
		}

		long accountEntryId = sharedAccountEntryId;

		ObjectEntry objectEntry = new ObjectEntry();

		objectEntry.setProperties(
			() -> Map.of(
				"r_accountToAIHubAgentIssueReports_accountEntryId",
				accountEntryId, "reason",
				GetterUtil.getString(agentIssueReport.getReason()), "surface",
				GetterUtil.getString(agentIssueReport.getSurface()), "traceId",
				GetterUtil.getString(agentIssueReport.getTraceId()),
				"userMessage",
				GetterUtil.getString(agentIssueReport.getUserMessage())));

		ObjectEntry persistedAgentIssueReportObjectEntry =
			_objectEntryManager.addObjectEntry(
				dtoConverterContext,
				_objectDefinitionLocalService.getObjectDefinition(
					company.getCompanyId(), "AIHubAgentIssueReport"),
				objectEntry, null);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectDefinitionId(
					agentDefinitionObjectDefinition.getObjectDefinitionId(),
					"agentDefinitionsToAgentIssueReports");

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(company.getCompanyId());
		serviceContext.setUserId(dtoConverterContext.getUserId());

		for (ObjectEntry agentDefinitionObjectEntry :
				agentDefinitionObjectEntries) {

			_objectRelationshipLocalService.
				addObjectRelationshipMappingTableValues(
					dtoConverterContext.getUserId(),
					objectRelationship.getObjectRelationshipId(),
					GetterUtil.getLong(agentDefinitionObjectEntry.getId()),
					GetterUtil.getLong(
						persistedAgentIssueReportObjectEntry.getId()),
					serviceContext);
		}

		return new AgentIssueReport() {
			{
				setAgentDefinitionExternalReferenceCodes(
					() -> agentDefinitionExternalReferenceCodes);
				setDateCreated(
					persistedAgentIssueReportObjectEntry::getDateCreated);
				setExternalReferenceCode(
					persistedAgentIssueReportObjectEntry::
						getExternalReferenceCode);
				setId(persistedAgentIssueReportObjectEntry::getId);
				setReason(agentIssueReport::getReason);
				setSurface(agentIssueReport::getSurface);
				setTraceId(agentIssueReport::getTraceId);
				setUserMessage(agentIssueReport::getUserMessage);
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}