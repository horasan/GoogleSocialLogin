package com.horasan.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horasan.util.AppUtil;
import com.horasan.util.LogType;

@RestController
public class HelloController {
	
	@Autowired
	private AppUtil appUtil;
	
	@Autowired
	private OAuth2AuthorizedClientRepository repo;
	
	@GetMapping("/hello")
	public ResponseEntity<GoogleOAuth2CustomerInfo> sayHello(HttpServletRequest request) {
		
		appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[8] Access to other Google APIs (actually no API call).", "https://developers.google.com/identity/protocols/oauth2/openid-connect#offlineaccess", "/hello controller.");
		String jSessionId = appUtil.getJSESSIONIDValueFrom(request);
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "'JSESSIONID' is sent as Cookie from the User Agent. 'JSESSIONID' is " + jSessionId, "https://spring.io/guides/tutorials/spring-boot-oauth2/ go to 'What Just Happened?' section", "/hello controller.");
		
		// Current authenticated Resource Owner.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		OAuth2AuthorizedClient authorizedClient = repo.loadAuthorizedClient("google", authentication, request);
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "'access_token' is retrieved from authorized client but not used. Access token is " + authorizedClient.getAccessToken().getTokenValue());
	    
	    DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
	    
	    GoogleOAuth2CustomerInfo customerInfo = new GoogleOAuth2CustomerInfo();
	    customerInfo.setGiven_name(defaultOAuth2User.getAttribute("given_name"));
	    customerInfo.setFamily_name(defaultOAuth2User.getAttribute("family_name"));
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "'Access to other Google APIs.' is completed.");
	    appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "Resource owner can see given_name:family_name" +  customerInfo.getGiven_name() + ":" + customerInfo.getFamily_name());
	    return new ResponseEntity<GoogleOAuth2CustomerInfo>(customerInfo, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getgoogleuserinfo")
	public ResponseEntity<GoogleOAuth2CustomerInfo> getGoogleUserInfoByAccessToken(HttpServletRequest request)
			throws URISyntaxException, ClientProtocolException, IOException {
		
		appUtil.printToConsoleAndLog(LogType.GOOGLE_STEP, AppUtil.SINGLE_TAB, "[8] Access to other Google APIs.", "https://developers.google.com/identity/protocols/oauth2/openid-connect#offlineaccess", "/getgoogleuserinfo controller.");
		String jSessionId = appUtil.getJSESSIONIDValueFrom(request);
		appUtil.printToConsoleAndLog(LogType.FRONTEND, AppUtil.SINGLE_TAB, "'JSESSIONID' is sent as Cookie from the User Agent. 'JSESSIONID' is " + jSessionId, "https://spring.io/guides/tutorials/spring-boot-oauth2/ go to 'What Just Happened?' section", "");
		// Current authenticated Resource Owner.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String accesstoken = repo.loadAuthorizedClient("google", authentication, request).getAccessToken()
				.getTokenValue();
		
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "Authenticated Resource Owner is retrieved by using "  + authentication.getName(), null, "/getgoogleuserinfo controller.");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Bearer " + accesstoken);

		CloseableHttpClient client = HttpClientBuilder.create().build();

		HttpGet httpGet = new HttpGet("https://www.googleapis.com/oauth2/v3/userinfo");

		URI uri = new URIBuilder(httpGet.getURI()).addParameter("access_token", accesstoken).build();
		((HttpRequestBase) httpGet).setURI(uri);
		CloseableHttpResponse response3 = client.execute(httpGet);

		
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "'access_token' is retrieved from authorized client and sent to Google API. Access token is " + accesstoken, null, "/getgoogleuserinfo controller.");
		
		ObjectMapper objectMapper = new ObjectMapper();
		GoogleOAuth2CustomerInfo customerInfo = objectMapper.readValue(response3.getEntity().getContent(),
				GoogleOAuth2CustomerInfo.class);
		client.close();
		appUtil.printToConsoleAndLog(LogType.OAUTH2, AppUtil.SINGLE_TAB, "'Access to other Google APIs.' is completed.");
		return new ResponseEntity<GoogleOAuth2CustomerInfo>(customerInfo, HttpStatus.OK);
	}
}
