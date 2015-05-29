/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sample;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.citrus.sdk.CitrusClient;


public class UIActivity extends ActionBarActivity {

    private FragmentManager fragmentManager = null;
    private Context mContext = this;
    private CitrusClient citrusClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        fragmentManager = getSupportFragmentManager();

        citrusClient = CitrusClient.getInstance(mContext);
        citrusClient.init("test-signup", "c78ec84e389814a05d3ae46546d16d2e", "test-signin", "52f7e15efd4208cf5345dd554443fd99", "prepaid", CitrusClient.Environment.SANDBOX);
    }

    public void onUserManagementClicked(View view) {
        UserManagementFragment fragment = UserManagementFragment.newInstance(this);

        fragmentManager.beginTransaction().add(R.id.fragment, fragment).commit();
    }

    public void onPaymentClicked(View view) {
    }
}
