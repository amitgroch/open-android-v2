package com.citrus.asynch;

import org.json.JSONObject;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Errorclass;
import com.citrus.wallet.CardDelete;

import android.app.Activity;
import android.os.AsyncTask;

public class DeleteCard extends AsyncTask<String, Void, JSONObject>{
	String cardlast4, scheme;
	
	Activity activity; Callback callback;
	
	public DeleteCard(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		
		if (params.length < 2) {
			return Errorclass.addErrorFlag("Please pass all the details", null);
		}
		
		else {
			this.cardlast4 = params[0];
			this.scheme = params[1];
			CardDelete cardDelete = new CardDelete(cardlast4, scheme);
			
			return cardDelete.deletecard(activity);
		}
		
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		
		if (result.has("error")) {
			callback.onTaskexecuted("", result.toString());
		}
		
		else {
			callback.onTaskexecuted(result.toString(), null);
		}
		
	}
	
}
