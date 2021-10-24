package com.horasan;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

@SpringBootApplication
public class GoogleSocialLoginApplication extends SpringBootServletInitializer {

	@Autowired
	private AppUtil appUtil;

	public static void main(String[] args) {
		SpringApplication.run(GoogleSocialLoginApplication.class, args);
	}

	@PostConstruct
    public void listen() {
		
		appUtil.deleteLogFile();
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "We will implement authorization code grant to obtain an access token.", "(RFC 6749 -- https://datatracker.ietf.org/doc/html/rfc6749#section-4).", GoogleSocialLoginApplication.class.getSimpleName());
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Steps (logged as GOOGLE STEP X) which are defined in Google oauth2 openid-connect documentation will be our focus area.", "https://developers.google.com/identity/protocols/oauth2/openid-connect", null);
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Before starting, following 3 steps must be completed.");
		appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[1] Obtain OAuth 2.0 credentials.", "https://developers.google.com/identity/protocols/oauth2/openid-connect#getcredentials", null);
		appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[2] Set a redirect URI.", "https://developers.google.com/identity/protocols/oauth2/openid-connect#setredirecturi", null);
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.DOUBLE_TAB, "Default Redirect URI is {baseUrl}/login/oauth2/code/google", "https://spring.io/guides/tutorials/spring-boot-oauth2/ search for {baseUrl}/login/oauth2/code/{registrationId}", null);

		appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[3] Customize the user consent screen", "https://developers.google.com/identity/protocols/oauth2/openid-connect#consentpageexperience", null);
		appUtil.printToConsoleAndLog(LogType.NOTE, AppUtil.SINGLE_TAB, "Spring boot application started.");
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "[1] Resource Owner is on the browser (User-Agent) on " + appUtil.getClientAgentFrontEndHome());
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "[2] Resource Owner is not logged in!");
		
    }


}
