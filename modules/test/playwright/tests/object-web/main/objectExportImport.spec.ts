/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationshipAPI,
	ObjectValidationRuleAPI,
	ObjectViewAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import {readFile, writeFile} from 'fs/promises';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {getTempDir} from '../../../utils/temp';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from './utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
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

	if (entry.businessType === 'Text') {
		const val = await page.getByLabel(entry.field.label['en_Us']).textContent();
	}

	const locator = page.locator(`[data-field-name="${entry.field.name}"]`);

	if (entry.businessType === 'Assignee') {
		await expect(
			page.getByRole('combobox', {name: entry.field.label['en_US']})
		).toHaveValue(entry.assertValue as string);
	}
	else if (entry.businessType === 'Boolean') {
		await expect(locator.locator('.custom-control-input')).toBeChecked;

		return;
	}
	else if (entry.businessType === 'MultiselectPicklist') {
		await expect(
			await locator.locator('.label-item-expand').allTextContents()
		).toEqual(entry.assertValue as string[]);

		return;
	}
	else if (entry.businessType === 'Picklist') {
		await expect(locator.locator('.form-control')).toHaveText(
			entry.assertValue
		);

		return;
	}
	else if (entry.businessType === 'RichText') {
		await expect(
			page
				.getByRole('application', {
					name: entry.field.label['en_US'],
				})
				.frameLocator('iframe')
				.getByRole('textbox')
		).toHaveText(entry.assertValue);

		return;
	}

	await expect(locator.locator('.form-control')).toHaveValue(
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

	await viewObjectDefinitionsPage.searchInput.fill(
		objectDefinitionLabel.replace(/ /g, '')
	);

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
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
	filePath: string,
	objectName: string
): Promise<number> {
	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.objectFolderActions.click();

	await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

	await page.getByLabel('Name').fill(objectName);

	await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(filePath);

	await expect(
		viewObjectDefinitionsPage.externalReferenceCodeInput
	).not.toBeEmpty({timeout: 10000});

	await page.getByRole('button', {name: 'Import'}).click();

	await page
		.locator('.modal-body')
		.waitFor({state: 'hidden', timeout: 10000});

	await expect(
		page.locator('.alert-danger', {
			hasText: 'The object definition failed to import.',
		})
	).not.toBeVisible();

	const {externalReferenceCode} = JSON.parse(
		await readFile(filePath, 'utf-8')
	);

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body} =
		await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			externalReferenceCode
		);

	return body.id;
}

// Migrated

test(
	'field values persist for object entries with 100 fields of all types after export and import',
	{
		annotation: {
			type: 'info',
			description:
				'this test requires an encryption algorithm and key to your portal properties',
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
				businessType: 'Assignee',
				assertValue: 'Test Test',
				fillValue: 'Test Test',
			},
			{
				businessType: 'AutoIncrement',
				assertValue: '1',
			},
			{
				businessType: 'Boolean',
				assertValue: 'Yes',
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

		await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

		const importedObjectName = 'ImportedObject' + getRandomInt();

		const importedObjectId = await importObjectDefinition(
			apiHelpers,
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

// Migrated

test('can cancel importing an object', async ({
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

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.objectFolderActions.click();

	await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

	await page.getByLabel('Name').fill('CancelledImport' + getRandomInt());

	await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(filePath);

	await expect(
		viewObjectDefinitionsPage.externalReferenceCodeInput
	).not.toBeEmpty({timeout: 10000});

	await page.getByRole('button', {name: 'Cancel'}).click();

	await expect(page.locator('.modal-body')).toBeHidden();

	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.searchInput.fill(
		objectDefinition.label['en_US']
	);

	await page.keyboard.press('Enter');

	await expect(
		page.getByRole('link', {
			exact: true,
			name: objectDefinition.label['en_US'],
		})
	).toHaveCount(1);
});

// Migrated

test('can clear the JSON file on the import dialog', async ({
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

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.objectFolderActions.click();

	await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

	await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(filePath);

	await expect(
		viewObjectDefinitionsPage.externalReferenceCodeInput
	).not.toBeEmpty({timeout: 10000});

	await page.getByRole('button', {name: 'Clear'}).click();

	await expect(
		viewObjectDefinitionsPage.externalReferenceCodeInput
	).not.toBeVisible();
});

// Migrated

test('can export and import an object with Actions', async ({
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
		apiHelpers,
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






// TODO migration of the tests below

test('Can export an object with Aggregation field', async ({
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

	const relationshipName = 'relationship' + Math.floor(Math.random() * 99);

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

	const objectViewAPIClient = await apiHelpers.buildRestClient(ObjectViewAPI);

	await objectViewAPIClient.postObjectDefinitionObjectView(
		objectDefinition.id,
		{
			defaultObjectView: true,
			name: {en_US: 'Custom View'},
			objectViewColumns: [
				{
					objectFieldName: objectFields[0].name,
					priority: 0,
				},
			],
			objectViewSortColumns: [
				{
					objectFieldName: objectFields[0].name,
					priority: 0,
					sortOrder: 'asc',
				},
			],
		}
	);

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const importedObjectName = 'ImportedCustomViews' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	const {jsonContent: reExportedJson} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	expect(reExportedJson).toBeTruthy();
	expect(reExportedJson.objectViews).toBeTruthy();
	expect(reExportedJson.objectViews.length).toBeGreaterThanOrEqual(1);
});

test('Can import and export State Manager structure', async ({
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
}) => {
	test.setTimeout(300000);
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeDefinition',
	});

	const picklistItemName = 'PicklistItem' + getRandomInt();

	await apiHelpers.listTypeAdmin.postListTypeEntry({
		key: picklistItemName,
		listTypeDefinitionExternalReferenceCode:
			listTypeDefinition.externalReferenceCode,
		name_i18n: {en_US: picklistItemName},
	});

	const objectFields = generateObjectFields({
		listTypeDefinitionExternalReferenceCode:
			listTypeDefinition.externalReferenceCode,
		objectFieldBusinessTypes: [
			{
				businessType: 'Picklist',
				objectFieldSettings: [
					{
						name: 'defaultValueType',
						value: 'inputAsValue' as any,
					},
					{
						name: 'defaultValue',
						value: picklistItemName.toLowerCase(),
					},
				],
				required: true,
				state: true,
			},
		],
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

	const importedObjectName = 'ImportedState' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	const {jsonContent: reExportedJson} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	expect(reExportedJson).toBeTruthy();

	const stateField = reExportedJson.objectFields?.find(
		(field) => field.state === true
	);

	expect(stateField).toBeTruthy();
});

test('Can import and export Validation structure', async ({
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

	const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
		ObjectValidationRuleAPI
	);

	await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
		objectDefinition.externalReferenceCode,
		{
			active: true,
			engine: 'ddm',
			engineLabel: 'Expression Builder',
			errorLabel: {
				en_US: 'Validation error message',
			},
			name: {
				en_US: 'Validation Rule ' + getRandomInt(),
			},
			objectValidationRuleSettings: [] as any,
			outputType: 'fullValidation',
			script: 'isEmailAddress(textField)',
			system: false,
		}
	);

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const importedObjectName = 'ImportedValidation' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	const {jsonContent: reExportedJson} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	expect(reExportedJson).toBeTruthy();
	expect(reExportedJson.objectValidationRules).toBeTruthy();
	expect(reExportedJson.objectValidationRules.length).toBeGreaterThanOrEqual(
		1
	);
});

test('Can import data structure to custom objects', async ({
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

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const importedObjectName = 'ImportedSimple' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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
});

test('Can import and maintain Fields after importing an Object', async ({
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const customFieldLabel = objectFields[0].label['en_US'];

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

	const importedObjectName = 'ImportedWithField' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	await page
		.locator('.nav-item .nav-link')
		.filter({hasText: 'Fields'})
		.click();

	await expect(
		page.getByRole('cell').getByText(customFieldLabel, {exact: true})
	).toBeVisible();

	await expect(
		page.getByRole('cell').getByText('Text', {exact: true}).first()
	).toBeVisible();
});

test('Can import and maintain Layouts after importing an Object', async ({
	apiHelpers,
	objectLayoutsPage,
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

	const layoutName = 'Layout' + getRandomInt();

	await objectLayoutsPage.goto(objectDefinition.label['en_US']);

	await objectLayoutsPage.createObjectLayout(layoutName);

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const importedObjectName = 'ImportedWithLayout' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	await page.getByRole('link', {name: 'Layouts'}).click();

	await expect(page.getByRole('link', {name: layoutName})).toBeVisible();
});

test('Can import the same object more than once', async ({
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

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const jsonContent = JSON.parse(await readFile(filePath, 'utf-8'));

	const importedObjectNameA = 'ImportedObjectA' + getRandomInt();
	const importedObjectNameB = 'ImportedObjectB' + getRandomInt();

	const filePathA = path.join(getTempDir(), importedObjectNameA + '.json');

	const filePathB = path.join(getTempDir(), importedObjectNameB + '.json');

	const makeMinimalJson = (
		objectName: string,
		label: Record<string, string>
	) => ({
		externalReferenceCode: objectName,
		label,
		name: objectName,
		objectFields: [],
		objectFolderExternalReferenceCode:
			jsonContent.objectFolderExternalReferenceCode ?? 'default',
		panelCategoryKey: jsonContent.panelCategoryKey ?? '',
		pluralLabel: {en_US: objectName + 's'},
		scope: jsonContent.scope ?? 'company',
		status: {code: 2},
		titleObjectFieldName: jsonContent.titleObjectFieldName ?? 'id',
	});

	await writeFile(
		filePathA,
		JSON.stringify(
			makeMinimalJson(
				importedObjectNameA,
				jsonContent.label as Record<string, string>
			)
		)
	);

	await writeFile(
		filePathB,
		JSON.stringify(
			makeMinimalJson(
				importedObjectNameB,
				jsonContent.label as Record<string, string>
			)
		)
	);

	const importedObjectIdA = await importObjectDefinition(
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
		filePathA,
		importedObjectNameA
	);

	if (importedObjectIdA) {
		apiHelpers.data.push({
			id: importedObjectIdA,
			type: 'objectDefinition',
		});
	}

	const importedObjectIdB = await importObjectDefinition(
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
		filePathB,
		importedObjectNameB
	);

	if (importedObjectIdB) {
		apiHelpers.data.push({
			id: importedObjectIdB,
			type: 'objectDefinition',
		});
	}

	await viewObjectDefinitionsPage.goto();

	await viewObjectDefinitionsPage.searchInput.fill(
		objectDefinition.label['en_US']
	);

	await page.keyboard.press('Enter');

	const objectDefinitionLinks = page.getByRole('link', {
		exact: true,
		name: objectDefinition.label['en_US'],
	});

	await expect(objectDefinitionLinks).toHaveCount(2);
});

test('Can import and maintain Scope after importing an Object', async ({
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			scope: 'company',
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

	const importedObjectName = 'ImportedWithScope' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
		objectDefinition.label['en_US']
	);

	await page.getByRole('link', {name: 'Details'}).click();

	await expect(page.getByText('Company', {exact: true})).toBeVisible();
});

test('Imported custom object is created with Draft status', async ({
	apiHelpers,
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

	const {filePath} = await exportObjectDefinition(
		page,
		viewObjectDefinitionsPage,
		objectDefinition.label['en_US']
	);

	await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

	const importedObjectName = 'ImportedDraft' + getRandomInt();

	const importedObjectId = await importObjectDefinition(
		apiHelpers,
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

	const row = page.getByRole('row', {
		name: objectDefinition.label['en_US'],
	});

	await expect(row.getByText('Draft')).toBeVisible();
});
