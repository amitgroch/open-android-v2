/*
 *
 *    Copyright 2015 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sdk;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusResponse;

import java.util.List;

import retrofit.Callback;

/**
 * Created by salil on 11/5/15.
 */
public class CitrusClient {

    public static enum Environment {
        SANDBOX {
            public String getBaseUrl() {
                return "https://sandboxadmin.citruspay.com";
            }
        }, PRODUCTION {
            public String getBaseUrl() {
                return "https://admin.citruspay.com";
            }
        };

        public abstract String getBaseUrl();
    }

    private String signinId;
    private String signinSecret;
    private String signupId;
    private String signupSecret;
    private String vanity;

    private String merchantName;
    private Environment environment = Environment.SANDBOX;
    private Amount balanceAmount;
    private static CitrusClient instance = null;

    private CitrusClient() {
    }

    public static CitrusClient getInstance() {
        if (instance == null) {
            synchronized (CitrusClient.class) {
                if (instance == null) {
                    instance = new CitrusClient();
                }
            }
        }

        return instance;
    }

    // Public APIS start

    /**
     * This api will check whether the user is existing user or not. If the user is existing user,
     * then it will return the existing details, else it will create an account internally and
     * then call setPassword to set the password and activate the account.
     *
     * @param emailId
     * @param mobileNo
     * @param callback
     */
    public synchronized void linkUser(String emailId, String mobileNo, Callback<CitrusUser> callback) {
        // TODO: Implemenation is remaining, need to change the response type as well.
    }

    /**
     * @param emailId
     * @param mobileNo
     * @param password
     * @param callback
     */
    public synchronized void signIn(String emailId, String mobileNo, String password, Callback<CitrusResponse> callback) {

    }

    /**
     * Signout the existing logged in user.
     */
    public synchronized void signOut(Callback<CitrusResponse> callback) {

    }

    /**
     * Set the user password.
     *
     * @param emailId
     * @param mobileNo
     * @param password
     * @param callback
     */
    public synchronized void setPassword(String emailId, String mobileNo, String password, Callback<CitrusResponse> callback) {

    }

    /**
     * Reset the user password. The password reset link will be sent to the user.
     *
     * @param emailId
     * @param mobileNo
     * @param callback
     */
    public synchronized void resetPassword(String emailId, String mobileNo, Callback<CitrusResponse> callback) {

    }

    /**
     * Get the user saved payment options.
     *
     * @param callback
     */
    public synchronized void getWallet(Callback<List<PaymentOption>> callback) {
        /*
         * Get the saved payment options of the user.
         */

    }

    /**
     * Get the balance of the user.
     *
     * @param callback
     */
    public synchronized void getBalance(Callback<Amount> callback) {

    }

    /**
     * Save the paymentOption.
     *
     * @param paymentOption - PaymentOption to be saved.
     * @param callback
     */
    public synchronized void savePaymentOption(PaymentOption paymentOption, Callback<CitrusResponse> callback) {

    }

    /**
     * Get the payment bill for the transaction.
     *
     * @param amount   - Transaction amount
     * @param callback
     */
    public synchronized void getBill(Amount amount, Callback<PaymentBill> callback) {

    }

    /**
     * Send money to your friend.
     *
     * @param toUser   - User to whom to send the money.
     * @param callback
     */
    public synchronized void sendMoney(CitrusUser toUser, Callback<CitrusResponse> callback) {

    }

//    public synchronized void getPrepaidToken()


    // Public APIS end


    // Getters and setters
    public String getSigninId() {
        return signinId;
    }

    public void setSigninId(String signinId) {
        this.signinId = signinId;
    }

    public String getSigninSecret() {
        return signinSecret;
    }

    public void setSigninSecret(String signinSecret) {
        this.signinSecret = signinSecret;
    }

    public String getSignupId() {
        return signupId;
    }

    public void setSignupId(String signupId) {
        this.signupId = signupId;
    }

    public String getSignupSecret() {
        return signupSecret;
    }

    public void setSignupSecret(String signupSecret) {
        this.signupSecret = signupSecret;
    }

    public String getVanity() {
        return vanity;
    }

    public void setVanity(String vanity) {
        this.vanity = vanity;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Amount getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Amount balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
}