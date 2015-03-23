package com.citruspay.sdkui;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by salil on 16/2/15.
 */
public final class Utils {
    public static final String INTENT_EXTRA_PAYMENT_URL = "INTENT_EXTRA_PAYMENT_URL";

    public static final String INTENT_EXTRA_PAYMENT_RESPONSE = "INTENT_EXTRA_PAYMENT_RESPONSE";
    public static final int REQUEST_CODE_PAYMENT_ACTIVITY = 123;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
