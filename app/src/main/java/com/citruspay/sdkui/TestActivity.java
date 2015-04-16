package com.citruspay.sdkui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.citrus.sdk.Constants;
import com.citrus.sdk.Utils;
import com.citrus.sdk.ui.CitrusActivity;
import com.citrus.sdk.ui.CitrusPaymentParams;
import com.citrus.sdk.ui.CitrusTransactionResponse;
import com.citrus.sdk.ui.classes.CitrusUser;


public class TestActivity extends ActionBarActivity {

    private static final String SANDBOX_BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.mangesh.php";// host your bill url here
    private static final String PROD_BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.production.php";// host your bill url here
    private static final String JSON_KEY_STORE = "{\"access_key\":\"06SLEEBYLVZELISZ5ECU\",\"signup-id\":\"kkizp9tsqg-signup\",\"signup-secret\":\"39c50a32eaabaf382223fdd05f331e1c\",\"signin-id\":\"kkizp9tsqg-signin\",\"signin-secret\":\"1fc1f57639ec87cf4d49920f6b3a2c9d\",\"vanity_Url\":\"https://www.citruspay.com/kkizp9tsqg\"}";
    private static final String JSON_KEY_STORE_DEMO = "{\"access_key\":\"06SLEEBYLVZELISZ5ECU\",\"signup-id\":\"test-signup\",\"signup-secret\":\"c78ec84e389814a05d3ae46546d16d2e\",\"signin-id\":\"test-signin\",\"signin-secret\":\"52f7e15efd4208cf5345dd554443fd99\",\"vanity_Url\":\"https://www.citruspay.com/nativeSDK\"}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onPaySandboxButtonClicked(View view) {

        Intent intent = new Intent(TestActivity.this, CitrusActivity.class);
        CitrusUser user = new CitrusUser("tester46@gmail.com", "1234567890", "Developer", "Citrus", null);

        CitrusPaymentParams paymentParams = CitrusPaymentParams
                .builder(3.0, SANDBOX_BILL_URL, JSON_KEY_STORE_DEMO)
                .user(user)
                .environment(CitrusPaymentParams.Environment.SANDBOX)
                .merchantName("Nature First")
                .build();

        intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        startActivityForResult(intent, Utils.REQUEST_CODE_PAYMENT_ACTIVITY);
    }


    public void onPayProductionButtonClicked(View view) {

        Intent intent = new Intent(TestActivity.this, CitrusActivity.class);

        CitrusUser user = new CitrusUser("salilgodbole@gmail.com", "1234567890", "Salil", "Godbole", null);
        CitrusPaymentParams paymentParams = CitrusPaymentParams
                .builder(3.0, PROD_BILL_URL, JSON_KEY_STORE_DEMO)
                .user(user)
                .merchantName("Nature First")
                .environment(CitrusPaymentParams.Environment.PRODUCTION)
                .build();

        intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        startActivityForResult(intent, Utils.REQUEST_CODE_PAYMENT_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            CitrusTransactionResponse transactionResponse = data.getParcelableExtra(Utils.INTENT_EXTRA_PAYMENT_RESPONSE);
            if (transactionResponse != null) {
                Log.e("Citrus", " transactionResponse : " + transactionResponse.toString());
            }
        }
    }
}
