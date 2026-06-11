/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ContentGapMatrixGrid from './ContentGapMatrixGrid';
import ContentGapMatrixHeader from './ContentGapMatrixHeader';
import {MatrixData} from './types';

import './ContentGapMatrix.scss';

/**
 * Pure presentational matrix. Receives a complete MatrixData and renders the
 * derived header and the grid. No fetching, no state.
 */
export default function ContentGapMatrix({data}: {data: MatrixData}) {
	return (
		<div className="lfr-cmp__content-gap-matrix">
			<ContentGapMatrixHeader data={data} />

			<ContentGapMatrixGrid data={data} />
		</div>
	);
}
