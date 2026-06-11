/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import React, {useCallback, useEffect, useState} from 'react';

import ContentGapMatrix from './ContentGapMatrix';
import {ContentCoverageServiceMock} from './services/ContentCoverageServiceMock';
import {MatrixData} from './types';
import {hasCategorizedAssets} from './utils';

import './ContentGapMatrix.scss';

interface Props {

	// Reserved for the real service: the scope and vocabulary identifiers the
	// aggregation will resolve. Unused by the mock, kept so the Java renderer
	// stays stable across the mock-to-real swap.

	cmsGroupId?: string;
	funnelStageVocabularyExternalReferenceCode?: string;
	personaVocabularyExternalReferenceCode?: string;
	projectId: string;
}

// Swap this single line for the real implementation when the backend lands.

const contentCoverageService = ContentCoverageServiceMock;

export default function ContentGapMatrixCard({projectId}: Props) {
	const [data, setData] = useState<MatrixData | null>(null);
	const [loading, setLoading] = useState(true);

	const fetchMatrix = useCallback(async () => {
		setLoading(true);

		setData(await contentCoverageService.getMatrix(projectId));

		setLoading(false);
	}, [projectId]);

	useEffect(() => {
		fetchMatrix();
	}, [fetchMatrix]);

	useEffect(() => {
		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, fetchMatrix);

		return () => {
			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, fetchMatrix);
		};
	}, [fetchMatrix]);

	return (
		<div className="lfr-cmp__content-gap-matrix-container">
			{loading || !data ? (
				<ClayLoadingIndicator />
			) : data.totalAssetCount === 0 ? (
				<>
					<ContentGapMatrix data={data} />

					<div className="empty-state">
						<ClayEmptyState
							description={Liferay.Language.get(
								'add-assets-to-this-project-to-track-coverage'
							)}
							title={Liferay.Language.get('no-assets-yet')}
						/>
					</div>
				</>
			) : !hasCategorizedAssets(data) ? (
				<div className="empty-state">
					<ClayEmptyState
						description={Liferay.Language.get(
							'categorize-assets-with-personas-and-funnel-stages-to-see-coverage'
						)}
						title={Liferay.Language.get('start-mapping')}
					/>
				</div>
			) : (
				<ContentGapMatrix data={data} />
			)}
		</div>
	);
}
