/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {TaxonomyTerm} from './types';
import {getCellTier, isSentinel} from './utils';

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

	// Discrete fill tier (1..N) relative to the busiest real cell, or 0 for
	// gaps and sentinel cells, which are never filled. The scale itself lives
	// in getCellTier, so the color model is one swap away.

	const tier = gap || sentinel ? 0 : getCellTier(totalCount, maxRealCount);

	return (
		<div
			aria-label={`${persona.name}, ${funnelStage.name}: ${totalCount}`}
			className={classNames('lfr-cmp__content-gap-cell', {
				'lfr-cmp__content-gap-cell--gap': gap,
				'lfr-cmp__content-gap-cell--sentinel': sentinel,
				[`lfr-cmp__content-gap-cell--tier-${tier}`]: tier > 0,
			})}
			role="gridcell"
		>
			<span className="lfr-cmp__content-gap-cell-count">
				{totalCount}
			</span>
		</div>
	);
}
