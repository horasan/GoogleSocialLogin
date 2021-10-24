package com.horasan.oauth2.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.client.RestTemplate;

import com.horasan.oauth2.intercepters.CustomAuthenticationFailureHandlerAfterUnsuccessfulAuthentication;
import com.horasan.oauth2.intercepters.Oauth2AuthenticationSuccessHandlerAfterSuccessfulAuthentication;
import com.horasan.oauth2.intercepters.RestAuthEntryPointForUnauthenticatedCalls;
import com.horasan.oauth2.intercepters.AccessToken.CustomOAuth2AccessTokenResponseConverter;
import com.horasan.oauth2.intercepters.AccessToken.CustomOAuth2AuthorizationCodeGrantRequestConverter;
import com.horasan.oauth2.intercepters.AuthorizationCode.CustomAuthorizationRequestResolverBeforeRedirectingForAuthentication;

@Configuration
@EnableOAuth2Client
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("customOauth2authSuccessHandler")
	private Oauth2AuthenticationSuccessHandlerAfterSuccessfulAuthentication oauth2authSuccessHandler;

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/login", "/oauth2/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.exceptionHandling().authenticationEntryPoint(new RestAuthEntryPointForUnauthenticatedCalls()).and()
				.csrf().disable();

		http.oauth2Login().successHandler(oauth2authSuccessHandler);
		
		http.oauth2Login().failureHandler(authenticationFailureHandler());
				
		http.oauth2Login().authorizationEndpoint()
				.authorizationRequestResolver(new CustomAuthorizationRequestResolverBeforeRedirectingForAuthentication(
						clientRegistrationRepository,
						OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI // "/oauth2/authorization"
				));

		http.oauth2Login().tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient());
		
		http.sessionManagement()
		  .sessionFixation()
		  .none();
		
		
	}

	@Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandlerAfterUnsuccessfulAuthentication();
    }
	
	@Bean
	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		accessTokenResponseClient.setRequestEntityConverter(new CustomOAuth2AuthorizationCodeGrantRequestConverter());

		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
		tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomOAuth2AccessTokenResponseConverter());
		RestTemplate restTemplate = new RestTemplate(
				Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

		accessTokenResponseClient.setRestOperations(restTemplate);
		return accessTokenResponseClient;
	}
}
