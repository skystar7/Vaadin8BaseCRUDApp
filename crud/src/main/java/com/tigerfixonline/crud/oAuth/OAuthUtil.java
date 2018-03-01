package com.tigerfixonline.crud.oAuth;

import java.util.Random;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

public class OAuthUtil {

	private static final String callBackString = "http://localhost:8080/callback/";

	public static OAuth20Service createGoogleService() {
		String clientsecret = GoogleClientIDWeb.clientSecret;
		final String secretState = "secret" + new Random().nextInt(999_999);
		OAuth20Service service = new ServiceBuilder(GoogleClientIDWeb.clientId).apiSecret(clientsecret).scope("profile")
				.state(secretState).callback(callBackString).build(GoogleApi20.instance());
		return service;
	}
}
