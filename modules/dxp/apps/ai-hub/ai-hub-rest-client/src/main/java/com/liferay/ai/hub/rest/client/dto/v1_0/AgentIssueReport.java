/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.AgentIssueReportSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AgentIssueReport implements Cloneable, Serializable {

	public static AgentIssueReport toDTO(String json) {
		return AgentIssueReportSerDes.toDTO(json);
	}

	public String[] getAgentDefinitionExternalReferenceCodes() {
		return agentDefinitionExternalReferenceCodes;
	}

	public void setAgentDefinitionExternalReferenceCodes(
		String[] agentDefinitionExternalReferenceCodes) {

		this.agentDefinitionExternalReferenceCodes =
			agentDefinitionExternalReferenceCodes;
	}

	public void setAgentDefinitionExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			agentDefinitionExternalReferenceCodesUnsafeSupplier) {

		try {
			agentDefinitionExternalReferenceCodes =
				agentDefinitionExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] agentDefinitionExternalReferenceCodes;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setReason(
		UnsafeSupplier<String, Exception> reasonUnsafeSupplier) {

		try {
			reason = reasonUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String reason;

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public void setSurface(
		UnsafeSupplier<String, Exception> surfaceUnsafeSupplier) {

		try {
			surface = surfaceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String surface;

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public void setTraceId(
		UnsafeSupplier<String, Exception> traceIdUnsafeSupplier) {

		try {
			traceId = traceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String traceId;

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public void setUserMessage(
		UnsafeSupplier<String, Exception> userMessageUnsafeSupplier) {

		try {
			userMessage = userMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userMessage;

	@Override
	public AgentIssueReport clone() throws CloneNotSupportedException {
		return (AgentIssueReport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AgentIssueReport)) {
			return false;
		}

		AgentIssueReport agentIssueReport = (AgentIssueReport)object;

		return Objects.equals(toString(), agentIssueReport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AgentIssueReportSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1534078519