package com.citrus.asynch;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrus.mobile.Callback;
import com.citrus.mobile.User;

import android.app.Activity;
import android.os.AsyncTask;

public class SigninMobile extends AsyncTask<Void, Void, JSONObject> {
	
	Activity activity;
	
	String mobile;
	
	Callback callback;
	
	public SigninMobile(Activity activity, Callback callback, String mobile) {
		this.activity = activity;
		this.mobile = mobile;
		this.callback = callback;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		User citrususer = new User(activity);
		
		JSONObject response = null;
		
		JSONObject userprofile = citrususer.getuserProfile(mobile);
		
		if (userprofile.has("responseData")) {
			JSONObject profilebyMobile = null;
			try {
				profilebyMobile = userprofile.getJSONObject("profileByMobile");
				
				if (profilebyMobile.has("email")) {
					response = citrususer.signinUser(profilebyMobile.getString("email"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		
		if (result.has("error")) {
			callback.onTaskexecuted("", result.toString());
		}
		else {
			callback.onTaskexecuted(result.toString(), "");
		}
		
	}

}