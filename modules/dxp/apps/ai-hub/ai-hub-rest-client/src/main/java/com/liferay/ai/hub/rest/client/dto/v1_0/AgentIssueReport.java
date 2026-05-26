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

	public String getAgentDefinitionExternalReferenceCode() {
		return agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		String agentDefinitionExternalReferenceCode) {

		this.agentDefinitionExternalReferenceCode =
			agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			agentDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			agentDefinitionExternalReferenceCode =
				agentDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String agentDefinitionExternalReferenceCode;

	public Category getCategory() {
		return category;
	}

	public String getCategoryAsString() {
		if (category == null) {
			return null;
		}

		return category.toString();
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setCategory(
		UnsafeSupplier<Category, Exception> categoryUnsafeSupplier) {

		try {
			category = categoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Category category;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

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

	public Surface getSurface() {
		return surface;
	}

	public String getSurfaceAsString() {
		if (surface == null) {
			return null;
		}

		return surface.toString();
	}

	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	public void setSurface(
		UnsafeSupplier<Surface, Exception> surfaceUnsafeSupplier) {

		try {
			surface = surfaceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Surface surface;

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

	public static enum Category {

		AGENT_ERROR("agentError"), HARMFUL_CONTENT("harmfulContent"),
		INCORRECT("incorrect"), OTHER("other"), PII_EXPOSURE("piiExposure");

		public static Category create(String value) {
			for (Category category : values()) {
				if (Objects.equals(category.getValue(), value) ||
					Objects.equals(category.name(), value)) {

					return category;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Category(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum Surface {

		AI_HUB("aiHub"), CLICK_TO_CHAT("clickToChat"),
		CMS_ASSISTANT("cmsAssistant");

		public static Surface create(String value) {
			for (Surface surface : values()) {
				if (Objects.equals(surface.getValue(), value) ||
					Objects.equals(surface.name(), value)) {

					return surface;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Surface(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:1413816942