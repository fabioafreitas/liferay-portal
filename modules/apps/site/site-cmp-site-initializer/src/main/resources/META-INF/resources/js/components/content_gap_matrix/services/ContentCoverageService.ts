/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MatrixData} from '../types';

/**
 * The single seam between the matrix UI and its data source.
 *
 * Components above this interface never know whether the counts are
 * aggregated server-side, bucketed client-side from a faceted search, or
 * served from fixtures. Swapping the implementation is a one-line change in
 * the Card.
 */
export interface ContentCoverageService {
	getMatrix(projectId: string): Promise<MatrixData>;
}

/**
 * Real implementation placeholder. When the aggregation source is decided
 * (a headless-cmp REST resource or client-side bucketing of /o/search), this
 * resolves the persona / funnel-stage vocabularies and returns the same
 * MatrixData shape the mock returns today.
 */
export const ContentCoverageServiceImpl: ContentCoverageService = {
	getMatrix() {
		return Promise.reject(
			new Error('ContentCoverageService real implementation is pending')
		);
	},
};
