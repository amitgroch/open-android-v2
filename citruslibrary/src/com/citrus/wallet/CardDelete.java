package com.citrus.wallet;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.citrus.mobile.Config;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;

public class CardDelete {
	String cardnum, scheme;
	
	public CardDelete(String cardnum, String scheme) {
		this.cardnum = cardnum;
		this.scheme = scheme;
	}
	
	public JSONObject deletecard(Activity activity) {
		
		OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
		
		JSONObject params = new JSONObject();
		JSONObject headers = new JSONObject();
				
		try {
			headers.put("Authorization", "Bearer " + token.getuserToken().getString("access_token"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		RESTclient restclient = new RESTclient("wallet", Config.getEnv(), params, headers);
	
		JSONObject response = restclient.deletecard(cardnum, scheme);
		
		return response;
	}
}
