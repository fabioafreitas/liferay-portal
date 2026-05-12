/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function styleActions(actions: any[]) {
	return actions.map((action) => {
		if (action?.data?.id === 'delete') {
			action.className = 'text-danger';
		}

		if (action.items) {
			action.items = styleActions(action.items);
		}

		return action;
	});
}

export function styleBulkActions(bulkActions: any[]) {
	return bulkActions.map((action) =>
		action?.data?.id === 'delete'
			? {
					...action,
					data: {
						...action.data,
						className: 'text-danger',
					},
				}
			: action
	);
}
