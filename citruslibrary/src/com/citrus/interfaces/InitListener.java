package com.citrus.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by MANGESH KADAM on 2/17/2015.
 */
public abstract interface InitListener {
    public abstract void onSuccess(String response);

    public abstract void onNetbankingSuccess();

    public abstract void onBindFailed(String response);

    public abstract void onWalletLoadFailed(String response);

    public abstract void onNetBankingListFailed(VolleyError error);

    public abstract void onError(Exception e);
}
