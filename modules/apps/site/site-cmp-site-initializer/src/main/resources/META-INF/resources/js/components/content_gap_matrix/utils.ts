/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MatrixCell, MatrixData, TaxonomyTerm} from './types';

/**
 * Pure derivations over MatrixData. Every value the header and cells display
 * is computed here, so the contract stays free of presentation concerns and
 * each formula is a single, swappable place.
 */

export function isSentinel(term: TaxonomyTerm): boolean {
	return Boolean(term.uncategorized);
}

export function cellKey(personaId: string, funnelStageId: string): string {
	return `${personaId}:${funnelStageId}`;
}

export function buildCountLookup(cells: MatrixCell[]): Map<string, number> {
	const lookup = new Map<string, number>();

	cells.forEach((cell) =>
		lookup.set(cellKey(cell.personaId, cell.funnelStageId), cell.totalCount)
	);

	return lookup;
}

function sentinelIds(terms: TaxonomyTerm[]): Set<string> {
	return new Set(terms.filter(isSentinel).map((term) => term.id));
}

function isRealCell(
	cell: MatrixCell,
	sentinelPersonaIds: Set<string>,
	sentinelStageIds: Set<string>
): boolean {
	return (
		!sentinelPersonaIds.has(cell.personaId) &&
		!sentinelStageIds.has(cell.funnelStageId)
	);
}

/**
 * Highest count among the real (non-sentinel) cells. Sentinel cells are
 * excluded so a heavy uncategorized bucket does not wash out the real grid.
 * Drives the color intensity scale (relative within the project).
 */
export function getMaxRealCount(data: MatrixData): number {
	const sentinelPersonaIds = sentinelIds(data.personas);
	const sentinelStageIds = sentinelIds(data.funnelStages);

	return data.cells.reduce(
		(max, cell) =>
			isRealCell(cell, sentinelPersonaIds, sentinelStageIds)
				? Math.max(max, cell.totalCount)
				: max,
		0
	);
}

/**
 * Number of non-empty color tiers. Counts collapse onto one of these discrete
 * steps (1..N) rather than a continuous gradient: the eye can rank a handful
 * of shades and spot the gaps between them, where it cannot rank a smooth
 * ramp. Tier 0 is reserved for empty cells, which are never filled.
 */
export const CELL_TIER_COUNT = 4;

/**
 * Maps a cell count to a fill tier in 1..CELL_TIER_COUNT, relative to the
 * busiest real cell, so the scale auto-fits both small and large projects with
 * no configured thresholds. Any positive count lands in at least tier 1, so a
 * single asset is never rendered invisible; 0 returns 0 (rendered as a gap).
 *
 * Linear by design, matching the project-relative intent. For a heavily skewed
 * project (a few huge cells, many small) swap the ratio for a log curve here —
 * Math.log1p(count) / Math.log1p(maxRealCount) — and every cell follows.
 */
export function getCellTier(count: number, maxRealCount: number): number {
	if (count <= 0 || maxRealCount <= 0) {
		return 0;
	}

	const intensity = Math.min(1, count / maxRealCount);

	return Math.max(1, Math.ceil(intensity * CELL_TIER_COUNT));
}

/**
 * Placeholder Coverage % (pending the product definition): filled real cells
 * over total real cells.
 */
export function computeCoveragePercentage(data: MatrixData): number {
	const realPersonaCount = data.personas.filter(
		(term) => !isSentinel(term)
	).length;
	const realStageCount = data.funnelStages.filter(
		(term) => !isSentinel(term)
	).length;

	const totalRealCells = realPersonaCount * realStageCount;

	if (totalRealCells === 0) {
		return 0;
	}

	const sentinelPersonaIds = sentinelIds(data.personas);
	const sentinelStageIds = sentinelIds(data.funnelStages);

	const filledRealCells = data.cells.filter(
		(cell) =>
			cell.totalCount > 0 &&
			isRealCell(cell, sentinelPersonaIds, sentinelStageIds)
	).length;

	return Math.round((filledRealCells / totalRealCells) * 100);
}

/**
 * Critical gaps: cells with a zero count across the full grid, including the
 * sentinel row and column (matches the mockup's "7 Critical Gaps").
 */
export function countCriticalGaps(data: MatrixData): number {
	const totalCells = data.personas.length * data.funnelStages.length;

	const filledCells = data.cells.filter((cell) => cell.totalCount > 0).length;

	return totalCells - filledCells;
}

/**
 * True when at least one real (non-sentinel) cell has assets. False means the
 * project has assets but none are categorized, which drives "Start Mapping".
 */
export function hasCategorizedAssets(data: MatrixData): boolean {
	const sentinelPersonaIds = sentinelIds(data.personas);
	const sentinelStageIds = sentinelIds(data.funnelStages);

	return data.cells.some(
		(cell) =>
			cell.totalCount > 0 &&
			isRealCell(cell, sentinelPersonaIds, sentinelStageIds)
	);
}
