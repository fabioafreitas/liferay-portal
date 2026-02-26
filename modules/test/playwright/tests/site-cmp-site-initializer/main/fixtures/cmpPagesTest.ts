/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditTaskPage} from '../pages/EditTaskPage';
import {ProjectPage} from '../pages/ProjectPage';
import {ProjectsPage} from '../pages/ProjectsPage';
import {TasksPage} from '../pages/TasksPage';

const cmpPagesTest = test.extend<{
	editTaskPage: EditTaskPage
	projectPage: ProjectPage;
	projectsPage: ProjectsPage;
	tasksPage: TasksPage;
}>({
	editTaskPage: async ({page}, use) => {
		await use(new EditTaskPage(page));
	},
	projectPage: async ({page}, use) => {
		await use(new ProjectPage(page));
	},
	projectsPage: async ({page}, use) => {
		await use(new ProjectsPage(page));
	},
	tasksPage: async ({page}, use) => {
		await use(new TasksPage(page));
	},
});

export {cmpPagesTest};
