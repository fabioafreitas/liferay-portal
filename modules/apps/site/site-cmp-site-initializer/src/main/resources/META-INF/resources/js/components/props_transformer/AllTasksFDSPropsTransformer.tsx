/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateRenderer,
	IInternalRenderer,
	IView,
} from '@liferay/frontend-data-set-web';
import {AssigneeValue} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {
	AssignToModalContent,
	SimpleActionLinkRenderer,
	UpdateDueDateModalContent,
	addOnClickToCreationMenuItems,
	deleteAssetEntriesBulkAction,
	deleteItemAction,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';
import React from 'react';

import {openCMPModal} from '../../utils/openCMPModal';
import StateLabel from '../StateLabel';
import BulkEditAssigneeModalContent from '../modal/BulkEditAssigneeModalContent';
import BulkEditDueDateModalContent from '../modal/BulkEditDueDateModalContent';
import BulkEditStateModalContent from '../modal/BulkEditStateModalContent';
import EditAssigneeModalContent from '../modal/EditAssigneeModalContent';
import ACTIONS from './actions/creationMenuActions';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';
import WorkflowAssigneeCell from './cell_renderers/WorkflowAssigneeCell';
import WorkflowStateCell from './cell_renderers/WorkflowStateCell';

const _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
	'com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken';

export type TaskAction = {
	data: {
		id: string;
	};
};

export interface TaskItemData {
	embedded: {
		assignTo: AssigneeValue | null | {};
		dateDue: string;
		externalReferenceCode: string;
		id: number;
		title: string;
	};
	entryClassName: string;
	id: number;
	title: string;
}

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

const styleActions = (actions: any[]): any[] =>
	actions.map((action) => {
		if (action?.data?.id === 'delete') {
			action.className = 'text-danger';
		}

		if (action.items) {
			action.items = styleActions(action.items);
		}

		return action;
	});

const isWorkflowRow = (itemData: any) =>
	itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN;

export default function AllTasksFDSPropsTransformer({
	additionalProps,
	creationMenu,
	id,
	itemsActions = [],
	views,
	...otherProps
}: {
	additionalProps: any;
	apiURL: string;
	creationMenu: any;
	id: string;
	itemsActions?: any[];
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
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData}) =>
						isWorkflowRow(itemData) ? (
							<WorkflowAssigneeCell
								embedded={itemData.embedded}
							/>
						) : (
							<AssigneeRenderer
								image={itemData.embedded?.assignTo?.portrait}
								name={itemData.embedded?.assignTo?.name}
							/>
						),
					name: 'assigneeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						DateRenderer({
							value: isWorkflowRow(itemData)
								? itemData.embedded?.dateDue
								: itemData.embedded?.dueDate,
						}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						isWorkflowRow(itemData)
							? '-'
							: itemData.embedded
									?.r_cmpProjectToCMPTasks_c_cmpProject?.title,
					name: 'projectTitleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({actions, itemData, options}) =>
						SimpleActionLinkRenderer({
							actions,
							itemData,
							options: isWorkflowRow(itemData)
								? {actionId: 'actionLinkWorkflowTask'}
								: options,
							value: isWorkflowRow(itemData)
								? itemData.embedded?.objectReviewed?.assetTitle
								: itemData.embedded?.title,
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						isWorkflowRow(itemData) ? (
							<WorkflowStateCell embedded={itemData.embedded} />
						) : (
							StateLabel({
								dueDate: itemData.embedded?.dueDate,
								state: itemData.embedded?.state,
							})
						),
					name: 'stateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		id,
		itemsActions: styleActions(itemsActions),
		async onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: TaskAction;
			itemData: TaskItemData;
			loadData: () => Promise<void>;
		}) {
			if (action?.data?.id === 'delete') {
				await deleteItemAction(
					sub(
						Liferay.Language.get('delete-task-confirmation-body'),
						itemData.embedded.title
					),
					itemData,
					loadData
				);
			}
			else if (action?.data?.id === 'assign-to') {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<EditAssigneeModalContent
							closeModal={closeModal}
							loadData={loadData}
							taskId={String(itemData.embedded.id)}
							taskTitle={itemData.embedded.title}
							value={itemData.embedded.assignTo}
						/>
					),
					size: 'md',
				});
			}

			if (isWorkflowRow(itemData)) {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						WORKFLOW_TASK_MODALS[action?.data?.id]({
							closeModal,
							dueDate: itemData.embedded?.dateDue,
							loadData,
							workflowTaskId: itemData.embedded?.id,
						}),
					size: 'md',
				});
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'assign-to') {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<BulkEditAssigneeModalContent
							apiURL={otherProps.apiURL}
							closeModal={closeModal}
							dataSetId={id}
							selectedData={selectedData}
							value={{name: null}}
						/>
					),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'delete') {
				deleteAssetEntriesBulkAction({
					apiURL: otherProps.apiURL,
					dataSetId: id,
					getCustomBulkDeleteMessage: (selectedData) => {
						if (selectedData.selectAll) {
							return {
								confirmationMessage: Liferay.Language.get(
									'delete-tasks-confirmation'
								),
								title: Liferay.Language.get('delete-all-tasks'),
							};
						}
						else if (selectedData.items.length > 1) {
							return {
								confirmationMessage: Liferay.Language.get(
									'delete-tasks-confirmation'
								),
								title: sub(
									Liferay.Language.get('delete-x-tasks'),
									[selectedData.items.length]
								),
							};
						}

						return {
							confirmationMessage: Liferay.Language.get(
								'delete-tasks-confirmation'
							),
							title: Liferay.Language.get('delete-task'),
						};
					},
					selectedData,
					showConfirmationModal: true,
				});
			}
			else if (action?.data?.id === 'update-due-date') {
				openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						BulkEditDueDateModalContent({
							apiURL: otherProps?.apiURL,
							closeModal,
							dataSetId: id,
							selectedData,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'update-state') {
				openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						BulkEditStateModalContent({
							apiURL: otherProps?.apiURL,
							closeModal,
							dataSetId: id,
							selectedData,
							states: additionalProps.states,
						}),
					size: 'md',
				});
			}
		},
		views: nonDefaultViews,
	};
}
