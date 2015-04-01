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
package com.citrus.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WebPage extends Activity {
    WebView webView;
    
    BroadcastReceiver receiver;
    
    Button submit;
    
    String js, otp;
    
    TextView otpview;
    
    private BroadcastReceiver mMessageReceiver;

    @TargetApi(Build.VERSION_CODES.L)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        
        initReceiver();
        
        String url = getIntent().getStringExtra("url");

        webView = (WebView) this.findViewById(R.id.webview);
        
        submit = (Button) this.findViewById(R.id.submitButton);
        
        otpview = (TextView) this.findViewById(R.id.otpText);

        webView.getSettings().setJavaScriptEnabled(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        webView.addJavascriptInterface(new JsInterface(), "CitrusResponse");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
                
        webView.loadUrl(url);

    }
    
   
    private class JsInterface {

        @JavascriptInterface
        public void pgResponse(String response) {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
        }
    }
    
    private void initReceiver() {
    	mMessageReceiver = new BroadcastReceiver() {
        	@Override
        	public void onReceive(Context context, Intent intent) {
        		js = intent.getStringExtra("js");
        		webView.loadUrl(js);
        	}
        };
        
        registerReceiver(mMessageReceiver, new IntentFilter("single_tap_otp"));
    }
    
}