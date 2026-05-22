/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.ai.hub.rest.dto.v1_0.ProvisioningRequest;
import com.liferay.ai.hub.rest.dto.v1_0.UserAccount;
import com.liferay.ai.hub.rest.manager.v1_0.ProvisioningRequestManager;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Davyson Melo
 */
@Component(service = ProvisioningRequestManager.class)
public class ProvisioningRequestManagerImpl
	implements ProvisioningRequestManager {

	@Override
	public ProvisioningRequest postProvisioning(
			Company company, DTOConverterContext dtoConverterContext,
			ProvisioningRequest provisioningRequest)
		throws Exception {

		long companyId = company.getCompanyId();
		Locale locale = dtoConverterContext.getLocale();

		AccountEntry aiHubAccountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				"L_AI_HUB", companyId);

		ServiceContext serviceContext = ServiceContextBuilder.create(
			company.getGroupId(), dtoConverterContext.getHttpServletRequest(),
			null
		).build();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(dtoConverterContext.getUserId());

		AccountEntry customerAccountEntry = _getOrAddAccountEntry(
			provisioningRequest.getAccountName(), serviceContext);
		User serviceAccountUser = _getOrAddUser(
			company, locale,
			FriendlyURLNormalizerUtil.normalize(
				provisioningRequest.getAccountName() + "-service-account"),
			serviceContext);

		long[] accountEntryIds = {
			aiHubAccountEntry.getAccountEntryId(),
			customerAccountEntry.getAccountEntryId()
		};

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			accountEntryIds, new long[0], serviceAccountUser.getUserId());

		User guestServiceAccountUser = _getOrAddUser(
			company, locale,
			FriendlyURLNormalizerUtil.normalize(
				provisioningRequest.getAccountName() +
					"-guest-service-account"),
			serviceContext);

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			accountEntryIds, new long[0], guestServiceAccountUser.getUserId());

		if (ArrayUtil.isNotEmpty(provisioningRequest.getUserAccounts())) {
			Role role = _roleLocalService.fetchRole(
				companyId, "AI Hub Agent Manager");

			AccountRole agentManagerAccountRole =
				_accountRoleLocalService.getAccountRoleByRoleId(
					role.getRoleId());

			for (UserAccount userAccount :
					provisioningRequest.getUserAccounts()) {

				User user = _getOrAddRegularUser(
					locale, userAccount, serviceContext);

				_accountEntryUserRelLocalService.updateAccountEntryUserRels(
					accountEntryIds, new long[0], user.getUserId());

				_accountRoleLocalService.associateUser(
					customerAccountEntry.getAccountEntryId(),
					agentManagerAccountRole.getAccountRoleId(),
					user.getUserId());
			}
		}

		_addOAuth2Application(
			provisioningRequest, serviceAccountUser, serviceContext);

		_addQuotas(customerAccountEntry, serviceContext);

		return new ProvisioningRequest() {
			{
				setAccountExternalReferenceCode(
					customerAccountEntry::getExternalReferenceCode);
				setAccountId(customerAccountEntry::getAccountEntryId);
				setAccountName(customerAccountEntry::getName);
				setLiferayDXPURL(provisioningRequest::getLiferayDXPURL);
				setUserAccounts(provisioningRequest::getUserAccounts);
			}
		};
	}

	private void _addOAuth2Application(
			ProvisioningRequest provisioningRequest, User clientCredentialUser,
			ServiceContext serviceContext)
		throws Exception {

		String portalURL = provisioningRequest.getLiferayDXPURL();

		_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
			null, clientCredentialUser.getUserId(),
			clientCredentialUser.getFullName(),
			Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
			"client_secret_post", clientCredentialUser.getUserId(),
			OAuth2SecureRandomGenerator.generateClientId(),
			ClientProfile.HEADLESS_SERVER.id(),
			OAuth2SecureRandomGenerator.generateClientSecret(), null,
			Collections.emptyList(), portalURL, 0, null,
			provisioningRequest.getAccountName(), null,
			Collections.singletonList(portalURL), false,
			Collections.emptyList(), false, serviceContext);
	}

	private void _addQuotaObjectEntry(
			AccountEntry accountEntry, String externalReferenceCode,
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());

		if (objectEntry != null) {
			return;
		}

		_objectEntryLocalService.addObjectEntry(
			0, serviceContext.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"limit", _QUOTA_TOKEN_LIMIT
			).put(
				"r_accountToAIHubQuotas_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"usage", 0
			).build(),
			serviceContext);
	}

	private void _addQuotas(
			AccountEntry accountEntry, ServiceContext serviceContext)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", serviceContext.getCompanyId());

		_addQuotaObjectEntry(
			accountEntry, "guest-quota-" + accountEntry.getAccountEntryId(),
			objectDefinition, serviceContext);
		_addQuotaObjectEntry(
			accountEntry, "quota-" + accountEntry.getAccountEntryId(),
			objectDefinition, serviceContext);
	}

	private AccountEntry _getOrAddAccountEntry(
			String name, ServiceContext serviceContext)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				name, serviceContext.getCompanyId());

		if (accountEntry != null) {
			return accountEntry;
		}

		return _accountEntryService.addAccountEntry(
			null, serviceContext.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, name, null, null,
			null, null, null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private User _getOrAddRegularUser(
			Locale locale, UserAccount userAccount,
			ServiceContext serviceContext)
		throws Exception {

		User user = _userLocalService.fetchUserByEmailAddress(
			serviceContext.getCompanyId(), userAccount.getEmailAddress());

		if (user != null) {
			return user;
		}

		return _userLocalService.addUser(
			UserConstants.USER_ID_DEFAULT, serviceContext.getCompanyId(), true,
			null, null, false, userAccount.getScreenName(),
			userAccount.getEmailAddress(), locale, userAccount.getFirstName(),
			StringPool.BLANK, userAccount.getLastName(), 0, 0, true,
			Calendar.JANUARY, 1, 1970, StringPool.BLANK,
			UserConstants.TYPE_REGULAR, null, null, null, null, false,
			serviceContext);
	}

	private User _getOrAddUser(
			Company company, Locale locale, String screenName,
			ServiceContext serviceContext)
		throws Exception {

		User user = _userLocalService.fetchUserByScreenName(
			company.getCompanyId(), screenName);

		if (user != null) {
			return user;
		}

		user = _userLocalService.addUser(
			UserConstants.USER_ID_DEFAULT, company.getCompanyId(), true, null,
			null, false, screenName,
			screenName + StringPool.AT + company.getMx(), locale, screenName,
			StringPool.BLANK, screenName, 0, 0, true, Calendar.JANUARY, 1, 1970,
			StringPool.BLANK, UserConstants.TYPE_SERVICE_ACCOUNT, null, null,
			null, null, false, serviceContext);

		user.setPasswordReset(false);
		user.setEmailAddressVerified(true);

		return _userLocalService.updateUser(user);
	}

	private static final int _QUOTA_TOKEN_LIMIT = 33333333;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}