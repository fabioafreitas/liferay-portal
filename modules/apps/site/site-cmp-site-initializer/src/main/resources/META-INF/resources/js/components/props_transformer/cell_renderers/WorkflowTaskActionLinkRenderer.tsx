/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

import {WorkflowTaskItemData} from '../../../utils/types';

const _ACTION_ID = 'actionLinkWorkflowTask';

type WorkflowAction = {
	data?: {id?: string};
	href?: string;
};

const WorkflowTaskActionLinkRenderer = ({
	actions,
	itemData,
}: {
	actions: WorkflowAction[];
	itemData: WorkflowTaskItemData;
}) => {
	const title =
		itemData.embedded?.objectReviewed?.assetTitle ||
		Liferay.Language.get('untitled-asset');

	const selectedAction = findAction(actions, _ACTION_ID);

	const href = selectedAction?.href
		? replaceTokens(selectedAction.href, itemData)
		: null;

	if (!href) {
		return <>{title}</>;
	}

	return (
		<div className="align-items-center d-flex table-list-title">
			<ClayLink aria-label={title} data-senna-off href={href}>
				{title}
			</ClayLink>
		</div>
	);
};

export default WorkflowTaskActionLinkRenderer;
