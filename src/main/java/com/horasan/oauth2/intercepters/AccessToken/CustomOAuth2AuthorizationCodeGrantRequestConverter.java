package com.horasan.oauth2.intercepters.AccessToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.util.MultiValueMap;

import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

@Configurable
public class CustomOAuth2AuthorizationCodeGrantRequestConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

	@Autowired
	private AppUtil appUtil;
	
	private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;
	
	public CustomOAuth2AuthorizationCodeGrantRequestConverter() {
		  
	      defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
	      appUtil = new AppUtil();
	  }
	  
	  @Override
	  public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
		  
		  OAuth2AuthorizationResponse authorizationresponseFromGoogle = req.getAuthorizationExchange().getAuthorizationResponse();
		  
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Resource Owner is successfully entered Google username/password. and gave consent.", null, this.getClass().getSimpleName());
		  appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.DOUBLE_TAB, "Access Token request to Google is sent just after getting Authorization Code. Hence we can see Authorization Code response here.", null, this.getClass().getSimpleName());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Authorization Code response is received by Client.");
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Authorization Code response (from Google) to Client (Spring Boot Application) is sent to " + authorizationresponseFromGoogle.getRedirectUri() + " aka RedirectUri");
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Authorization Code response to Client includes 'Authentication Code' " + authorizationresponseFromGoogle.getCode());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Authorization Code response to Client includes 'state' parameter. " + authorizationresponseFromGoogle.getState());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "'Send an authentication request to Google' is completed.");
		  
		  
		  appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[6] Confirm anti-forgery state token", "https://developers.google.com/identity/protocols/oauth2/openid-connect#confirmxsrftoken", this.getClass().getSimpleName());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Confirm anti-forgery state token confirmation is done in OAuth2LoginAuthenticationProvider.authenticate method.");
		  
		  RequestEntity<?> requestEntity = defaultConverter.convert(req);
		  appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[7] Exchange code for access token and ID token", "https://developers.google.com/identity/protocols/oauth2/openid-connect#exchangecode", this.getClass().getSimpleName());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.DOUBLE_TAB, "Access token request (from Client to Google) message includes following parameters");
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "Request uri: " + requestEntity.getUrl());
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "Headers: " + requestEntity.getHeaders());
		  appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.TRIPLE_TAB, "Client Secret is used when creating Authorization:\"Basic ....\" header information.");
		  MultiValueMap<String, String> requestEntityBody =  (MultiValueMap<String, String>) requestEntity.getBody();
		  
		  appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.TRIPLE_TAB, "Code: " + requestEntityBody.get("code") + "(Authorization Code)");
		  
	      return requestEntity;
	      
	  }

	}
