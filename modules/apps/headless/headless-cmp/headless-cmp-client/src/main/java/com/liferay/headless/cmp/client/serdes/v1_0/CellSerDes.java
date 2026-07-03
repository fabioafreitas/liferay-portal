/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.Cell;
import com.liferay.headless.cmp.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class CellSerDes {

	public static Cell toDTO(String json) {
		CellJSONParser cellJSONParser = new CellJSONParser();

		return cellJSONParser.parseToDTO(json);
	}

	public static Cell[] toDTOs(String json) {
		CellJSONParser cellJSONParser = new CellJSONParser();

		return cellJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Cell cell) {
		if (cell == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (cell.getFunnelStageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"funnelStageId\": ");

			sb.append("\"");

			sb.append(_escape(cell.getFunnelStageId()));

			sb.append("\"");
		}

		if (cell.getPersonaId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"personaId\": ");

			sb.append("\"");

			sb.append(_escape(cell.getPersonaId()));

			sb.append("\"");
		}

		if (cell.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(cell.getTotalCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CellJSONParser cellJSONParser = new CellJSONParser();

		return cellJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Cell cell) {
		if (cell == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (cell.getFunnelStageId() == null) {
			map.put("funnelStageId", null);
		}
		else {
			map.put("funnelStageId", String.valueOf(cell.getFunnelStageId()));
		}

		if (cell.getPersonaId() == null) {
			map.put("personaId", null);
		}
		else {
			map.put("personaId", String.valueOf(cell.getPersonaId()));
		}

		if (cell.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put("totalCount", String.valueOf(cell.getTotalCount()));
		}

		return map;
	}

	public static class CellJSONParser extends BaseJSONParser<Cell> {

		@Override
		protected Cell createDTO() {
			return new Cell();
		}

		@Override
		protected Cell[] createDTOArray(int size) {
			return new Cell[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "funnelStageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "personaId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Cell cell, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "funnelStageId")) {
				if (jsonParserFieldValue != null) {
					cell.setFunnelStageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "personaId")) {
				if (jsonParserFieldValue != null) {
					cell.setPersonaId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					cell.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-225844252