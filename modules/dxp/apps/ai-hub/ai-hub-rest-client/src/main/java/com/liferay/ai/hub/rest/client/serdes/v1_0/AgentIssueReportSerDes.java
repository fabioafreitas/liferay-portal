/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.AgentIssueReport;
import com.liferay.ai.hub.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AgentIssueReportSerDes {

	public static AgentIssueReport toDTO(String json) {
		AgentIssueReportJSONParser agentIssueReportJSONParser =
			new AgentIssueReportJSONParser();

		return agentIssueReportJSONParser.parseToDTO(json);
	}

	public static AgentIssueReport[] toDTOs(String json) {
		AgentIssueReportJSONParser agentIssueReportJSONParser =
			new AgentIssueReportJSONParser();

		return agentIssueReportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AgentIssueReport agentIssueReport) {
		if (agentIssueReport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (agentIssueReport.getAgentDefinitionExternalReferenceCodes() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"agentDefinitionExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < agentIssueReport.
					 getAgentDefinitionExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						agentIssueReport.
							getAgentDefinitionExternalReferenceCodes()[i]));

				if ((i + 1) < agentIssueReport.
						getAgentDefinitionExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (agentIssueReport.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					agentIssueReport.getDateCreated()));

			sb.append("\"");
		}

		if (agentIssueReport.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(agentIssueReport.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (agentIssueReport.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(agentIssueReport.getId());
		}

		if (agentIssueReport.getReason() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reason\": ");

			sb.append("\"");

			sb.append(_escape(agentIssueReport.getReason()));

			sb.append("\"");
		}

		if (agentIssueReport.getSurface() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"surface\": ");

			sb.append("\"");

			sb.append(_escape(agentIssueReport.getSurface()));

			sb.append("\"");
		}

		if (agentIssueReport.getTraceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"traceId\": ");

			sb.append("\"");

			sb.append(_escape(agentIssueReport.getTraceId()));

			sb.append("\"");
		}

		if (agentIssueReport.getUserMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userMessage\": ");

			sb.append("\"");

			sb.append(_escape(agentIssueReport.getUserMessage()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AgentIssueReportJSONParser agentIssueReportJSONParser =
			new AgentIssueReportJSONParser();

		return agentIssueReportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AgentIssueReport agentIssueReport) {
		if (agentIssueReport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (agentIssueReport.getAgentDefinitionExternalReferenceCodes() ==
				null) {

			map.put("agentDefinitionExternalReferenceCodes", null);
		}
		else {
			map.put(
				"agentDefinitionExternalReferenceCodes",
				String.valueOf(
					agentIssueReport.
						getAgentDefinitionExternalReferenceCodes()));
		}

		if (agentIssueReport.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					agentIssueReport.getDateCreated()));
		}

		if (agentIssueReport.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(agentIssueReport.getExternalReferenceCode()));
		}

		if (agentIssueReport.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(agentIssueReport.getId()));
		}

		if (agentIssueReport.getReason() == null) {
			map.put("reason", null);
		}
		else {
			map.put("reason", String.valueOf(agentIssueReport.getReason()));
		}

		if (agentIssueReport.getSurface() == null) {
			map.put("surface", null);
		}
		else {
			map.put("surface", String.valueOf(agentIssueReport.getSurface()));
		}

		if (agentIssueReport.getTraceId() == null) {
			map.put("traceId", null);
		}
		else {
			map.put("traceId", String.valueOf(agentIssueReport.getTraceId()));
		}

		if (agentIssueReport.getUserMessage() == null) {
			map.put("userMessage", null);
		}
		else {
			map.put(
				"userMessage",
				String.valueOf(agentIssueReport.getUserMessage()));
		}

		return map;
	}

	public static class AgentIssueReportJSONParser
		extends BaseJSONParser<AgentIssueReport> {

		@Override
		protected AgentIssueReport createDTO() {
			return new AgentIssueReport();
		}

		@Override
		protected AgentIssueReport[] createDTOArray(int size) {
			return new AgentIssueReport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName,
					"agentDefinitionExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reason")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "surface")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "traceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userMessage")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AgentIssueReport agentIssueReport, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName,
					"agentDefinitionExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					agentIssueReport.setAgentDefinitionExternalReferenceCodes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					agentIssueReport.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reason")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setReason((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "surface")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setSurface((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "traceId")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setTraceId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userMessage")) {
				if (jsonParserFieldValue != null) {
					agentIssueReport.setUserMessage(
						(String)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
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
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}
// LIFERAY-REST-BUILDER-HASH:-2043153848