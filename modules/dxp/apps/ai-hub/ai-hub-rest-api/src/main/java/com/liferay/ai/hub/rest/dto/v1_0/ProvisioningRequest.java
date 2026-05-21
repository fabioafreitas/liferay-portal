/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@GraphQLName("ProvisioningRequest")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"accountName", "liferayDXPURL", "userAccounts"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProvisioningRequest")
public class ProvisioningRequest implements Serializable {

	public static ProvisioningRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(ProvisioningRequest.class, json);
	}

	public static ProvisioningRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProvisioningRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAccountExternalReferenceCode() {
		if (_accountExternalReferenceCodeSupplier != null) {
			accountExternalReferenceCode =
				_accountExternalReferenceCodeSupplier.get();

			_accountExternalReferenceCodeSupplier = null;
		}

		return accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		String accountExternalReferenceCode) {

		this.accountExternalReferenceCode = accountExternalReferenceCode;

		_accountExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountExternalReferenceCodeUnsafeSupplier) {

		_accountExternalReferenceCodeSupplier = () -> {
			try {
				return accountExternalReferenceCodeUnsafeSupplier.get();
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
	protected String accountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAccountId() {
		if (_accountIdSupplier != null) {
			accountId = _accountIdSupplier.get();

			_accountIdSupplier = null;
		}

		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;

		_accountIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		_accountIdSupplier = () -> {
			try {
				return accountIdUnsafeSupplier.get();
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
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAccountName() {
		if (_accountNameSupplier != null) {
			accountName = _accountNameSupplier.get();

			_accountNameSupplier = null;
		}

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;

		_accountNameSupplier = null;
	}

	@JsonIgnore
	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		_accountNameSupplier = () -> {
			try {
				return accountNameUnsafeSupplier.get();
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
	protected String accountName;

	@JsonIgnore
	private Supplier<String> _accountNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLiferayDXPURL() {
		if (_liferayDXPURLSupplier != null) {
			liferayDXPURL = _liferayDXPURLSupplier.get();

			_liferayDXPURLSupplier = null;
		}

		return liferayDXPURL;
	}

	public void setLiferayDXPURL(String liferayDXPURL) {
		this.liferayDXPURL = liferayDXPURL;

		_liferayDXPURLSupplier = null;
	}

	@JsonIgnore
	public void setLiferayDXPURL(
		UnsafeSupplier<String, Exception> liferayDXPURLUnsafeSupplier) {

		_liferayDXPURLSupplier = () -> {
			try {
				return liferayDXPURLUnsafeSupplier.get();
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
	protected String liferayDXPURL;

	@JsonIgnore
	private Supplier<String> _liferayDXPURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public UserAccount[] getUserAccounts() {
		if (_userAccountsSupplier != null) {
			userAccounts = _userAccountsSupplier.get();

			_userAccountsSupplier = null;
		}

		return userAccounts;
	}

	public void setUserAccounts(UserAccount[] userAccounts) {
		this.userAccounts = userAccounts;

		_userAccountsSupplier = null;
	}

	@JsonIgnore
	public void setUserAccounts(
		UnsafeSupplier<UserAccount[], Exception> userAccountsUnsafeSupplier) {

		_userAccountsSupplier = () -> {
			try {
				return userAccountsUnsafeSupplier.get();
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
	protected UserAccount[] userAccounts;

	@JsonIgnore
	private Supplier<UserAccount[]> _userAccountsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProvisioningRequest)) {
			return false;
		}

		ProvisioningRequest provisioningRequest = (ProvisioningRequest)object;

		return Objects.equals(toString(), provisioningRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String accountExternalReferenceCode = getAccountExternalReferenceCode();

		if (accountExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountExternalReferenceCode));

			sb.append("\"");
		}

		Long accountId = getAccountId();

		if (accountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountId);
		}

		String accountName = getAccountName();

		if (accountName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(accountName));

			sb.append("\"");
		}

		String liferayDXPURL = getLiferayDXPURL();

		if (liferayDXPURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"liferayDXPURL\": ");

			sb.append("\"");

			sb.append(_escape(liferayDXPURL));

			sb.append("\"");
		}

		UserAccount[] userAccounts = getUserAccounts();

		if (userAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccounts\": ");

			sb.append("[");

			for (int i = 0; i < userAccounts.length; i++) {
				sb.append(String.valueOf(userAccounts[i]));

				if ((i + 1) < userAccounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.ProvisioningRequest",
		name = "x-class-name"
	)
	public String xClassName;

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
// LIFERAY-REST-BUILDER-HASH:-14222986