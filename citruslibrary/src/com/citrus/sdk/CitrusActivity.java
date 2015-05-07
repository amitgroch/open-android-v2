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

package com.citrus.sdk;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.citrus.asynch.GetBill;
import com.citrus.cash.LoadMoney;
import com.citrus.cash.PersistentConfig;
import com.citrus.cash.Prepaid;
import com.citrus.library.R;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;

import org.json.JSONException;
import org.json.JSONObject;

public class CitrusActivity extends ActionBarActivity {

    private WebView mPaymentWebview = null;
    private String mUrl = null;
    private Context mContext = this;
    private ProgressDialog mProgressDialog = null;
    private PaymentParams mPaymentParams = null;
    private PaymentType mPaymentType = null;
    private PaymentOption mPaymentOption = null;
    private String mTransactionId = null;
    private ActionBar mActionBar = null;
    private String mColorPrimary = null;
    private String mColorPrimaryDark = null;
    private String mVanity = null;
    private String mMerchantOrItemName = null;

    String sessionCookie;

    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citrus);

        mPaymentParams = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS);

        // Set payment Params
        if (mPaymentParams != null) {
            mPaymentType = mPaymentParams.getPaymentType();
            mPaymentOption = mPaymentParams.getPaymentOption();
            mColorPrimary = mPaymentParams.getColorPrimary();
            mColorPrimaryDark = mPaymentParams.getColorPrimaryDark();
            mVanity = mPaymentParams.getVanity();
        } else {
            throw new IllegalArgumentException("Payment Params Should not be null");
        }

        mActionBar = getSupportActionBar();
        mProgressDialog = new ProgressDialog(mContext);
        mPaymentWebview = (WebView) findViewById(R.id.payment_webview);
        mPaymentWebview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
            This setting is required to enable redirection of urls from https to http or vice-versa.
            This redirection is blocked by default from Lollipop (Android 21).
             */
            mPaymentWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mPaymentWebview.addJavascriptInterface(new JsInterface(), Constants.JS_INTERFACE_NAME);

        mPaymentWebview.setWebChromeClient(new WebChromeClient());

        mPaymentWebview.setWebViewClient(new CitrusWebClient());
        if (mPaymentType instanceof PaymentType.PGPayment || mPaymentType instanceof PaymentType.CitrusCash) {
            if (mPaymentType.getPaymentBill() != null) {
                // TODO Need to refactor the code.
                if(PaymentBill.toJSONObject(mPaymentType.getPaymentBill()) != null) {
                    proceedToPayment(PaymentBill.toJSONObject(mPaymentType.getPaymentBill()).toString());
                }
            } else {
                fetchBill();
            }
        } else { //load cash does not requires Bill Generator
            Amount amount = mPaymentType.getAmount();

            LoadMoney loadMoney = new LoadMoney(amount.getValue(), mPaymentType.getUrl());
            PG paymentgateway = new PG(mPaymentOption, loadMoney, new UserDetails(CitrusUser.toJSONObject(mPaymentParams.getUser())));

            paymentgateway.load(CitrusActivity.this, new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                }
            });
        }

        setTitle("Processing...");
        setActionBarBackground(mColorPrimary, mColorPrimaryDark);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setActionBarBackground(String colorPrimary, String colorPrimaryDark) {
        // Set primary color
        if (mColorPrimary != null && mActionBar != null) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorPrimary)));
        }

        // Set action bar color. Available only on android version Lollipop or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mColorPrimaryDark != null) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(colorPrimaryDark));
        }
    }

    private void fetchBill() {
        showDialog("Validating Payment Details. Please wait...", false);

        String billUrl = mPaymentType.getUrl();

        new GetBill(billUrl, new Callback() {
            @Override
            public void onTaskexecuted(String bill, String error) {
                dismissDialog();

                if (!android.text.TextUtils.isEmpty(error)) {
                    Toast.makeText(CitrusActivity.this, error, Toast.LENGTH_SHORT).show();

                    TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error, mTransactionId);
                    sendResult(transactionResponse);
                } else {
                    proceedToPayment(bill);
                }
            }
        }).execute();
    }

    private void proceedToPayment(String billJSON) {


        if(mPaymentType instanceof PaymentType.CitrusCash) { //pay using citrus cash

            final CitrusUser citrusUser = mPaymentParams.getUser();

            UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(citrusUser));
            Prepaid prepaid = new Prepaid(userDetails.getEmail());
            Bill bill = new Bill(billJSON);
            mTransactionId = bill.getTxnId();
            PG paymentgateway = new PG(prepaid, bill, userDetails);
            if (bill.getCustomParameters() != null) {
                paymentgateway.setCustomParameters(bill.getCustomParameters());
            }
            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    //showDialog("Redirecting to Citrus. Please wait...", false);
                    prepaidPayment(success, error);
                }
            });

        }
        else {

            showDialog("Redirecting to Citrus. Please wait...", false);
            final CitrusUser citrusUser = mPaymentParams.getUser();
            UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(citrusUser));
            Bill bill = new Bill(billJSON);
            mTransactionId = bill.getTxnId();

            PG paymentgateway = new PG(mPaymentOption, bill, userDetails);

            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                    dismissDialog();
                }
            });
        }
    }

    private void processresponse(String response, String error) {

        TransactionResponse transactionResponse = null;
        if (!android.text.TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                if (!android.text.TextUtils.isEmpty(redirect.getString("redirectUrl"))) {

                    mPaymentWebview.loadUrl(redirect.getString("redirectUrl"));
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                    transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, response, mTransactionId);
                    sendResult(transactionResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error, mTransactionId);
            sendResult(transactionResponse);
        }

    }

    private void prepaidPayment(String response, String error) {

        TransactionResponse transactionResponse = null;
        if (!android.text.TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                if (!android.text.TextUtils.isEmpty(redirect.getString("redirectUrl"))) {
                    setCookie();
                    mPaymentWebview.loadUrl(redirect.getString("redirectUrl"));
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                    transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, response, mTransactionId);
                    sendResult(transactionResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error, mTransactionId);
            sendResult(transactionResponse);
        }

    }


    private void showDialog(String message, boolean cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.CANCELLED, "Cancelled By User", mTransactionId);
                sendResult(transactionResponse);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        // Set other dialog properties
        builder.setMessage("Do you want to cancel the transaction?")
                .setTitle("Cancel Transaction?");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void setCookie(){

        cookieManager = CookieManager.getInstance();
        sessionCookie = new PersistentConfig(CitrusActivity.this).getCookieString();
        cookieManager.setCookie(Config.getBaseURL(), sessionCookie);
    }


    private  static void removeCookies() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                }
            });
        }
        else {
            CookieManager.getInstance().removeAllCookie();
        }
    }
    private void sendResult(TransactionResponse transactionResponse) {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE, transactionResponse);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPaymentWebview != null) {
            mPaymentWebview.stopLoading();
            mPaymentWebview.destroy();
        }
        mPaymentWebview = null;
        mUrl = null;
    }

    /**
     * Handle all the Webview loading in custom webview client.
     */
    private class CitrusWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Let this webview handle all the urls loaded inside. Return false to denote that.
            view.loadUrl(url);

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // Display the message.
            showDialog("Processing your payment. Please do not refresh the page.", true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // Dismiss the progress/message dialog.
            dismissDialog();
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }

    /**
     * This class will be loaded as JSInterface and the methods of this class will be called from
     * the javascript loaded inside webview.
     * <p/>
     * Handle the payment response and take actions accordingly.
     */
    private class JsInterface {

        @JavascriptInterface
        public void pgResponse(String response) {
            if(mPaymentType instanceof PaymentType.CitrusCash){
                removeCookies();
            }
            TransactionResponse transactionResponse = TransactionResponse.fromJSON(response);
            sendResult(transactionResponse);
        }

        /**
         * This method will be called by returnURL when Cash is loaded in user's account
         *
         * @param response post parameters sent by Citrus
         */
        @JavascriptInterface
        public void loadWalletResponse(String response) {

          /*  if (response.contains(":")) {
                String decodeResp[] = response.split(":");
                if (decodeResp.length > 0) {
                    if (TextUtils.equals(decodeResp[0], "SUCCESSFUL")) {
                        Toast.makeText(getApplicationContext(), "Your wallet is loaded Successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        //transaction was fail
                        Toast.makeText(getApplicationContext(), "Wallet Load Failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }*/

            TransactionResponse transactionResponse = TransactionResponse.parseLoadMoneyResponse(response);
            sendResult(transactionResponse);
        }
    }
}
