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
package com.citrus.asynch;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.citrus.mobile.Callback;
import com.citrus.mobile.User;

public class Binduser extends AsyncTask<String, Void, Void> {

    JSONObject binderesult;
    Activity activity;
    Callback callback;
    User user;

    public Binduser(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {
        user = new User(activity);
        String email;
        if (TextUtils.isEmpty(params[0])) {
        	email = params[1] + "@tinyowl-citruspay.com"; 
        }
        else {
        	email = params[0];
        }
        binderesult = user.binduser(email, params[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        
        if (binderesult != null) {
        	
        	if (binderesult.has("error")) {
        		callback.onTaskexecuted("", binderesult.toString());	
        	}
        	else {
        		callback.onTaskexecuted(binderesult.toString(), "");	
        	}
        	
        }
    }
}
