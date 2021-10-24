package com.horasan.oauth2.intercepters;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

@Component("customOauth2authSuccessHandler")
public class Oauth2AuthenticationSuccessHandlerAfterSuccessfulAuthentication implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private AppUtil appUtil;
	
	@Autowired
    private InMemoryOAuth2AuthorizedClientService inMemoryOAuth2AuthorizedClientService;


	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Resource Owner gives consent. Authentication is successful.", null, this.getClass().getSimpleName());
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "A principal object is created using id_token parameters.");
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "'OAuth2AuthorizedClient' object is saved to 'InMemoryOAuth2AuthorizedClientService'.");
		
		Authentication authToken = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> attributes;
		// We are dealing with Oauth2 so authentication is type OAuth2AuthenticationToken!
	    attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();

	    appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "'OAuth2AuthorizedClient' object can be retrieved from 'InMemoryOAuth2AuthorizedClientService'.", null, this.getClass().getSimpleName());
	    
	    OAuth2AuthorizedClient authorizedClient = inMemoryOAuth2AuthorizedClientService.loadAuthorizedClient("google", ((OAuth2AuthenticationToken) authToken).getPrincipal().getName());

	    appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "'access_token' is retrieved from authorized client. Access token is " + authorizedClient.getAccessToken().getTokenValue());
	    
	    String email = attributes.get(AppUtil.OAUTH2_AUTHENTICATION_TOKEN_ATTRIBUTE_EMAIL).toString();
	    appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Resource Owner's email is :" + email);
	    
		String targetURL = UriComponentsBuilder.fromUriString(appUtil.getClientAgentFrontEndHome())
		.queryParam("status", "loggedin")
        .queryParam("email", email)
        .build().toUriString();

		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Client (Spring Boot Application) is sending email info to user agent ");
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "AuthenticationSuccess is redirected to " + appUtil.getClientAgentFrontEndHome() + " by Client.");
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "[5] Resource Owner can see the email address (" + appUtil.getClientAgentFrontEndHome()+ ").");
		
		String jsessionId = appUtil.getJSESSIONIDValueFrom(request);
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "[6] 'JSESSIONID' is sent as Cookie to the User Agent. 'JSESSIONID' is " + jsessionId, "https://spring.io/guides/tutorials/spring-boot-oauth2/ go to 'What Just Happened?' section", null);
		
		this.redirectStrategy.sendRedirect(request, response, targetURL);
		
		
	}

}
