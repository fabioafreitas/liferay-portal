/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.ProvisioningRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class ProvisioningRequest implements Cloneable, Serializable {

	public static ProvisioningRequest toDTO(String json) {
		return ProvisioningRequestSerDes.toDTO(json);
	}

	public String getAccountExternalReferenceCode() {
		return accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		String accountExternalReferenceCode) {

		this.accountExternalReferenceCode = accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountExternalReferenceCodeUnsafeSupplier) {

		try {
			accountExternalReferenceCode =
				accountExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountExternalReferenceCode;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		try {
			accountId = accountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountId;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		try {
			accountName = accountNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountName;

	public String getLiferayDXPURL() {
		return liferayDXPURL;
	}

	public void setLiferayDXPURL(String liferayDXPURL) {
		this.liferayDXPURL = liferayDXPURL;
	}

	public void setLiferayDXPURL(
		UnsafeSupplier<String, Exception> liferayDXPURLUnsafeSupplier) {

		try {
			liferayDXPURL = liferayDXPURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String liferayDXPURL;

	public UserAccount[] getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(UserAccount[] userAccounts) {
		this.userAccounts = userAccounts;
	}

	public void setUserAccounts(
		UnsafeSupplier<UserAccount[], Exception> userAccountsUnsafeSupplier) {

		try {
			userAccounts = userAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccount[] userAccounts;

	@Override
	public ProvisioningRequest clone() throws CloneNotSupportedException {
		return (ProvisioningRequest)super.clone();
	}

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
		return ProvisioningRequestSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1010208806