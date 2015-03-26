package com.citrus.asynch;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Errorclass;
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
		
		if (userprofile.has("error")) 
				response = userprofile;
		
		if (userprofile.has("responseData")) {
			JSONObject profilebyMobile = null;
			try {
				profilebyMobile = userprofile.getJSONObject("responseData").getJSONObject("profileByMobile");
				if (profilebyMobile.has("email")) {
					response = citrususer.signinUser(profilebyMobile.getString("email"));
				}
				else {
					response = Errorclass.addErrorFlag("No User mapped against this number", null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			response = Errorclass.addErrorFlag("Could not fetch user against mobile number", null);
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