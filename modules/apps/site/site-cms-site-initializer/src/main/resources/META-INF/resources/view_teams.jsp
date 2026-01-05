<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewTeamsSectionDisplayContext viewTeamsSectionDisplayContext = (ViewTeamsSectionDisplayContext)request.getAttribute(ViewTeamsSectionDisplayContext.class.getName());
%>

<div>
	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= viewTeamsSectionDisplayContext.getAPIURL() %>"
			emptyState="<%= viewTeamsSectionDisplayContext.getEmptyState() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.TEAMS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
		/>
	</div>
</div>