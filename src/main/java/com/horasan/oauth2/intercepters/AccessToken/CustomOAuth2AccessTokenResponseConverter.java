package com.horasan.oauth2.intercepters.AccessToken;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

@Configurable
public class CustomOAuth2AccessTokenResponseConverter implements Converter<Map<String, String>, OAuth2AccessTokenResponse> {
	
	@Autowired
	private AppUtil appUtil;
	
	
    private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
            OAuth2ParameterNames.ACCESS_TOKEN, 
            OAuth2ParameterNames.TOKEN_TYPE, 
            OAuth2ParameterNames.EXPIRES_IN, 
            OAuth2ParameterNames.REFRESH_TOKEN, 
            OAuth2ParameterNames.SCOPE) .collect(Collectors.toSet());

        @Override
        public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
        	
        	appUtil = new AppUtil();
        	
            String accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
            
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Access Token response is received with the following parameters.", null, this.getClass().getSimpleName());
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "accessToken: " + accessToken, "https://developers.google.com/identity/protocols/oauth2/openid-connect#java - A token that can be sent to a Google API.", null);
            
            OAuth2AccessToken.TokenType accessTokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue()
                .equalsIgnoreCase(tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE))) {
                accessTokenType = OAuth2AccessToken.TokenType.BEARER;
            }
            
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "accessTokenType: " + accessTokenType.getValue(), null, null);
                      
            long expiresIn = 0;
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
                try {
                    expiresIn = Long.valueOf(tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN));
                } catch (NumberFormatException ex) {
                }
            }

            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "expiresIn: " + expiresIn, null, null);
                       
            Set<String> scopes = Collections.emptySet();
            String scope = "";
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
                scope = tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
                scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " "))
                    .collect(Collectors.toSet());
            }

            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "scope: " + scope, null, null);
            
            String refreshToken = tokenResponseParameters.get(OAuth2ParameterNames.REFRESH_TOKEN);

            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "refreshToken: " + refreshToken, null, null);
            
            Map<String, Object> additionalParameters = new LinkedHashMap<>();
            tokenResponseParameters.entrySet()
                .stream()
                .filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

            
            String idToken = String.valueOf(additionalParameters.get("id_token"));
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "id_token: " + idToken, "https://developers.google.com/identity/protocols/oauth2/openid-connect#java - A JWT that contains identity information about the user that is digitally signed by Google.", null);
            
            Base64.Decoder decoder = Base64.getDecoder();
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "id_token includes " + new String(decoder.decode(idToken.split("\\.")[1])), "https://jwt.io/", null);
            appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "'Exchange code for access token and ID token' is completed.");
            
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(accessTokenType)
                .expiresIn(expiresIn)
                .scopes(scopes)
                .refreshToken(refreshToken)
                .additionalParameters(additionalParameters)
                .build();
            
    		return oAuth2AccessTokenResponse;
    		
        }

    }
