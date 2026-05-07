/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateRenderer,
	IInternalRenderer,
	IView,
} from '@liferay/frontend-data-set-web';
import {
	AssignToModalContent,
	SimpleActionLinkRenderer,
	UpdateDueDateModalContent,
} from '@liferay/site-cms-site-initializer';
import React from 'react';

import {openCMPModal} from '../../utils/openCMPModal';
import {TaskAction, TaskItemData} from './AllTasksFDSPropsTransformer';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';
import WorkflowStateCell from './cell_renderers/WorkflowStateCell';

const WORKFLOW_TASK_MODALS: Record<
	string,
	(baseProps: {
		closeModal: () => void;
		dueDate: string;
		loadData: () => Promise<void>;
		workflowTaskId: number;
	}) => JSX.Element
> = {
	assignToMeWorkflowTask: (props) => (
		<AssignToModalContent {...props} assignable={false} />
	),
	assignToWorkflowTask: (props) => (
		<AssignToModalContent {...props} assignable={true} />
	),
	updateDueDateWorkflowTask: (props) => (
		<UpdateDueDateModalContent {...props} />
	),
};

export default function WorkflowTasksFDSPropsTransformer({
	id,
	views,
	...otherProps
}: {
	apiURL: string;
	id: string;
	otherProps: any;
	views: IView[];
}) {
	const nonDefaultViews = views.map((view) => ({
		...view,
		default: false,
		initialPaginationDelta: 20,
	}));

	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: ({actions, itemData}) =>
						SimpleActionLinkRenderer({
							actions,
							itemData,
							options: {actionId: 'actionLinkWorkflowTask'},
							value: itemData.embedded?.objectReviewed?.assetTitle,
						}),
					name: 'assetTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.objectReviewed?.assetType ?? '-',
					name: 'assetTypeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) => (
						<AssigneeRenderer
							image={itemData.embedded?.creatorPerson?.image}
							name={itemData.embedded?.creatorPerson?.name}
						/>
					),
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.title ?? itemData.embedded?.name,
					name: 'taskTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.dateDue}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						itemData.embedded?.objectReviewed?.projectTitle ?? '-',
					name: 'projectTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) => (
						<WorkflowStateCell embedded={itemData.embedded} />
					),
					name: 'workflowStateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({value: itemData.embedded?.modifiedDate}),
					name: 'lastActivityDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		id,
		async onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: TaskAction;
			itemData: TaskItemData;
			loadData: () => Promise<void>;
		}) {
			const modal = WORKFLOW_TASK_MODALS[action?.data?.id];

			if (!modal) {
				return;
			}

			await openCMPModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: () => void}) =>
					modal({
						closeModal,
						dueDate: itemData.embedded?.dateDue,
						loadData,
						workflowTaskId: itemData.embedded?.id,
					}),
				size: 'md',
			});
		},
		views: nonDefaultViews,
	};
}
