/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {FeatureFlagsInstanceSettingsPage} from '../../../feature-flag-web/main/pages/FeatureFlagsInstanceSettingsPage';

const cmpSetupTest = loginTest().extend<{
	setup: void;
}>({
	setup: [
		async ({page}, use) => {
			const apiHelpers = new ApiHelpers(page);

			await apiHelpers.featureFlag.updateFeatureFlag('LPD-58677', true);

			const featureFlagsInstanceSettingsPage =
				new FeatureFlagsInstanceSettingsPage(page);

			await featureFlagsInstanceSettingsPage.goto('Beta');

			await page
				.getByPlaceholder('Search For')
				.fill('Content Marketing Platform (CMP)');

			await page.getByRole('button', {name: 'Search for'}).click();

			const toggle = page.getByRole('switch', {
				name: 'Content Marketing Platform (CMP)',
			});

			await toggle.setChecked(true);

			await expect(toggle).toBeChecked();

			const space = await apiHelpers.headlessAssetLibrary
				.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					settings: {},
					type: 'Space',
				});

			await use();

			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(space.externalReferenceCode);

			await apiHelpers.featureFlag.updateFeatureFlag('LPD-58677', false);
		},
		{auto: true},
	],
});

export {cmpSetupTest};
