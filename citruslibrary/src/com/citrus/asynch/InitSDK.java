package com.citrus.asynch;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citrus.interfaces.InitListener;
import com.citrus.mobile.Callback;
import com.citrus.sdkui.CitrusPaymentParams;
import com.citrus.sdkui.CitrusUser;
import com.citrus.sdkui.classes.CardOption;
import com.citrus.sdkui.classes.NetbankingOption;
import com.citrus.sdkui.classes.PaymentOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MANGESH KADAM on 2/17/2015.
 */
public final class InitSDK {

    private InitListener mListener;
    private Context mContext;
    private String mEmailId;
    private String mMobileNo;
    private String mVanity;

    public InitSDK(Context context, InitListener listener, CitrusPaymentParams paymentParams) {
        mContext = context;
        mListener = listener;

        if (listener == null || paymentParams == null) {
            throw new IllegalArgumentException("PaymentParams or Listener can not be null");
        }

        CitrusUser user;
        if ((user = paymentParams.getUser()) != null) {
            mEmailId = user.getEmailId();
            mMobileNo = user.getMobileNo();
        }

        mVanity = paymentParams.getVanity();

        fetchBankList();
    }

    private void fetchBankList() {
        new GetNetBankingList(mContext, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<NetbankingOption> listNetbankingOptions = new ArrayList<>();
                ArrayList<NetbankingOption> listTopNetbanking = new ArrayList<>();
                try {
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
                                listTopNetbanking.add(netbankingOption);
                            } else {
                                listNetbankingOptions.add(netbankingOption);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mListener.onReceiveNetbankingList(listNetbankingOptions, listTopNetbanking);
                    bindUser();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onFailToReceiveNetbankingList(error.getMessage());

                // Try binding the user
                bindUser();
            }
        }).getBankList(mVanity);
    }

    private void bindUser() {

        // Call only if the email id is provided.
        if (!TextUtils.isEmpty(mEmailId)) {
            new Binduser(mContext, new Callback() {
                @Override
                public void onTaskexecuted(String response, String error) {
                    if (response.equalsIgnoreCase("User Bound Successfully!")) {
                        fetchWallet();
                    } else {
                        mListener.onFailToReceiveSavedOptions(error);
                        mListener.onInitCompleted();
                    }
                }
            }).execute(mEmailId, mMobileNo);
        } else {
            mListener.onFailToReceiveSavedOptions("Email id and mobile no should not be blank for processing member payment.");
            mListener.onInitCompleted();
        }
    }


    private void fetchWallet() {

        new GetWallet(mContext, new Callback() {
            @Override
            public void onTaskexecuted(String response, String error) {
                if (TextUtils.isEmpty(response)) {
                    mListener.onFailToReceiveSavedOptions(error);
                    mListener.onInitCompleted();
                } else {
                    ArrayList<PaymentOption> walletList = new ArrayList<>();
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

                    mListener.onReceiveSavedOptions(walletList);
                }

                mListener.onInitCompleted();
            }
        }).execute();
    }
}
