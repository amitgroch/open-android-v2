/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.mobile;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;


public class OauthToken {
    private static final String STORED_VALUES = "UserStorage";

    private Activity activity;

    private JSONObject jsontoken;

    private SharedPreferences tokenPrefs;

    private String base_url, token_type;

    public OauthToken(Activity activity, String token_type) {
        this.activity = activity;
        tokenPrefs = this.activity.getSharedPreferences(STORED_VALUES, 0);
        base_url = Config.getEnv();
        this.token_type = token_type;
    }

    public boolean createToken(JSONObject usertoken) {

        jsontoken = new JSONObject();

        long expiry = new Date().getTime()/1000l;

        try {
            expiry += usertoken.getLong("expires_in");
            jsontoken.put("expiry", expiry);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Iterator<?> keys = usertoken.keys(); keys.hasNext();) {
            String key = (String) keys.next();

            try {
                jsontoken.put(key, usertoken.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return storeToken();

    }

    private boolean storeToken() {
        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.putString(token_type, jsontoken.toString());
        return editor.commit();
    }

    public JSONObject getuserToken() {
        JSONObject token = null;
        try {
        	if (tokenPrefs.contains(token_type)) {
                token = new JSONObject(tokenPrefs.getString(token_type, null));
        	}
        	else {
        		return null;
        	}
       
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (token.has("refresh_token")) {
            return refreshToken(token);
        }
        else {
            return token;
        }

    }

    private JSONObject refreshToken(JSONObject token) {

        if (hasExpired(token)) {
            try {
                return refresh(token.getString("refresh_token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return token;

    }

    private boolean hasExpired(JSONObject token) {
        try {
            return token.getLong("expiry") <= (new Date().getTime() / 1000l);
        } catch (JSONException e) {
            return true;
        }
    }

    private JSONObject refresh(String refreshToken) {
        JSONObject response = new JSONObject();

        JSONObject userJson = new JSONObject();

        try {
            userJson.put("client_id", Config.getSigninId());

            userJson.put("client_secret", Config.getSigninSecret());

            userJson.put("grant_type", "refresh_token");

            userJson.put("refresh_token", refreshToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Content-Type", "application/x-www-form-urlencoded");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RESTclient restclient = new RESTclient("signin", base_url, userJson, headers);

        try {
            response = restclient.makePostrequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.has("access_token")) {
            jsontoken = response;
            storeToken();
            return response;
        }
        else {
            return null;
        }
    }
}
