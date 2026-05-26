/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
@GraphQLName("AgentIssueReport")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {
		"agentDefinitionExternalReferenceCode", "category", "surface", "traceId"
	}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AgentIssueReport")
public class AgentIssueReport implements Serializable {

	public static AgentIssueReport toDTO(String json) {
		return ObjectMapperUtil.readValue(AgentIssueReport.class, json);
	}

	public static AgentIssueReport unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AgentIssueReport.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAgentDefinitionExternalReferenceCode() {
		if (_agentDefinitionExternalReferenceCodeSupplier != null) {
			agentDefinitionExternalReferenceCode =
				_agentDefinitionExternalReferenceCodeSupplier.get();

			_agentDefinitionExternalReferenceCodeSupplier = null;
		}

		return agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		String agentDefinitionExternalReferenceCode) {

		this.agentDefinitionExternalReferenceCode =
			agentDefinitionExternalReferenceCode;

		_agentDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAgentDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			agentDefinitionExternalReferenceCodeUnsafeSupplier) {

		_agentDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return agentDefinitionExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String agentDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _agentDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("category")
	@Valid
	public Category getCategory() {
		if (_categorySupplier != null) {
			category = _categorySupplier.get();

			_categorySupplier = null;
		}

		return category;
	}

	@JsonIgnore
	public String getCategoryAsString() {
		Category category = getCategory();

		if (category == null) {
			return null;
		}

		return category.toString();
	}

	public void setCategory(Category category) {
		this.category = category;

		_categorySupplier = null;
	}

	@JsonIgnore
	public void setCategory(
		UnsafeSupplier<Category, Exception> categoryUnsafeSupplier) {

		_categorySupplier = () -> {
			try {
				return categoryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Category category;

	@JsonIgnore
	private Supplier<Category> _categorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("surface")
	@Valid
	public Surface getSurface() {
		if (_surfaceSupplier != null) {
			surface = _surfaceSupplier.get();

			_surfaceSupplier = null;
		}

		return surface;
	}

	@JsonIgnore
	public String getSurfaceAsString() {
		Surface surface = getSurface();

		if (surface == null) {
			return null;
		}

		return surface.toString();
	}

	public void setSurface(Surface surface) {
		this.surface = surface;

		_surfaceSupplier = null;
	}

	@JsonIgnore
	public void setSurface(
		UnsafeSupplier<Surface, Exception> surfaceUnsafeSupplier) {

		_surfaceSupplier = () -> {
			try {
				return surfaceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Surface surface;

	@JsonIgnore
	private Supplier<Surface> _surfaceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTraceId() {
		if (_traceIdSupplier != null) {
			traceId = _traceIdSupplier.get();

			_traceIdSupplier = null;
		}

		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;

		_traceIdSupplier = null;
	}

	@JsonIgnore
	public void setTraceId(
		UnsafeSupplier<String, Exception> traceIdUnsafeSupplier) {

		_traceIdSupplier = () -> {
			try {
				return traceIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String traceId;

	@JsonIgnore
	private Supplier<String> _traceIdSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String agentDefinitionExternalReferenceCode =
			getAgentDefinitionExternalReferenceCode();

		if (agentDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"agentDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(agentDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Category category = getCategory();

		if (category != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append("\"");
			sb.append(category);
			sb.append("\"");
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Surface surface = getSurface();

		if (surface != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"surface\": ");

			sb.append("\"");
			sb.append(surface);
			sb.append("\"");
		}

		String traceId = getTraceId();

		if (traceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"traceId\": ");

			sb.append("\"");

			sb.append(_escape(traceId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.AgentIssueReport",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Category")
	public static enum Category {

		AGENT_ERROR("agentError"), HARMFUL_CONTENT("harmfulContent"),
		INCORRECT("incorrect"), OTHER("other"), PII_EXPOSURE("piiExposure");

		@JsonCreator
		public static Category create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Category category : values()) {
				if (Objects.equals(category.getValue(), value)) {
					return category;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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

	@GraphQLName("Surface")
	public static enum Surface {

		AI_HUB("aiHub"), CLICK_TO_CHAT("clickToChat"),
		CMS_ASSISTANT("cmsAssistant");

		@JsonCreator
		public static Surface create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Surface surface : values()) {
				if (Objects.equals(surface.getValue(), value)) {
					return surface;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:93166536