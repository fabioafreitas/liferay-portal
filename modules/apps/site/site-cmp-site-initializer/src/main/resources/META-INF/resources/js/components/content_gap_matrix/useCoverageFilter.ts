/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBaseFilterState,
	IFDSState,
	getOrCreateFDSAtom,
} from '@liferay/frontend-data-set-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {useCallback, useMemo} from 'react';

import {MatrixData, TaxonomyTerm, UNCATEGORIZED_ID} from './types';

/**
 * Ids of the asset data set's persona and funnel-stage category filters. Each
 * targets the internal-category field scoped to its own vocabulary; the data
 * set ANDs them so a cell click narrows to assets carrying both categories.
 */
const FUNNEL_STAGE_FILTER_ID = 'cmpFunnelStageCategoryIds';
const PERSONA_FILTER_ID = 'cmpPersonaCategoryIds';

interface CoverageFilter {

	/**
	 * Filters the project's asset data set by a persona and a funnel-stage
	 * category.
	 */
	applyFilter: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;

	/**
	 * Category ids currently selected in the persona and funnel-stage filters,
	 * used to highlight the matching cell. The uncategorized sentinel ("-1")
	 * stands for a filter set to exclude every real category on that axis.
	 */
	selectedCategoryIds: Set<string>;
}

/**
 * Builds the selected data for one axis. A real category selects itself; the
 * uncategorized sentinel excludes every real category on that axis, which is
 * the project-relative "no persona" / "no funnel stage" query.
 */
function buildSelectedData(
	term: TaxonomyTerm,
	realTerms: TaxonomyTerm[]
): Record<string, unknown> {
	if (term.uncategorized) {
		return {
			exclude: true,
			selectedItems: realTerms.map((realTerm) => ({
				label: realTerm.name,
				value: realTerm.id,
			})),
		};
	}

	return {
		exclude: false,
		selectedItems: [{label: term.name, value: term.id}],
	};
}

/**
 * Bridges the matrix to the project's asset data set: it writes to the data
 * set's own state atom, resolved by its id, and reads back which categories are
 * filtered so the matrix can highlight the selected cell.
 */
export function useCoverageFilter(
	assetFDSId: string,
	data: MatrixData
): CoverageFilter {
	const assetFDSAtom = useMemo(
		() => getOrCreateFDSAtom({fdsName: assetFDSId}),
		[assetFDSId]
	);

	const [assetFDSState, setAssetFDSState] =
		useLiferayState<IFDSState>(assetFDSAtom);

	const {funnelStages, personas} = data;

	const applyFilter = useCallback(
		(persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => {
			const selectedDataByFilterId: Record<
				string,
				Record<string, unknown>
			> = {
				[FUNNEL_STAGE_FILTER_ID]: buildSelectedData(
					funnelStage,
					funnelStages.filter((term) => !term.uncategorized)
				),
				[PERSONA_FILTER_ID]: buildSelectedData(
					persona,
					personas.filter((term) => !term.uncategorized)
				),
			};

			setAssetFDSState({
				...assetFDSState,
				filters: (assetFDSState?.filters ?? []).map(
					(filter: IBaseFilterState) => {
						const selectedData = selectedDataByFilterId[filter.id];

						if (!selectedData) {
							return filter;
						}

						return {
							...filter,
							active: true,
							selectedData,
						};
					}
				),
			});
		},
		[assetFDSState, funnelStages, personas, setAssetFDSState]
	);

	const selectedCategoryIds = useMemo(() => {
		const categoryIds = new Set<string>();

		for (const filter of assetFDSState?.filters ?? []) {
			if (
				(filter.id !== FUNNEL_STAGE_FILTER_ID &&
					filter.id !== PERSONA_FILTER_ID) ||
				!filter.active
			) {
				continue;
			}

			const selectedData = filter.selectedData as
				| {exclude?: boolean; selectedItems?: Array<{value: string}>}
				| undefined;

			if (selectedData?.exclude) {
				categoryIds.add(UNCATEGORIZED_ID);
			}
			else {
				for (const item of selectedData?.selectedItems ?? []) {
					categoryIds.add(String(item.value));
				}
			}
		}

		return categoryIds;
	}, [assetFDSState]);

	return {applyFilter, selectedCategoryIds};
}
