/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {Response, expect, mergeTests} from '@playwright/test';
import {readFile} from 'fs/promises';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {getTempDir} from '../../../utils/temp';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getFilePath} from '../utils/fileHelpers';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-36105': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

type ObjectFieldEntry = {
	assertValue?: string | string[];
	businessType: ObjectField['businessType'];
	field?: Partial<ObjectField>;
	fillValue?: string | string[];
};

async function assertObjectField(page, entry: ObjectFieldEntry): Promise<void> {
	if (!entry.assertValue || !entry.field) {
		return;
	}

	if (entry.businessType === 'Boolean') {
		expect(page.getByLabel(entry.field.label['en_US'])).toBeChecked({
			checked: entry.assertValue === 'true',
		});

		return;
	}
	else if (entry.businessType === 'MultiselectPicklist') {
		for (const value of entry.assertValue as string[]) {
			await expect(
				page.getByRole('gridcell', {
					exact: true,
					name: value,
				})
			).toBeVisible();
		}

		return;
	}
	else if (entry.businessType === 'Picklist') {
		await expect(
			page.getByRole('combobox', {name: entry.field.label['en_US']})
		).toHaveText(entry.assertValue as string);

		return;
	}
	else if (entry.businessType === 'RichText') {
		await expect(
			page
				.getByRole('textbox', {name: entry.field.label['en_US']})
				.frameLocator('iframe')
				.getByRole('textbox')
		).toHaveText(entry.assertValue as string);

		return;
	}

	expect(page.getByLabel(entry.field.label['en_US'])).toHaveValue(
		entry.assertValue as string
	);
}

async function deleteObjectDefinitionFromData(apiHelpers, objectDefinitionId) {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const dataIndex = apiHelpers.data.findIndex(
		(item) =>
			item.id === objectDefinitionId && item.type === 'objectDefinition'
	);

	if (dataIndex !== -1) {
		apiHelpers.data.splice(dataIndex, 1);
	}

	await objectDefinitionAPIClient.deleteObjectDefinition(objectDefinitionId);
}

async function exportObjectDefinition(
	page,
	viewObjectDefinitionsPage,
	objectDefinitionLabel: string
) {
	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.searchInput.fill(objectDefinitionLabel);

	await viewObjectDefinitionsPage.page.keyboard.press('Enter');

	const downloadPromise = page.waitForEvent('download');

	const row = page.getByRole('row', {name: objectDefinitionLabel});

	await row.getByRole('button', {name: 'Actions'}).click();

	await page
		.getByRole('menuitem', {name: 'Export Object Definition'})
		.click();

	const download = await downloadPromise;

	const filePath = path.join(getTempDir(), download.suggestedFilename());

	await download.saveAs(filePath);

	const content = await readFile(filePath, 'utf-8');

	return {content, filePath, jsonContent: JSON.parse(content)};
}

async function fillObjectField(
	page,
	viewObjectEntriesPage,
	entry: ObjectFieldEntry
): Promise<void> {
	if (entry.fillValue === undefined || !entry.field) {
		return;
	}

	if (entry.businessType === 'MultiselectPicklist') {
		for (const option of entry.fillValue as string[]) {
			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'MultiselectPicklist',
				objectFieldName: entry.field.name,
				objectFieldValue: option as string,
			});
		}

		return;
	}

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: entry.businessType,
		objectFieldLabel: entry.field.label['en_US'],
		objectFieldValue: entry.fillValue as string,
	});
}

async function importObjectDefinition(
	page,
	viewObjectDefinitionsPage,
	filePath: string,
	objectName: string
): Promise<number> {
	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.objectFolderActions.click();

	await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

	await page.getByLabel('Name').fill(objectName.replace(/ /g, ''));

	await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(filePath);

	const responsePromise = page.waitForResponse(
		(response: Response) =>
			response
				.url()
				.includes('/o/object-admin/v1.0/object-definitions') &&
			response.request().method() === 'GET' &&
			response.status() === 200
	);

	await page.getByRole('button', {exact: true, name: 'Import'}).click();

	await page
		.locator('.modal-body')
		.waitFor({state: 'hidden', timeout: 10000});

	await expect(
		page.locator('.alert-danger', {
			hasText: 'The object definition failed to import.',
		})
	).not.toBeVisible();

	const response = await responsePromise;

	const {items} = await response.json();

	const internalName = objectName.replace(/ /g, '');

	const imported = items.find(
		(item: {id: number; name: string}) => item.name === internalName
	);

	return imported?.id;
}

test.describe('Manage export/import object definitions through UI', () => {
	test('Can cancel importing an object definition', async ({
		page,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

		await page.getByLabel('Name').fill('CancelledImport' + getRandomInt());

		await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(
			getFilePath('ImportedSimpleObject.json')
		);

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(page.locator('.modal-body')).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		const importedObjectName = 'Imported Simple Object';

		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);

		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {
				exact: true,
				name: importedObjectName,
			})
		).toBeHidden;
	});

	test('Can clear the JSON file on the import dialog', async ({
		page,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

		await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(
			getFilePath('ImportedSimpleObject.json')
		);

		await page.getByRole('button', {name: 'Clear'}).click();

		await expect(
			page.getByText('ImportedSimpleObject.json', {
				exact: true,
			})
		).not.toBeVisible();
	});

	test('can export data structure from a custom object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		const downloadPromise = page.waitForEvent('download');

		const row = page.getByRole('row', {
			name: objectDefinition.label['en_US'],
		});

		await row.getByRole('button', {name: 'Actions'}).click();

		await page
			.getByRole('menuitem', {name: 'Export Object Definition'})
			.click();

		const download = await downloadPromise;

		expect(download.suggestedFilename()).toContain(
			objectDefinition.externalReferenceCode
		);
	});

	test('Can export and import an object definition with Actions', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const actionLabel = 'ImportedAction' + getRandomInt();

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				label: {
					en_US: actionLabel,
				},
				name: 'actionName' + getRandomInt(),
				objectActionExecutorKey: 'webhook',
				objectActionTriggerKey: 'onAfterAdd',
				parameters: {
					secret: '',
					url: 'http://localhost:8080',
				},
			}
		);

		const {filePath, jsonContent} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			objectDefinition.label['en_US']
		);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectActions).toBeTruthy();
		expect(jsonContent.objectActions.length).toBeGreaterThanOrEqual(1);

		await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

		const importedObjectName = 'ImportedWithAction' + getRandomInt();

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			filePath,
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.label['en_US']
		);

		await page.getByRole('link', {name: 'Actions'}).click();

		await expect(
			page.getByRole('cell').getByText(actionLabel, {exact: true})
		).toBeVisible();

		await expect(page.getByText('Yes')).toBeVisible();
	});

	test('Can export an object definition with Aggregation field', async ({
		apiHelpers,
		objectFieldsPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const relationshipName =
			'relationship' + Math.floor(Math.random() * 99);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode,
			{
				label: {
					en_US: 'Relationship',
				},
				name: relationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId1: objectDefinition.id,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.postObjectDefinitionPublish(
			objectDefinition.id
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.addObjectField({
			aggregationField: 'ID',
			aggregationFieldFunction: 'Max',
			aggregationFieldRelationship: 'Relationship',
			objectFieldBusinessType: 'Aggregation',
			objectFieldLabel: 'Custom Aggregation',
		});

		const {jsonContent} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			objectDefinition.label['en_US']
		);

		expect(jsonContent).toBeTruthy();

		const aggregationField = jsonContent.objectFields?.find(
			(field) => field.businessType === 'Aggregation'
		);

		expect(aggregationField).toBeTruthy();
	});

	test('Can import and export Custom Views structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Custom Views';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedObjectWithCustomViews.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			importedObjectName
		);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectViews).toBeTruthy();
		expect(jsonContent.objectViews.length).toBeGreaterThanOrEqual(1);
	});

	test('Can import and export State Manager structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With State';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedObjectWithState.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			importedObjectName
		);

		expect(jsonContent).toBeTruthy();

		const stateManagerField = jsonContent.objectFields?.find(
			(field) => field.state === true
		);

		expect(stateManagerField).toBeTruthy();
	});

	test('Can import and export Validation structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Validation';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedObjectWithValidation.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			importedObjectName
		);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectValidationRules).toBeTruthy();
		expect(jsonContent.objectValidationRules.length).toBeGreaterThanOrEqual(
			1
		);
	});

	test('Can import and export metadata fields', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {filePath} = await exportObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			objectDefinition.label['en_US']
		);

		await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

		const importedObjectName = 'ImportedWithMetadata' + getRandomInt();

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			filePath,
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.label['en_US']
		);

		await page.getByRole('link', {name: 'Fields'}).click();

		for (const metadataField of [
			'Author',
			'Create Date',
			'External Reference Code',
			'ID',
			'Modified Date',
			'Status',
		]) {
			await expect(
				page.getByRole('cell').getByText(metadataField, {exact: true})
			).toBeVisible();
		}
	});

	test('Can import and maintain Layouts after importing an Object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Layout';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedObjectWithLayout.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			importedObjectName
		);

		await page.getByRole('link', {name: 'Layouts'}).click();

		await expect(
			page.getByRole('link', {exact: true, name: 'Layout'})
		).toBeVisible();
	});

	test('Can import and maintain Scope after importing an Object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Scope';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedObjectWithScope.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			importedObjectName
		);

		await page.getByRole('link', {name: 'Details'}).click();

		await expect(page.getByText('Company', {exact: true})).toBeVisible();
	});

	test('Can import the same object more than once', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectIdA = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedSimpleObject.json'),
			'ImportedSimpleObjectA'
		);

		if (importedObjectIdA) {
			apiHelpers.data.push({
				id: importedObjectIdA,
				type: 'objectDefinition',
			});
		}

		const importedObjectIdB = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedSimpleObject.json'),
			'ImportedSimpleObjectB'
		);

		if (importedObjectIdB) {
			apiHelpers.data.push({
				id: importedObjectIdB,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.searchInput.fill(
			'Imported Simple Object'
		);

		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {
				exact: true,
				name: 'Imported Simple Object',
			})
		).toHaveCount(2);
	});

	test('Verify that an imported custom object is created with Draft status', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Simple Object';

		const importedObjectId = await importObjectDefinition(
			page,
			viewObjectDefinitionsPage,
			getFilePath('ImportedSimpleObject.json'),
			importedObjectName
		);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const row = page.getByRole('row', {
			name: importedObjectName,
		});

		await expect(row.getByText('Draft')).toBeVisible();
	});
});

test.describe('Manage export/import object definitions with object entries', () => {
	test(
		'Can create an object entry of an object definition with 100 fields of all types after export and import',
		{
			annotation: {
				description:
					'this test requires an encryption algorithm and key to your portal properties',
				type: 'info',
			},
		},
		async ({
			apiHelpers,
			page,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
		}) => {
			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});

			for (const statusName of ['open', 'review', 'closed']) {
				await apiHelpers.listTypeAdmin.postListTypeEntry({
					key: statusName,
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name_i18n: {en_US: statusName},
				});
			}

			const objectFields: ObjectFieldEntry[] = [
				{
					assertValue: 'Test Test',
					businessType: 'Assignee',
					fillValue: 'Test Test',
				},
				{
					assertValue: '1',
					businessType: 'AutoIncrement',
				},
				{
					assertValue: 'true',
					businessType: 'Boolean',
					fillValue: 'true',
				},
				{
					assertValue: '01/01/2024',
					businessType: 'Date',
					fillValue: '01/01/2024',
				},
				{
					assertValue: '10/10/2024 08:00 AM',
					businessType: 'DateTime',
					fillValue: '10/10/2024 08:00 AM',
				},
				{
					assertValue: '13.579',
					businessType: 'Decimal',
					fillValue: '13.579',
				},
				{
					assertValue: 'Encrypted text',
					businessType: 'Encrypted',
					fillValue: 'Encrypted text',
				},
				{
					assertValue: '24680',
					businessType: 'Integer',
					fillValue: '24680',
				},
				{
					assertValue: '1234567890',
					businessType: 'LongInteger',
					fillValue: '1234567890',
				},
				{
					assertValue: 'Long text test',
					businessType: 'LongText',
					fillValue: 'Long text test',
				},
				{
					assertValue: ['review', 'closed'],
					businessType: 'MultiselectPicklist',
					fillValue: ['review', 'closed'],
				},
				{
					assertValue: 'review',
					businessType: 'Picklist',
					fillValue: 'review',
				},
				{
					assertValue: '1.23',
					businessType: 'PrecisionDecimal',
					fillValue: '1.23',
				},
				{
					assertValue: 'Rich text test',
					businessType: 'RichText',
					fillValue: 'Rich text test',
				},
				{
					assertValue: 'Simple text',
					businessType: 'Text',
					fillValue: 'Simple text',
				},
			];

			const generatedFields = generateObjectFields({
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				objectFieldBusinessTypes: objectFields.map(
					(entry) => entry.businessType
				),
			});

			for (let i = 0; i < objectFields.length; i++) {
				objectFields[i].field = generatedFields[i];
			}

			const allObjectFields = [
				...generatedFields,
				...generateObjectFields({
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					objectFieldBusinessTypes: [
						...Array(7).fill('AutoIncrement'),
						...Array(7).fill('Boolean'),
						...Array(7).fill('Date'),
						...Array(7).fill('DateTime'),
						...Array(7).fill('Decimal'),
						...Array(7).fill('Encrypted'),
						...Array(7).fill('Integer'),
						...Array(7).fill('LongInteger'),
						...Array(7).fill('LongText'),
						...Array(7).fill('PrecisionDecimal'),
						...Array(7).fill('RichText'),
						...Array(8).fill('Text'),
					] as any[],
				}),
			];

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: allObjectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const {filePath} = await exportObjectDefinition(
				page,
				viewObjectDefinitionsPage,
				objectDefinition.label['en_US']
			);

			await deleteObjectDefinitionFromData(
				apiHelpers,
				objectDefinition.id
			);

			const importedObjectName = 'ImportedObject' + getRandomInt();

			const importedObjectId = await importObjectDefinition(
				page,
				viewObjectDefinitionsPage,
				filePath,
				importedObjectName
			);

			if (importedObjectId) {
				apiHelpers.data.push({
					id: importedObjectId,
					type: 'objectDefinition',
				});
			}

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.searchInput.fill(
				objectDefinition.label['en_US']
			);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {name: objectDefinition.label['en_US']})
			).toBeVisible();

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const importedObjectDefinition =
				await objectDefinitionAPIClient.getObjectDefinition(
					importedObjectId
				);

			await viewObjectEntriesPage.goto(
				importedObjectDefinition.body.className
			);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			for (const entry of objectFields) {
				await fillObjectField(page, viewObjectEntriesPage, entry);
			}

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			for (const entry of objectFields) {
				await assertObjectField(page, entry);
			}
		}
	);
});
