/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.mobile;

import android.content.Context;

import com.citrus.asynch.InitSDK;
import com.citrus.interfaces.InitListener;
import com.citrus.sdkui.NetbankingOption;
import com.citrus.sdkui.PaymentOption;

import java.util.ArrayList;
import java.util.List;

public class Config {
    //private static Context context;

    static ArrayList<PaymentOption> citrusWallet = null;
    private static String env, signinId, signinSecret, signupId, signupSecret;
    private static String vanity;
    private static String emailID;
    private static String mobileNo;
    private static ArrayList<NetbankingOption> bankList;
    private static ArrayList<NetbankingOption> topBankList;

    public static void setupSignupId(String id) {
        signupId = id;
    }

    public static void setupSignupSecret(String secret) {
        signupSecret = secret;
    }

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String sip) {
        env = sip;
    }

    public static String getSigninId() {
        return signinId;
    }

    public static void setSigninId(String id) {
        signinId = id;
    }

    public static String getSigninSecret() {
        return signinSecret;
    }

    public static void setSigninSecret(String secret) {
        signinSecret = secret;
    }

    public static String getSignupId() {
        return signupId;
    }

    public static String getSignupSecret() {
        return signupSecret;
    }


    /*
     * These methods are added for single screen ui.
     */
    public static ArrayList<NetbankingOption> getBankList() {
        return bankList;
    }

    public static void setBankList(ArrayList<NetbankingOption> bankList) {
        Config.bankList = bankList;
    }

    public static ArrayList<NetbankingOption> getTopBankList() {
        return topBankList;
    }

    public static void setTopBankList(ArrayList<NetbankingOption> topBankList) {
        Config.topBankList = topBankList;
    }

    public static String getVanity() {
        return vanity;
    }

    public static void setVanity(String vanity) {
        Config.vanity = vanity;
    }

    public static String getEmailID() {
        return emailID;
    }

    public static void setEmailID(String emailID) {
        Config.emailID = emailID;
    }

    public static String getMobileNo() {
        return mobileNo;
    }

    public static void setMobileNo(String mobileNo) {
        Config.mobileNo = mobileNo;
    }

    public static ArrayList<PaymentOption> getCitrusWallet() {
        return citrusWallet;
    }

    public static void setCitrusWallet(ArrayList<PaymentOption> citrusWallet) {
        Config.citrusWallet = citrusWallet;
    }

    public static void initUser(Context context, InitListener initListener) {
        new InitSDK(context, initListener, null, null);
    }

    public static void setEnvironment(Environment environment) {
        setEnv(environment.getEnvironment());
    }

    public enum Environment {
        PRODUCTION {
            String getEnvironment() {
                return "production";
            }
        }, SANDBOX {
            String getEnvironment() {
                return "production";
            }
        };

        abstract String getEnvironment();
    }

    public static final String INTENT_EXTRA_USER_EMAIL = "INTENT_EXTRA_USER_EMAIL";
    public static final String INTENT_EXTRA_USER_MOBILE = "INTENT_EXTRA_USER_MOBILE";
    public static final String INTENT_EXTRA_MERCHANT_VANITY = "INTENT_EXTRA_MERCHANT_VANITY";
    public static final String INTENT_EXTRA_MERCHANT_BILL_URL = "INTENT_EXTRA_MERCHANT_BILL_URL";
    public static final String INTENT_EXTRA_TRANSACTION_AMOUNT = "INTENT_EXTRA_TRANSACTION_AMOUNT";
    public static final String INTENT_EXTRA_MERCHANT_NAME = "INTENT_EXTRA_MERCHANT_NAME";

}