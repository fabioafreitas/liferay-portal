/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class ProjectPage {
	readonly newTaskButton: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly detailsTab: Locator;
	readonly tasksTab: Locator;

	constructor(page: Page) {
		this.page = page;
		this.newButton = page.getByRole('button', {
			name: 'New',
		});
		this.newTaskButton = page.getByRole('button', {
			name: 'New Task',
		});
		this.detailsTab = page.getByRole('tab', {
			name: 'Details',
		});
		this.tasksTab = page.getByRole('tab', {
			name: 'Tasks',
		});
	}

}
