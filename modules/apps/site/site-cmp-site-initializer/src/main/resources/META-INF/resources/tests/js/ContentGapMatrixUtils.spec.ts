/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FULL_COVERAGE_MATRIX,
	PARTIAL_COVERAGE_MATRIX,
	UNCATEGORIZED_MATRIX,
} from '../../js/components/content_gap_matrix/services/fixtures';
import {
	CELL_TIER_COUNT,
	getCellTier,
	getMaxRealCount,
} from '../../js/components/content_gap_matrix/utils';

describe('content gap matrix color scale', () => {
	describe('getMaxRealCount', () => {
		it('returns the busiest real cell', () => {
			expect(getMaxRealCount(PARTIAL_COVERAGE_MATRIX)).toBe(10);
			expect(getMaxRealCount(FULL_COVERAGE_MATRIX)).toBe(9);
		});

		it('ignores sentinel rows and columns so uncategorized buckets do not wash out the scale', () => {

			// The only counts live in the No Persona / No Funnel sentinel cell.

			expect(getMaxRealCount(UNCATEGORIZED_MATRIX)).toBe(0);
		});
	});

	describe('getCellTier', () => {
		it('returns 0 for an empty cell so it renders as a gap, never a fill', () => {
			expect(getCellTier(0, 10)).toBe(0);
		});

		it('returns 0 when there is no busiest cell to scale against', () => {
			expect(getCellTier(5, 0)).toBe(0);
		});

		it('places any positive count in at least tier 1 so a single asset is visible', () => {
			expect(getCellTier(1, 1000)).toBe(1);
		});

		it('scales counts across the tiers relative to the busiest cell', () => {
			expect(getCellTier(1, 10)).toBe(1);
			expect(getCellTier(3, 10)).toBe(2);
			expect(getCellTier(6, 10)).toBe(3);
			expect(getCellTier(8, 10)).toBe(4);
		});

		it('puts the busiest cell in the top tier', () => {
			expect(getCellTier(10, 10)).toBe(CELL_TIER_COUNT);
		});

		it('clamps counts above the max to the top tier', () => {
			expect(getCellTier(20, 10)).toBe(CELL_TIER_COUNT);
		});
	});
});
