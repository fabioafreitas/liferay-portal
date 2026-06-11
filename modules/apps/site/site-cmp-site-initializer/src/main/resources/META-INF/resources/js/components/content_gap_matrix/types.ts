/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Content Coverage Matrix contract.
 *
 * MatrixData carries only facts (the axes, the cell counts, the scope total).
 * Everything the header and cells show (coverage %, critical gaps, color
 * intensity, empty states) is derived in the front end, never stored here, so
 * the shape stays stable when the producer flips from the mock to a real
 * service.
 */

export interface TaxonomyTerm {
	description?: string;
	externalReferenceCode: string | null;
	id: string;
	name: string;

	// True only for the "No Persona" / "No Funnel" sentinel row and column.

	uncategorized?: boolean;
}

export interface MatrixCell {
	funnelStageId: string;
	personaId: string;

	// Distinct assets tagged with BOTH terms. Overlap across cells is expected
	// under multi-valued tagging and is not deduped.

	totalCount: number;
}

export interface MatrixData {

	// Sparse tolerant: a missing (personaId, funnelStageId) pair means 0.

	cells: MatrixCell[];

	funnelStages: TaxonomyTerm[];
	personas: TaxonomyTerm[];

	// Distinct assets in scope, supplied by the producer. NOT sum(cells), since
	// a multi-tagged asset lands in several cells.

	totalAssetCount: number;
}

export const NO_PERSONA: TaxonomyTerm = {
	externalReferenceCode: null,
	id: 'no-persona',
	name: 'No Persona',
	uncategorized: true,
};

export const NO_FUNNEL_STAGE: TaxonomyTerm = {
	externalReferenceCode: null,
	id: 'no-funnel-stage',
	name: 'No Funnel',
	uncategorized: true,
};
