package com.citrus.sdk.ui.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by salil on 4/3/15.
 */
public final class CitrusCash extends PaymentOption {

    private String amount = null;

    public CitrusCash() {
    }

    /**
     * @param name  - User friendly name of the payment option. e.g. Debit Card (4242) or Net Banking - ICICI Bank
     * @param token - Token for payment option, used for tokenized payment.
     */
    public CitrusCash(String name, String token) {
        super(name, token);
    }

    public CitrusCash(String amount) {
        this.amount = amount;
    }

    @Override
    public Drawable getOptionIcon(Context context) {
        return context.getResources().getDrawable(context.getResources().getIdentifier("citrus_cash", "drawable", context.getPackageName()));
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return super.toString() + "CitrusCash{" +
                "amount='" + amount + '\'' +
                '}';
    }
}
