/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import WorkflowTaskActionLinkRenderer from '../../js/components/props_transformer/cell_renderers/WorkflowTaskActionLinkRenderer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: jest.fn(),
	replaceTokens: jest.fn(),
}));

const {findAction, replaceTokens} = jest.requireMock(
	'@liferay/frontend-data-set-web'
);

const itemDataWithAssetTitle = {
	embedded: {objectReviewed: {assetTitle: 'My Asset'}},
} as any;

const itemDataWithoutAssetTitle = {
	embedded: {objectReviewed: {assetTitle: ''}},
} as any;

describe('WorkflowTaskActionLinkRenderer', () => {
	beforeEach(() => {
		findAction.mockReset();
		replaceTokens.mockReset();
	});

	it('renders a link when actionLinkWorkflowTask action resolves to an href', () => {
		findAction.mockReturnValue({href: '/raw/{id}'});
		replaceTokens.mockReturnValue('/resolved/42');

		const {getByRole} = render(
			<WorkflowTaskActionLinkRenderer
				actions={[]}
				itemData={itemDataWithAssetTitle}
			/>
		);

		const link = getByRole('link', {name: 'My Asset'});

		expect(link).toHaveAttribute('href', '/resolved/42');
	});

	it('renders the title without a link when no actionLinkWorkflowTask action is present', () => {
		findAction.mockReturnValue(undefined);

		const {getByText, queryByRole} = render(
			<WorkflowTaskActionLinkRenderer
				actions={[]}
				itemData={itemDataWithAssetTitle}
			/>
		);

		expect(getByText('My Asset')).toBeInTheDocument();
		expect(queryByRole('link')).not.toBeInTheDocument();
	});

	it('falls back to "untitled-asset" when assetTitle is empty', () => {
		findAction.mockReturnValue(undefined);

		const {getByText} = render(
			<WorkflowTaskActionLinkRenderer
				actions={[]}
				itemData={itemDataWithoutAssetTitle}
			/>
		);

		expect(getByText('untitled-asset')).toBeInTheDocument();
	});
});
