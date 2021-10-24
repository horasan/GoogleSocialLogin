package com.horasan.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleOAuth2CustomerInfo {

	private String sub;
	private String name;
	private String given_name;
	private String family_name;
	private String picture;
	private String email;
	private Boolean email_verified;
	private String locale;
	
}
