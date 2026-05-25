/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.search;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.search.indexer.IndexerPermissionPostFilter;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken",
	service = IndexerPermissionPostFilter.class
)
public class KaleoTaskInstanceTokenIndexerPermissionPostFilter
	implements IndexerPermissionPostFilter {

	@Override
	public boolean hasPermission(
		PermissionChecker permissionChecker, long entryClassPK) {

		try {
			return _kaleoTaskInstanceTokenModelResourcePermission.contains(
				permissionChecker, entryClassPK, ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean isPermissionAware() {
		return true;
	}

	@Override
	public boolean isVisible(long classPK, int status) {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskInstanceTokenIndexerPermissionPostFilter.class);

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken)"
	)
	private ModelResourcePermission<KaleoTaskInstanceToken>
		_kaleoTaskInstanceTokenModelResourcePermission;

}
