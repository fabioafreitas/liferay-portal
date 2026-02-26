/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class ProjectsPage {
	readonly dataSetFragmentPage: DataSetPage;
	readonly newProjectButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
		this.newProjectButton = page.getByRole('button', {
			name: 'New Project',
		});
		this.dataSetFragmentPage = new DataSetPage(page);
	}

	getProject(filter: string) {
		return this.dataSetFragmentPage.getRow(filter).getByRole('link', { name: filter });
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmpProjects);
	}
}
