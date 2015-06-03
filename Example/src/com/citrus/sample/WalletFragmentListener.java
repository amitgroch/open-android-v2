package com.citrus.sample;

import com.citrus.sdk.TransactionResponse;

/**
 * Created by salil on 3/6/15.
 */
public interface WalletFragmentListener {
    void onPaymentComplete(TransactionResponse transactionResponse);
}
