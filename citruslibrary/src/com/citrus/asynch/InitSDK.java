package com.citrus.asynch;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citrus.interfaces.InitListener;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.sdkui.CardOption;
import com.citrus.sdkui.NetbankingOption;
import com.citrus.sdkui.PaymentOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MANGESH KADAM on 2/17/2015.
 */
public class InitSDK {

    InitListener initListener;
    Context context;
    Callback bindCallBack;
    Callback walletCallBack;
    Response.Listener successListener;
    Response.ErrorListener errorListener;
    String emailId = null;
    String mobileNo = null;

    public InitSDK(Context context, InitListener initListener, String emailId, String mobileNo) {
        this.context = context;
        this.initListener = initListener;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        initListeners();
        bindUser();
    }

    private void bindUser() {

        new Binduser(context, bindCallBack).execute(emailId, mobileNo);
    }

    private void initListeners() {
        bindCallBack = new Callback() {
            @Override
            public void onTaskexecuted(String response, String error) {
                if (response.equalsIgnoreCase("User Bound Successfully!")) {
                    new GetWallet(context, walletCallBack).execute();
                } else {
                    initListener.onBindFailed(error);
                }
            }
        };

        walletCallBack = new Callback() {
            @Override
            public void onTaskexecuted(String response, String error) {
                if (TextUtils.isEmpty(response)) {
                    initListener.onWalletLoadFailed(error);
                } else {
                    ArrayList<PaymentOption> walletList = new ArrayList<PaymentOption>();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray paymentOptions = jsonObject.optJSONArray("paymentOptions");


                        if (paymentOptions != null) {
                            for (int i = 0; i < paymentOptions.length(); i++) {
                                PaymentOption option = PaymentOption.fromJSONObject(paymentOptions.getJSONObject(i));

                                // NOTE: Add only Cards, no need to add netbanking at this moment.
                                // As per decided.
                                if (option instanceof CardOption) {
                                    walletList.add(option);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Config.setCitrusWallet(walletList);
                }

                new GetNetBankingList(context, successListener, errorListener).getBankList();
            }
        };

        successListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    ArrayList<NetbankingOption> bankDetails = new ArrayList<>();
                    ArrayList<NetbankingOption> topBankList = new ArrayList<>();
                    JSONObject pgSetting = new JSONObject(response);
                    JSONArray bankArray = pgSetting.getJSONArray("netBanking");
                    int size = bankArray.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject bankOption = bankArray.getJSONObject(i);
                        String bankName = bankOption.optString("bankName");
                        String issuerCode = bankOption.getString("issuerCode");
                        if (!TextUtils.isEmpty(bankName) && !TextUtils.isEmpty(issuerCode)) {
                            NetbankingOption netbankingOption = new NetbankingOption(bankName, issuerCode);

                            // Check whether the bank is from top bank list or other bank
                            // Currently the top banks are AXIS (CID002), ICICI (CID001), SBI (CID005) and HDFC (CID010).
                            if ("CID002".equalsIgnoreCase(issuerCode) || "CID001".equalsIgnoreCase(issuerCode) || "CID005".equalsIgnoreCase(issuerCode) || "CID010".equalsIgnoreCase(issuerCode)) {
                                topBankList.add(netbankingOption);
                            } else {
                                bankDetails.add(netbankingOption);
                            }
                        }
                    }
                    // Set the bank lists.
                    Config.setBankList(bankDetails);
                    Config.setTopBankList(topBankList);

                    initListener.onSuccess("SUCCESS");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initListener.onNetBankingListFailed(error);
            }
        };
    }
}
