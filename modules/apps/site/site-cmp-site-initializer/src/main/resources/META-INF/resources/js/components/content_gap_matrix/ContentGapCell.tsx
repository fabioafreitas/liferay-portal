/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {TaxonomyTerm} from './types';
import {isSentinel} from './utils';

export default function ContentGapCell({
	funnelStage,
	maxRealCount,
	persona,
	totalCount,
}: {
	funnelStage: TaxonomyTerm;
	maxRealCount: number;
	persona: TaxonomyTerm;
	totalCount: number;
}) {
	const sentinel = isSentinel(persona) || isSentinel(funnelStage);
	const gap = totalCount === 0;

	// Relative intensity within the project, scaled against the busiest real
	// cell. Isolated here so the scale is a one-line change later.

	const intensity =
		sentinel || maxRealCount === 0 ? 0 : totalCount / maxRealCount;

	return (
		<div
			aria-label={`${persona.name}, ${funnelStage.name}: ${totalCount}`}
			className={classNames('lfr-cmp__content-gap-cell', {
				'lfr-cmp__content-gap-cell--gap': gap,
				'lfr-cmp__content-gap-cell--sentinel': sentinel,
			})}
			role="gridcell"
		>
			{!gap && !sentinel && (
				<span
					aria-hidden="true"
					className="lfr-cmp__content-gap-cell-fill"
					style={{opacity: 0.15 + intensity * 0.85}}
				/>
			)}

			<span className="lfr-cmp__content-gap-cell-count">
				{totalCount}
			</span>
		</div>
	);
}
