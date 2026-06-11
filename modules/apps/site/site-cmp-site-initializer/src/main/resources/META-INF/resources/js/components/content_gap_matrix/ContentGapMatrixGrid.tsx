/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ContentGapCell from './ContentGapCell';
import {MatrixData} from './types';
import {buildCountLookup, cellKey, getMaxRealCount} from './utils';

export default function ContentGapMatrixGrid({data}: {data: MatrixData}) {
	const {funnelStages, personas} = data;

	const countLookup = buildCountLookup(data.cells);
	const maxRealCount = getMaxRealCount(data);

	return (
		<div
			aria-colcount={funnelStages.length + 1}
			aria-rowcount={personas.length + 1}
			className="lfr-cmp__content-gap-matrix-grid"
			role="grid"
			style={{
				gridTemplateColumns: `minmax(8rem, auto) repeat(${funnelStages.length}, minmax(0, 1fr))`,
			}}
		>
			<div
				className="lfr-cmp__content-gap-matrix-row"
				role="row"
				style={{display: 'contents'}}
			>
				<div
					className="lfr-cmp__content-gap-matrix-corner"
					role="columnheader"
				/>

				{funnelStages.map((funnelStage) => (
					<div
						className="lfr-cmp__content-gap-matrix-column-header"
						key={funnelStage.id}
						role="columnheader"
						title={funnelStage.description}
					>
						{funnelStage.name}
					</div>
				))}
			</div>

			{personas.map((persona) => (
				<div
					className="lfr-cmp__content-gap-matrix-row"
					key={persona.id}
					role="row"
					style={{display: 'contents'}}
				>
					<div
						className="lfr-cmp__content-gap-matrix-row-header"
						role="rowheader"
						title={persona.description}
					>
						{persona.name}
					</div>

					{funnelStages.map((funnelStage) => (
						<ContentGapCell
							funnelStage={funnelStage}
							key={funnelStage.id}
							maxRealCount={maxRealCount}
							persona={persona}
							totalCount={
								countLookup.get(
									cellKey(persona.id, funnelStage.id)
								) ?? 0
							}
						/>
					))}
				</div>
			))}
		</div>
	);
}
