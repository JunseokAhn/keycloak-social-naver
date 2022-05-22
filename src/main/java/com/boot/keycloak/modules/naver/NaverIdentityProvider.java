/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boot.keycloak.modules.naver;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

public class NaverIdentityProvider extends AbstractOAuth2IdentityProvider implements SocialIdentityProvider {

	public static final String AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
	public static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	public static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
	public static final String DEFAULT_SCOPE = "basic";

	public NaverIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
		super(session, config);
		config.setAuthorizationUrl(AUTH_URL);
		config.setTokenUrl(TOKEN_URL);
		config.setUserInfoUrl(PROFILE_URL);
	}

	@Override
	protected boolean supportsExternalExchange() {
		return true;
	}

	@Override
	protected String getProfileEndpointForValidation(EventBuilder event) {
		return PROFILE_URL;
	}

	@Override
	protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {

		//키클락에서 Naver로그인 진행 > response 결과
		JsonNode response = profile.get("response");
		/*{
			"resultcode":"00",
			"message":"success",
			"response":
				{
					"id":"osSjKfNDbgj6FyR7srJOOELTLV_lO-eba1XKQvlXsfg",
					"email":"yourAddress@email.com",
					"mobile":"010-0000-0000",
					"mobile_e164":"+820000000000",
					"name":"yourName",
					"birthyear":"yourBirthYear"
				}
		}*/

		//	가져온 결과 파싱 후 리턴
		BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(response, "id"));
		user.setUsername(getJsonProperty(response, "name"));
		user.setEmail(getJsonProperty(response, "email"));

		user.setIdpConfig(getConfig());
		user.setIdp(this);
		AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

		return user;

	}


	@Override
	protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
		try {
			JsonNode profile = SimpleHttp.doGet(PROFILE_URL, session).param("access_token", accessToken).asJson();
			System.out.println("PROFILE : " + profile);
			BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);

			return user;
		} catch (Exception e) {
			System.out.println("=====================EXCEPTION==============================");
			System.out.println("ACCESS TOKEN : " + accessToken);
			System.out.println("PROFILE_URL : " + PROFILE_URL);
			System.out.println("SESSION : " + session);
			System.out.println("=====================EXCEPTION==============================");
			throw new IdentityBrokerException("Could not obtain user profile from naver.", e);
		}
	}

	@Override
	protected String getDefaultScopes() {
		return DEFAULT_SCOPE;
	}
}
