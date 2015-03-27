package com.citrus.sdkui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.mobile.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by salil on 9/3/15.
 */
public final class CitrusPaymentParams implements Parcelable {

    public static final Creator<CitrusPaymentParams> CREATOR = new Creator<CitrusPaymentParams>() {
        public CitrusPaymentParams createFromParcel(Parcel source) {
            return new CitrusPaymentParams(source);
        }

        public CitrusPaymentParams[] newArray(int size) {
            return new CitrusPaymentParams[size];
        }
    };
    ;
    // Following are the parameters used internally.
    ArrayList<NetbankingOption> netbankingOptionList = new ArrayList<>(); // Netbanking options enabled for the merchant.
    ArrayList<NetbankingOption> topNetbankingOptions = new ArrayList<>(); // List of top n banks. This list will contain 0 to 4 items depending upon the enabled netbanking options for the merchant.
    ArrayList<PaymentOption> userSavedOptionList = new ArrayList<>(); // List of different payment options saved for the user.
    /**
     * User details. Email id and mobile no. will be used to fetch saved payment options for that user.
     */
    CitrusUser user;
    /**
     * Bill url. This page will be hosted on your server. Host pages depending upon your backend technology.
     * It is required to authenticate the transaction. The merchant needs to sign the particular transaction.
     */
    String billUrl;
    /**
     * Accent color for the app, in the form of #123456.
     * This is the color of the status bar when the is opened.
     * This will be used only on android versions lollipop and above.
     */
    String colorPrimaryDark = "#E7961D";
    /**
     * Main color code for the app in the form #123456
     */
    String colorPrimary = "#F9A323";
    /**
     * Primary text color. #123456
     */
    String textColorPrimary = "#ffffff";
    /**
     * Accent color for the app, will be used to display common actions.
     */
    String accentColor;
    /**
     * Transaction amount.
     */
    double transactionAmount;
    /**
     * Name of the merchant. For the display purpose.
     */
    String merchantName;
    /**
     * JSON Key store containing the merchant credentials. The json is in the following format.
     * <p/>
     * {"access_key":"06SLEEBYLVZELISZ5ECU",
     * "signup-id":"kkizp9tsqg-signup",
     * "signup-secret":"39c50a32eaabaf382223fdd05f331e1c",
     * "signin-id":"kkizp9tsqg-signin",
     * "signin-secret":"1fc1f57639ec87cf4d49920f6b3a2c9d",
     * "vanity_Url":"https://www.citruspay.com/kkizp9tsqg"
     * }
     */
    String jsonKeyStore;
    /**
     * Merchant vanity to fetch the available payment options.
     */
    String vanity;
    /**
     * Merchant accessKey found on merchant portal.
     */
    String accessKey;
    /**
     * Merchant keys required for user signin and signup.
     */
    String signinId;
    String signinSecret;
    String signupId;
    String signupSecret;

    Environment environment = Environment.SANDBOX;

    CitrusPaymentParams(double transactionAmount, String billUrl, String jsonKeyStore) {
        this.transactionAmount = transactionAmount;
        this.billUrl = billUrl;
        this.jsonKeyStore = jsonKeyStore;

        // Parse the jsonKeystore.
        try {
            JSONObject keyStore = new JSONObject(jsonKeyStore);
            this.accessKey = keyStore.getString("access_key");
            this.signupId = keyStore.getString("signup-id");
            this.signupSecret = keyStore.getString("signup-secret");
            this.signinId = keyStore.getString("signin-id");
            this.signinSecret = keyStore.getString("signin-secret");

            String vanity = keyStore.getString("vanity_Url");
            // If vanity is in the form of https://www.citruspay.com/vanity
            // take only vanity part else as it is.
            if (vanity.startsWith("http") || vanity.startsWith("https")) {
                vanity = vanity.substring(vanity.lastIndexOf("/") + 1);
            }
            this.vanity = vanity;

            // Set merchant keys.
            Config.setupSignupId(signupId);
            Config.setupSignupSecret(signupSecret);
            Config.setSigninId(signinId);
            Config.setSigninSecret(signinSecret);

        } catch (JSONException ex) {
            throw new IllegalArgumentException("The json keystore is not a valid json.");
        }
    }

    private CitrusPaymentParams(Parcel in) {
        this.netbankingOptionList = (ArrayList<NetbankingOption>) in.readSerializable();
        this.topNetbankingOptions = (ArrayList<NetbankingOption>) in.readSerializable();
        this.userSavedOptionList = (ArrayList<PaymentOption>) in.readSerializable();
        this.user = in.readParcelable(CitrusUser.class.getClassLoader());
        this.billUrl = in.readString();
        this.colorPrimaryDark = in.readString();
        this.colorPrimary = in.readString();
        this.textColorPrimary = in.readString();
        this.accentColor = in.readString();
        this.transactionAmount = in.readDouble();
        this.vanity = in.readString();
        this.merchantName = in.readString();
        this.jsonKeyStore = in.readString();
    }

    public static CitrusPaymentParams builder(double transactionAmount, String billUrl, String jsonKeyStore) {
        if (transactionAmount <= 0 && TextUtils.isEmpty(billUrl) && TextUtils.isEmpty(jsonKeyStore)) {
            throw new IllegalArgumentException("Mandatory parameters missing...");
        }

        return new CitrusPaymentParams(transactionAmount, billUrl, jsonKeyStore);
    }

    public CitrusPaymentParams colorPrimaryDark(String colorPrimaryDark) {
        this.colorPrimaryDark = colorPrimaryDark;
        return this;
    }

    public CitrusPaymentParams colorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
        return this;
    }

    public CitrusPaymentParams textColorPrimary(String textColorPrimary) {
        this.textColorPrimary = textColorPrimary;
        return this;
    }

    public CitrusPaymentParams accentColor(String accentColor) {
        this.accentColor = accentColor;
        return this;
    }

    public CitrusPaymentParams merchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public CitrusPaymentParams user(CitrusUser user) {
        this.user = user;

        if (user != null) {
            Config.setEmailID(user.getEmailId());
            Config.setMobileNo(user.getMobileNo());
        }

        return this;
    }

    public CitrusPaymentParams environment(Environment environment) {
        this.environment = environment;

        if (environment == Environment.PRODUCTION) {
            Config.setEnv("production");
        } else if (environment == Environment.SANDBOX) {
            Config.setEnv("sandbox");
        }

        return this;
    }

    public CitrusUser getUser() {
        return user;
    }

    public String getBillUrl() {
        return billUrl;
    }

    public String getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getTextColorPrimary() {
        return textColorPrimary;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getVanity() {
        return vanity;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSigninId() {
        return signinId;
    }

    public String getSigninSecret() {
        return signinSecret;
    }

    public String getSignupId() {
        return signupId;
    }

    public String getSignupSecret() {
        return signupSecret;
    }

    public String getJsonKeyStore() {
        return jsonKeyStore;
    }

    public CitrusPaymentParams build() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.netbankingOptionList);
        dest.writeSerializable(this.topNetbankingOptions);
        dest.writeSerializable(this.userSavedOptionList);
        dest.writeParcelable(this.user, 0);
        dest.writeString(this.billUrl);
        dest.writeString(this.colorPrimaryDark);
        dest.writeString(this.colorPrimary);
        dest.writeString(this.textColorPrimary);
        dest.writeString(this.accentColor);
        dest.writeDouble(this.transactionAmount);
        dest.writeString(this.vanity);
        dest.writeString(this.merchantName);
        dest.writeString(this.jsonKeyStore);
    }

    public static enum Environment {
        PRODUCTION, SANDBOX
    }
}
