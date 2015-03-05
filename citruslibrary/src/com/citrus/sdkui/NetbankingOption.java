package com.citrus.sdkui;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by salil on 13/2/15.
 */
public class NetbankingOption extends PaymentOption {

    private String bankName = null;
    private String bankCID = null;

    public NetbankingOption(String bankName, String bankCID) {
        this.bankName = bankName;
        this.bankCID = bankCID;
    }

    /**
     * @param name     - Human readable names for banks. e.g. Net Banking - AXIS BANK
     * @param token    - Token for netbanking payment.
     * @param bankName - Bank's name - ICICI, AXIS.
     */
    public NetbankingOption(String name, String token, String bankName) {
        super(name, token);
        this.bankName = bankName;
    }


    public String getBankName() {
        return bankName;
    }

    public String getBankCID() {
        return bankCID;
    }

    @Override
    public Drawable getOptionIcon(Context context) {
        // Return the icon depending upon the scheme of the card.
        Drawable drawable = null;

        int resourceId = 0;
        if ("AXIS Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("axis_bank", "drawable", context.getPackageName());
        } else if ("Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_india", "drawable", context.getPackageName());
        } else if ("Bank Of Baroda".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_baroda", "drawable", context.getPackageName());
        } else if ("Bank of Maharashtra".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_maharashtra", "drawable", context.getPackageName());
        } else if ("Central Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("central_bank_of_india", "drawable", context.getPackageName());
        } else if ("CITI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("citi_bank", "drawable", context.getPackageName());
        } else if ("Corporation Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("corporation_bank", "drawable", context.getPackageName());
        } else if ("City Union Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("city_union_bank", "drawable", context.getPackageName());
        } else if ("Canara Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("canara_bank", "drawable", context.getPackageName());
        } else if ("DEUTSCHE Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("deutsche_bank", "drawable", context.getPackageName());
        } else if ("Federal Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("federal_bank", "drawable", context.getPackageName());
        } else if ("HDFC Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("hdfc_bank", "drawable", context.getPackageName());
        } else if ("ICICI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("icici_bank", "drawable", context.getPackageName());
        } else if ("IDBI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("idbi_bank", "drawable", context.getPackageName());
        } else if ("Indian Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("indian_bank", "drawable", context.getPackageName());
        } else if ("Indian Overseas Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("indian_overseas_bank", "drawable", context.getPackageName());
        } else if ("Induslnd Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("induslnd_bank", "drawable", context.getPackageName());
        } else if ("ING VYSA".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("ing_vysa", "drawable", context.getPackageName());
        } else if ("Kotak Mahindra Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("kotak_mahindra_bank", "drawable", context.getPackageName());
        } else if ("Karnataka Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("karnataka_bank", "drawable", context.getPackageName());
        } else if ("PNB Retail".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("pnb_retail", "drawable", context.getPackageName());
        } else if ("PNB Corporate".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("pnb_corporate", "drawable", context.getPackageName());
        } else if ("SBI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("sbi_bank", "drawable", context.getPackageName());
        } else if ("State Bank of Bikaner and Jaipur".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_bikaner_and_jaipur", "drawable", context.getPackageName());
        } else if ("State Bank of Hyderabad".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_hyderabad", "drawable", context.getPackageName());
        } else if ("State Bank of Mysore".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_mysore", "drawable", context.getPackageName());
        } else if ("State Bank of Travancore".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_travancore", "drawable", context.getPackageName());
        } else if ("State Bank of Patiala".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_patiala", "drawable", context.getPackageName());
        } else if ("Union Bank Of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("union_bank_of_india", "drawable", context.getPackageName());
        } else if ("United Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("united_bank_of_india", "drawable", context.getPackageName());
        } else if ("Vijaya Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("vijaya_bank", "drawable", context.getPackageName());
        } else if ("YES Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("yes_bank", "drawable", context.getPackageName());
        }

        if (resourceId == 0) {
            if ((resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName())) != 0) {
                drawable = context.getResources().getDrawable(resourceId);
            }
        } else {
            drawable = context.getResources().getDrawable(resourceId);
        }

        return drawable;
    }

    @Override
    public String toString() {
        return bankName;
    }
}