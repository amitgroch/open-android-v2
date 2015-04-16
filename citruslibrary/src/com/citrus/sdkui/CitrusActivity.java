package com.citrus.sdkui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.citrus.asynch.InitSDK;
import com.citrus.interfaces.InitListener;
import com.citrus.mobile.Callback;
import com.citrus.netbank.Bank;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citrus.sdkui.classes.CardOption;
import com.citrus.sdkui.classes.CitrusCash;
import com.citrus.sdkui.classes.NetbankingOption;
import com.citrus.sdkui.classes.PaymentOption;
import com.citruspay.citruslibrary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.citrus.sdkui.CitrusTransactionResponse.TransactionStatus;
import static com.citrus.sdkui.PaymentProcessingFragment.OnTransactionCompleteListener;
import static com.citrus.sdkui.PaymentStatusFragment.OnTransactionResponseListener;


public final class CitrusActivity extends ActionBarActivity implements FragmentEventsListeners, OnPaymentOptionSelectedListener, OnTransactionResponseListener, OnTransactionCompleteListener, ProcessPaymentListener, InitListener {

    private String mMerchantName = null;
    private String mMerchantBillUrl = null;
    private double mTransactionAmount = 0.0;
    private ProgressDialog mProgressDialog = null;
    private FragmentManager mFragmentManager = null;
    private CitrusPaymentParams mPaymentParams = null;
    private String mColorPrimary = null;
    private String mColorPrimaryDark = null;
    private ActionBar mActionBar = null;
    private CitrusTransactionResponse mTransactionResponse;
    private boolean mShowDialog = false;
    private boolean mNewCardAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_citrus);

        // Restrict the activity orientation to portrait only.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mActionBar = getSupportActionBar();
        mPaymentParams = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS);

        // Get required details from intent.
        if (mPaymentParams != null) {
            mTransactionAmount = mPaymentParams.transactionAmount;

            mMerchantBillUrl = mPaymentParams.billUrl;
            mMerchantName = mPaymentParams.merchantName;

            mColorPrimary = mPaymentParams.colorPrimary;
            mColorPrimaryDark = mPaymentParams.colorPrimaryDark;

            setActionBarBackground(mColorPrimary, mColorPrimaryDark);

            setCustomTitleView();
        }

        mProgressDialog = new ProgressDialog(this);
        mFragmentManager = getSupportFragmentManager();

        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                onBackPressed();
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissDialog();
        mProgressDialog = null;
        mFragmentManager = null;
        mColorPrimaryDark = null;
        mColorPrimary = null;
        mMerchantBillUrl = null;
        mPaymentParams = null;
    }

    @Override
    public void onBackPressed() {

        // TODO: Move this alert dialog inside the fragment.
        // TODO: Create a super fragment and then inherit all the fragments from it.
        if (mShowDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button

                    dialog.dismiss();

                    CitrusTransactionResponse transactionResponse = new CitrusTransactionResponse(TransactionStatus.FAIL, "Cancelled by the user.", "");
                    showPaymentStatusFragment(transactionResponse, mPaymentParams);
                }
            });
            builder.setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            // Set other dialog properties

            builder.setMessage(R.string.message_transaction_cancel)
                    .setTitle(R.string.message_title_transaction_cancel);
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            setActionBarBackground(mColorPrimary, mColorPrimaryDark);

            setCustomTitleView();

            super.onBackPressed();
            mShowDialog = true;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        // Disable custom title.
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(false);

        super.setTitle(title);
    }

    void setCustomTitleView() {
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.layout_action_bar_view, null);

        if (mActionBar != null && mPaymentParams != null) {

            TextView txtMerchantName = (TextView) view.findViewById(R.id.txt_merchant_name);
            TextView txtTransactionAmount = (TextView) view.findViewById(R.id.txt_transaction_amount);

            txtMerchantName.setText(mMerchantName);
            txtMerchantName.setTextColor(Color.parseColor(mPaymentParams.textColorPrimary));

            // &#x20B9; stands for Rupee symbol.
            txtTransactionAmount.setText(Html.fromHtml("&#x20B9;" + mPaymentParams.transactionAmount));
            txtTransactionAmount.setTextColor(Color.parseColor(mPaymentParams.textColorPrimary));

            // Enable custom title.
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);

            mActionBar.setCustomView(view);
        }
    }

    private void init() {
        new InitSDK(this, this, mPaymentParams);

        showDialog("Processing Your Payment...", true);
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

    private void processResponse(String response, String error) {
        dismissDialog();

        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);

                if (!TextUtils.isEmpty(redirect.getString("redirectUrl"))) {
                    showPaymentProcessingFragment(redirect.getString("redirectUrl"));
                } else {
                    Utils.showToast(getApplicationContext(), response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast(getApplicationContext(), error);
        }
    }

    private void showPaymentOptionsFragment() {
        setCustomTitleView();

        mFragmentManager.beginTransaction()
                .replace(R.id.container, PaymentOptionsFragment.newInstance(mPaymentParams))
                .commit();

        dismissDialog();

        mShowDialog = true;
    }

    private void showAddCardFragment() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(
                R.id.container, NewCardPaymentFragment.newInstance(mPaymentParams));
        ft.addToBackStack(null);
        ft.commit();

        // No need to display the dialog, since the user will be migrating to the payment fragment on backbutton press.
        mShowDialog = false;
    }

    private void showSavedCardPaymentFragment(final CardOption cardOption) {
        setActionBarBackground("#414A5A", "#2B313D");

        setTitle("256-bit secure encryption");

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(
                R.id.container, SavedCardPaymentFragment.newInstance(cardOption, mPaymentParams));
        ft.addToBackStack(null);
        ft.commit();

        mShowDialog = false;
    }

    private void showNetbankingFragment() {
        ArrayList<NetbankingOption> netbankingOptionsList = mPaymentParams.netbankingOptionList;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(
                R.id.container, NetbankingPaymentFragment.newInstance(netbankingOptionsList));
        ft.addToBackStack(null);
        ft.commit();

        mShowDialog = false;
    }

    private void showPaymentProcessingFragment(String redirectUrl) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(
                R.id.container, PaymentProcessingFragment.newInstance(redirectUrl));
        ft.addToBackStack(null);
        ft.commit();

        mShowDialog = true;
    }

    private void showPaymentStatusFragment(CitrusTransactionResponse transactionResponse, CitrusPaymentParams paymentParams) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(
                R.id.container, PaymentStatusFragment.newInstance(transactionResponse, paymentParams));
        // No need to add this fragment to backstack as we do not need to show the payment options and directly go back to previous activity.
//        ft.addToBackStack(null);
        ft.commit();

        mShowDialog = false;
    }

    private void processNebankingPayment(final NetbankingOption netbankingOption) {
        showDialog("Redirecting to bank's page.", false);

        new GetBill(mMerchantBillUrl, mTransactionAmount, new Callback() {
            @Override
            public void onTaskexecuted(String billString, String error) {
                Bill bill = new Bill(billString);

                Bank netbank = new Bank(netbankingOption.getBankCID());

                // TODO Make token payment for bank
                // Token payment for bank may not be needed till we show saved netbanking options.

                UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(mPaymentParams.getUser()));

                PG paymentgateway = new PG(netbank, bill, userDetails);

                paymentgateway.charge(new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processResponse(success, error);
                    }
                });
            }
        }).execute();
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

    private void sendResponse(CitrusTransactionResponse transactionResponse) {
        Intent intent = new Intent();
        intent.putExtra(Utils.INTENT_EXTRA_PAYMENT_RESPONSE, transactionResponse);
        setResult(Utils.REQUEST_CODE_PAYMENT_ACTIVITY, intent);
        finish();
    }

    // Listeners

    @Override
    public void onActivityTitleChanged(String title) {
        setTitle(title);
    }

    @Override
    public void onNewCardAdded(boolean cardAdded) {
        mNewCardAdded = cardAdded;
    }

    @Override
    public void onOptionSelected(final PaymentOption paymentOption) {

        if (paymentOption != null) {
            if (paymentOption instanceof CardOption) {
                final CardOption cardOption = (CardOption) paymentOption;

                // If the Add Card button is clicked show the fragment to Add New Card.
                if (cardOption == CardOption.DEFAULT_CARD) {
                    showAddCardFragment();
                } else {
                    showSavedCardPaymentFragment(cardOption);
                }
            } else if (paymentOption instanceof NetbankingOption) {

                final NetbankingOption netbankingOption = (NetbankingOption) paymentOption;

                // If the bank is selected other than the top 4 banks, show the list of available banks.
                if (netbankingOption == NetbankingOption.DEFAULT_BANK) {
                    showNetbankingFragment();
                } else {
                    processNebankingPayment(netbankingOption);
                }
            } else if (paymentOption instanceof CitrusCash) {
                // TODO: Make payment using citrus cash
            }
        } else {
            Utils.showToast(getApplicationContext(), "Something went wrong..");
        }
    }

    @Override
    public void onInitCompleted() {
        showPaymentOptionsFragment();
    }

    @Override
    public void onReceiveNetbankingList(ArrayList<NetbankingOption> listNetbanking, ArrayList<NetbankingOption> listTopNetbanking) {
        mPaymentParams.netbankingOptionList = listNetbanking;
        mPaymentParams.topNetbankingOptions = listTopNetbanking;
    }

    @Override
    public void onFailToReceiveNetbankingList(String errorMessage) {
        Log.d(Utils.TAG, "Failed to receive netbanking list " + errorMessage);
    }

    @Override
    public void onReceiveSavedOptions(ArrayList<PaymentOption> listSavedOption) {
        mPaymentParams.userSavedOptionList = listSavedOption;
    }

    @Override
    public void onFailToReceiveSavedOptions(String errorMessage) {
        Log.d(Utils.TAG, errorMessage);
    }

    @Override
    public void onTransactionComplete(CitrusTransactionResponse transactionResponse) {
        mTransactionResponse = transactionResponse;
        // Remove the payment processing fragment.
        mFragmentManager.popBackStack();
        // Show payment status fragment.
        showPaymentStatusFragment(transactionResponse, mPaymentParams);
    }

    // TODO: Set the title of the activity depending upon the transcation status.

    @Override
    public void onRetryTransaction() {

        // Fetch the saved cards since new card has been added.
        if (mNewCardAdded) {
            init();
        } else {
            showPaymentOptionsFragment();
        }
    }

    @Override
    public void onDismiss() {
        sendResponse(mTransactionResponse);

        mShowDialog = false;
    }

    @Override
    public void processPayment(final String response, final String error) {
        setActionBarBackground(mColorPrimary, mColorPrimaryDark);

        mFragmentManager.popBackStack();

        processResponse(response, error);
    }
}
