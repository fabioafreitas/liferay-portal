/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {MatrixData} from './types';
import {computeCoveragePercentage, countCriticalGaps} from './utils';

export default function ContentGapMatrixHeader({data}: {data: MatrixData}) {
	const coveragePercentage = computeCoveragePercentage(data);
	const criticalGaps = countCriticalGaps(data);

	return (
		<div className="lfr-cmp__content-gap-matrix-header">
			<div>
				<h5 className="c-mb-1 text-uppercase">
					{Liferay.Language.get('content-coverage-matrix')}
				</h5>

				<p className="c-mb-0 text-2 text-secondary">
					{Liferay.Language.get(
						'amount-of-assets-per-persona-and-funnel-stage'
					)}
				</p>
			</div>

			<div className="lfr-cmp__content-gap-matrix-header-stats">
				<span className="text-weight-semi-bold">
					{`${coveragePercentage}% ${Liferay.Language.get(
						'covered'
					)}`}
				</span>

				<span className="text-danger text-weight-semi-bold">
					{`${criticalGaps} ${Liferay.Language.get('critical-gaps')}`}
				</span>
			</div>
		</div>
	);
}
