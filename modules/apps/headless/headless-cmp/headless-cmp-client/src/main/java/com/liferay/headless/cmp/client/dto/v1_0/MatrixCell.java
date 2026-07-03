/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.dto.v1_0;

import com.liferay.headless.cmp.client.function.UnsafeSupplier;
import com.liferay.headless.cmp.client.serdes.v1_0.MatrixCellSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class MatrixCell implements Cloneable, Serializable {

	public static MatrixCell toDTO(String json) {
		return MatrixCellSerDes.toDTO(json);
	}

	public String getFunnelStageId() {
		return funnelStageId;
	}

	public void setFunnelStageId(String funnelStageId) {
		this.funnelStageId = funnelStageId;
	}

	public void setFunnelStageId(
		UnsafeSupplier<String, Exception> funnelStageIdUnsafeSupplier) {

		try {
			funnelStageId = funnelStageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String funnelStageId;

	public String getPersonaId() {
		return personaId;
	}

	public void setPersonaId(String personaId) {
		this.personaId = personaId;
	}

	public void setPersonaId(
		UnsafeSupplier<String, Exception> personaIdUnsafeSupplier) {

		try {
			personaId = personaIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String personaId;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		try {
			totalCount = totalCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long totalCount;

	@Override
	public MatrixCell clone() throws CloneNotSupportedException {
		return (MatrixCell)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MatrixCell)) {
			return false;
		}

		MatrixCell matrixCell = (MatrixCell)object;

		return Objects.equals(toString(), matrixCell.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MatrixCellSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1071979521