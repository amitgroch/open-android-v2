package com.citruspay.sdkui;

import android.os.Parcel;
import android.os.Parcelable;

import com.citrus.sdkui.NetbankingOption;
import com.citrus.sdkui.PaymentOption;

import java.util.ArrayList;

/**
 * Created by salil on 9/3/15.
 */
public final class CitrusPaymentParams implements Parcelable {

    /**
     * User details. Email id and mobile no. will be used to fetch saved payment options for that user.
     */
    public CitrusUser user;
    /**
     * Bill url. This page will be hosted on your server. Host pages depending upon your backend technology.
     * It is required to authenticate the transaction. The merchant needs to sign the particular transaction.
     */
    public String billUrl;
    /**
     * Accent color for the app, in the form of #123456.
     * This is the color of the status bar when the is opened.
     * This will be used only on android versions lollipop and above.
     */
    public String colorPrimaryDark;
    /**
     * Main color code for the app in the form #123456
     */
    public String colorPrimary;
    /**
     * Primary text color. #123456
     */
    public String textColorPrimary = "#ffffff";
    /**
     * Accent color for the app, will be used to display common actions.
     */
    public String accentColor;
    /**
     * Transaction amount.
     */
    public double transactionAmount;
    /**
     * Your vanity to fetch the available payment options.
     */
    public String vanity;
    /**
     * Name of the merchant. For the display purpose.
     */
    public String merchantName;
    // Following are the parameters used internally.
    ArrayList<NetbankingOption> netbankingOptionList = new ArrayList<>(); // Netbanking options enabled for the merchant.
    ArrayList<NetbankingOption> topNetbankingOptions = new ArrayList<>(); // List of top n banks. This list will contain 0 to 4 items depending upon the enabled netbanking options for the merchant.
    ArrayList<PaymentOption> userSavedOptionList =new ArrayList<>(); // List of different payment options saved for the user.


    public CitrusPaymentParams() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, 0);
        dest.writeString(this.billUrl);
        dest.writeString(this.colorPrimaryDark);
        dest.writeString(this.colorPrimary);
        dest.writeString(this.textColorPrimary);
        dest.writeString(this.accentColor);
        dest.writeDouble(this.transactionAmount);
        dest.writeString(this.vanity);
        dest.writeString(this.merchantName);
        dest.writeTypedList(this.netbankingOptionList);
        dest.writeTypedList(this.topNetbankingOptions);
        dest.writeTypedList(this.userSavedOptionList);
    }

    private CitrusPaymentParams(Parcel in) {
        this.user = in.readParcelable(CitrusUser.class.getClassLoader());
        this.billUrl = in.readString();
        this.colorPrimaryDark = in.readString();
        this.colorPrimary = in.readString();
        this.textColorPrimary = in.readString();
        this.accentColor = in.readString();
        this.transactionAmount = in.readDouble();
        this.vanity = in.readString();
        this.merchantName = in.readString();

        in.readTypedList(this.netbankingOptionList, NetbankingOption.CREATOR);
        in.readTypedList(this.topNetbankingOptions, NetbankingOption.CREATOR);
        in.readTypedList(this.userSavedOptionList, PaymentOption.CREATOR);
    }

    public static final Creator<CitrusPaymentParams> CREATOR = new Creator<CitrusPaymentParams>() {
        public CitrusPaymentParams createFromParcel(Parcel source) {
            return new CitrusPaymentParams(source);
        }

        public CitrusPaymentParams[] newArray(int size) {
            return new CitrusPaymentParams[size];
        }
    };
}
