/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ContentGapMatrixHeader from '../../js/components/content_gap_matrix/ContentGapMatrixHeader';
import {
	EMPTY_MATRIX,
	PARTIAL_COVERAGE_MATRIX,
} from '../../js/components/content_gap_matrix/services/fixtures';

// Liferay.Language.get returns the key in the jest setup, so assertions match
// on the key string.

describe('ContentGapMatrixHeader', () => {
	it('shows the critical gaps count when the project has assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		expect(
			getByText('critical-gaps', {exact: false})
		).toBeInTheDocument();
		expect(queryByText('no-assets-found')).not.toBeInTheDocument();
	});

	it('replaces the critical gaps count with "No Assets Found" when the project has no assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={EMPTY_MATRIX} />
		);

		expect(getByText('no-assets-found')).toBeInTheDocument();
		expect(
			queryByText('critical-gaps', {exact: false})
		).not.toBeInTheDocument();
		expect(getByText('0% covered')).toBeInTheDocument();
	});
});
