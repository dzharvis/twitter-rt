package com.dzharvis.auth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Authenticatior {
	private static Twitter twitter;
	private static String token = "381687774-b3kWP458bgG0SIVafTHIQzAGVxl9Gnqe31Gok91J";
	private static String tokenSecret = "GIoVP0NJcQ5IkkL9TlE1uoYIN9dlKlpc8fdyHur4YKA";
	
	public static void auth(){
		if (token == null && tokenSecret == null) {
			makeOAuth();
		} else {
			initTwitter();
		}
	}

	private static void makeOAuth() {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("OA8ufcmmu5LYVAFP8NstCQ", "DpkyRzEBvIPzDrYsk2KDF9SnRCt1c5PLn71R6lekU");
		RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = null;
			try {
				pin = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (pin.length() > 0) {
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// persist to the accessToken for future reference.
		try {
			storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private static void initTwitter() {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("OA8ufcmmu5LYVAFP8NstCQ", "DpkyRzEBvIPzDrYsk2KDF9SnRCt1c5PLn71R6lekU");
		twitter.setOAuthAccessToken(loadAccessToken());
	}

	private static AccessToken loadAccessToken() {
		return new AccessToken(token, tokenSecret);
	}

	private static void storeAccessToken(long useId, AccessToken accessToken) {
		token = accessToken.getToken();
		tokenSecret = accessToken.getTokenSecret();

		System.out.println("token = " + token);
		System.out.println("tokenSecret = " + tokenSecret);
	}
}
