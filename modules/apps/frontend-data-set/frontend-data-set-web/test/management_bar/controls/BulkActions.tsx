/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import BulkActions from '../../../src/main/resources/META-INF/resources/management_bar/controls/BulkActions';
import {IBulkActionItem} from '../../../src/main/resources/META-INF/resources/utils/types';

const onBulkActionItemClick = jest.fn();

const baseContext = {
	actionParameterName: undefined,
	allItemsSelectedActive: false,
	apiURL: '/o/test/items',
	formId: undefined,
	formName: undefined,
	globalFDSState: {filters: [], search: {query: ''}},
	loadData: () => {},
	namespace: undefined,
	onBulkActionItemClick,
	searchParam: undefined,
	showBulkActionsManagementBar: true,
	showBulkActionsManagementBarActions: true,
	showInfoPanel: false,
	sidePanelId: undefined,
};

const renderBulkActions = (
	bulkActions: IBulkActionItem[],
	contextOverrides = {}
) =>
	render(
		<FrontendDataSetContext.Provider
			value={{...baseContext, ...contextOverrides} as any}
		>
			<BulkActions
				bulkActions={bulkActions}
				handleSelectAll={() => {}}
				items={[{id: 1}, {id: 2}]}
				onClear={() => {}}
				pageSelectedItemsValue={[1]}
				selectedItems={[{id: 1}]}
				selectedItemsKey="id"
				selectedItemsValue={[1]}
				total={2}
			/>
		</FrontendDataSetContext.Provider>
	);

describe('BulkActions disabled state (LPD-88846)', () => {
	beforeEach(() => {
		onBulkActionItemClick.mockClear();
	});

	it('renders a highlighted disabled bulk action with the supplied className and short-circuits click', async () => {
		const bulkActions: IBulkActionItem[] = [
			{
				data: {
					className: 'text-danger',
					disabled: true,
					highlighted: true,
					id: 'delete',
				},
				icon: 'trash',
				label: 'Delete',
			},
		];

		renderBulkActions(bulkActions);

		const button = screen.getByRole('button', {name: /delete/i});

		expect(button).toBeDisabled();
		expect(button).toHaveClass('text-danger');

		await userEvent.click(button);

		expect(onBulkActionItemClick).not.toHaveBeenCalled();
	});

	it('renders an overflow dropdown item as disabled with the supplied className', async () => {
		const bulkActions: IBulkActionItem[] = [
			{
				data: {
					className: 'text-danger',
					disabled: true,
					id: 'delete',
				},
				icon: 'trash',
				label: 'Delete',
			},
		];

		renderBulkActions(bulkActions);

		await userEvent.click(
			screen.getByRole('button', {name: /actions/i})
		);

		const dropdownItem = screen.getByRole('menuitem', {name: /delete/i});

		expect(dropdownItem).toHaveClass('text-danger');

		await userEvent.click(dropdownItem);

		// data.disabled short-circuits handleActionClick at
		// BulkActions.tsx:118-120 regardless of whether ClayUI
		// suppresses pointer events on the menuitem.

		expect(onBulkActionItemClick).not.toHaveBeenCalled();
	});

	it('renders an enabled highlighted bulk action that fires onBulkActionItemClick', async () => {
		const bulkActions: IBulkActionItem[] = [
			{
				data: {highlighted: true, id: 'update'},
				icon: 'pencil',
				label: 'Update',
			},
		];

		renderBulkActions(bulkActions);

		const button = screen.getByRole('button', {name: /update/i});

		expect(button).not.toBeDisabled();

		await userEvent.click(button);

		expect(onBulkActionItemClick).toHaveBeenCalledTimes(1);
	});
});
