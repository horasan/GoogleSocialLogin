package com.horasan.oauth2.intercepters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

public class CustomAuthenticationFailureHandlerAfterUnsuccessfulAuthentication implements AuthenticationFailureHandler {
	
	private AppUtil appUtil;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String targetURL = UriComponentsBuilder.fromUriString(appUtil.getClientAgentFrontEndHome())
				.queryParam("status", "AuthenticationFailure")
		        .build().toUriString();
				
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "[3.1] (Cannot Get User Info): Resource Owner authentication is failed.", null, this.getClass().getSimpleName());
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "[3.2] (Cannot Get User Info): Resource Owner can see 'authentication is failed' message (" + appUtil.getClientAgentFrontEndHome() + ").", null, this.getClass().getSimpleName());
		
		this.redirectStrategy.sendRedirect(request, response, targetURL);
		
	}
	

	@Bean
	public AppUtil appUtil() {
		return new AppUtil();
	}

}
