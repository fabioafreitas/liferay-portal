<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewTaskContentsSummarySectionDisplayContext viewTaskContentsSummarySectionDisplayContext = (ViewTaskContentsSummarySectionDisplayContext)request.getAttribute(ViewTaskContentsSummarySectionDisplayContext.class.getName());
%>

<div>
	<div>
		<frontend-data-set:headless-display
			apiURL="<%= viewTaskContentsSummarySectionDisplayContext.getAPIURL() %>"
			fdsActionDropdownItems="<%= viewTaskContentsSummarySectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMPSiteInitializerFDSNames.CMP_TASK_CONTENTS_SUMMARY_SECTION %>"
			itemsPerPage="<%= 20 %>"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>