/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AssigneeRenderer from './AssigneeRenderer';

const WorkflowAssigneeCell = ({embedded}: {embedded: any}) => {
	if (embedded?.assigneePerson) {
		return (
			<AssigneeRenderer
				image={embedded.assigneePerson.image}
				name={embedded.assigneePerson.name}
			/>
		);
	}

	return (
		<>
			{embedded?.assigneeRoles
				?.map(({name}: {name: string}) => name)
				.join(', ')}
		</>
	);
};

export default WorkflowAssigneeCell;
