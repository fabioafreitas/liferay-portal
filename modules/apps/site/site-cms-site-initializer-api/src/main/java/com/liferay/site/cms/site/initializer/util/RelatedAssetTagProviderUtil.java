/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Fábio Alves
 */
public class RelatedAssetTagProviderUtil {

	public static Set<String> getRelatedAssetTagNames(
			long parentObjectEntryId, String relationshipErc,
			AssetTagLocalService assetTagLocalService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService)
		throws PortalException {

		Set<String> tagNames = new HashSet<>();

		ObjectEntry parentObjectEntry = objectEntryLocalService.getObjectEntry(
			parentObjectEntryId);

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					relationshipErc,
					parentObjectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return tagNames;
		}

		for (ObjectEntry childObjectEntry :
				objectEntryLocalService.getOneToManyObjectEntries(
					parentObjectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(), null, false,
					parentObjectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			tagNames.addAll(
				_getTagNames(
					objectDefinitionLocalService.fetchObjectDefinition(
						childObjectEntry.getObjectDefinitionId()),
					childObjectEntry, assetTagLocalService));
		}

		return tagNames;
	}

	private static Set<String> _getTagNames(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		AssetTagLocalService assetTagLocalService) {

		List<AssetTag> assetTags = assetTagLocalService.getTags(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		return new HashSet<>(
			TransformUtil.transform(
				assetTags,
				assetTag -> {
					if (!StringUtil.startsWith(
							assetTag.getName(),
							objectDefinition.getExternalReferenceCode())) {

						return null;
					}

					return assetTag.getName();
				}));
	}

}
