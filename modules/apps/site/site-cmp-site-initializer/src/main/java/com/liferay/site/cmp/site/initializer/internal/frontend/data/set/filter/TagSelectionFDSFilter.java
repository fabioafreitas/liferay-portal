/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Fábio Alves
 */
public class TagSelectionFDSFilter extends BaseSelectionFDSFilter {

	public TagSelectionFDSFilter(
		AssetTagLocalService assetTagLocalService, List<Long> groupIds) {

		_assetTagLocalService = assetTagLocalService;
		_groupIds = groupIds;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "keywords";
	}

	@Override
	public String getLabel() {
		return "tag";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		//		long[] groupIds;
		//
		//		if (_groupedModel == null) {
		//			groupIds = TransformUtil.transformToLongArray(
		//				_depotEntryLocalService.getDepotEntries(
		//					CompanyThreadLocal.getCompanyId(),
		//					DepotConstants.TYPE_PROJECT),
		//				DepotEntryModel::getGroupId);
		//		}
		//		else {
		//			groupIds = new long[] {_groupedModel.getGroupId()};
		//		}

		return TransformUtil.transform(
			SetUtil.fromCollection(
				TransformUtil.transform(
					ListUtil.filter(
						_assetTagLocalService.getGroupsTags(
							ArrayUtil.toLongArray(_groupIds)),
						assetTag -> !StringUtil.startsWith(
							assetTag.getName(), "L_CMP_TASK_")),
					AssetTag::getName)),
			assetTagName -> new SelectionFDSFilterItem(
				assetTagName, assetTagName));
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	private final AssetTagLocalService _assetTagLocalService;
	private final List<Long> _groupIds;

}