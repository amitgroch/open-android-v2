/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.citrus.asynch.GetJSONBill;
import com.citrus.cash.PersistentConfig;
import com.citrus.citrususer.RandomPassword;
import com.citrus.mobile.Config;
import com.citrus.mobile.OAuth2GrantType;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.User;
import com.citrus.retrofit.API;
import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BindPOJO;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import eventbus.CookieEvents;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.citrus.sdk.response.CitrusResponse.Status;

/**
 * Created by salil on 11/5/15.
 */
public class CitrusClient {

    public enum Environment {
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

    public static final String SIGNIN_TOKEN = "signin_token";
    public static final String SIGNUP_TOKEN = "signup_token";
    public static final String PREPAID_TOKEN = "prepaid_token";

    private String signinId;
    private String signinSecret;
    private String signupId;
    private String signupSecret;
    private String vanity;

    private String merchantName;
    private Environment environment = Environment.SANDBOX;
    private Amount balanceAmount;
    private static CitrusClient instance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private MerchantPaymentOption merchantPaymentOption = null;

    private API retrofitClient;
    private String prepaidCookie = null;
    private OauthToken oauthToken = null;
    private CookieManager cookieManager;

    private CitrusClient(Context context) {
        mContext = context;

        initRetrofitClient();
        oauthToken = new OauthToken(context);
    }

    public void init(@NonNull String signupId, @NonNull String signupSecret, @NonNull String signinId, @NonNull String signinSecret, @NonNull String vanity, @NonNull Environment environment) {
        this.signupId = signupId;
        this.signupSecret = signupSecret;
        this.signinId = signinId;
        this.signinSecret = signinSecret;
        this.vanity = vanity;


        if (environment == null) {
            this.environment = Environment.SANDBOX;
        }
        this.environment = environment;

        if (validate()) {
            initRetrofitClient();
        }

        // TODO: Remove full dependency on this class.
        Config.setupSignupId(signupId);
        Config.setupSignupSecret(signupSecret);

        Config.setSigninId(signinId);
        Config.setSigninSecret(signinSecret);

        switch (environment) {
            case SANDBOX:
                Config.setEnv("sandbox");
                break;
            case PRODUCTION:
                Config.setEnv("production");
                break;
        }
    }

    private void initRetrofitClient() {
        RetroFitClient.initRetroFitClient(environment);
        retrofitClient = RetroFitClient.getCitrusRetroFitClient();
    }

    public static CitrusClient getInstance(Context context) {
        if (instance == null) {
            synchronized (CitrusClient.class) {
                if (instance == null) {
                    instance = new CitrusClient(context);
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
     * @param emailId  - emailId of the user
     * @param mobileNo - mobileNo of the user
     * @param callback - callback
     */
    public synchronized void linkUser(final String emailId, final String mobileNo, final Callback<Boolean> callback) {
        if (validate()) {

            retrofitClient.getSignUpToken(signupId, signupSecret, OAuth2GrantType.implicit.toString(), new retrofit.Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken, Response response) {
                    Logger.d("accessToken " + accessToken.getJSON().toString());

                    if (accessToken.getAccessToken() != null) {
                        OauthToken signuptoken = new OauthToken(mContext, SIGNUP_TOKEN);
                        signuptoken.createToken(accessToken.getJSON()); //Oauth Token received

                        retrofitClient.getBindResponse(accessToken.getHeaderAccessToken(), emailId, mobileNo, new retrofit.Callback<BindPOJO>() {
                            @Override
                            public void success(BindPOJO bindPOJO, Response response) {
                                Logger.d("BIND RESPONSE " + bindPOJO.getUsername());

                                retrofitClient.getSignInToken(signinId, signinSecret, emailId, OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {
                                    @Override
                                    public void success(AccessToken accessToken, Response response) {
                                        Logger.d("SIGNIN accessToken" + accessToken.getJSON().toString());
                                        if (accessToken.getAccessToken() != null) {
                                            OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                                            token.createToken(accessToken.getJSON());

                                            RandomPassword pwd = new RandomPassword();

                                            String random_pass = pwd.generate(emailId, mobileNo);

                                            retrofitClient.getSignInWithPasswordResponse(signinId, signinSecret, emailId, random_pass, OAuth2GrantType.password.toString(), new retrofit.Callback<AccessToken>() {
                                                @Override
                                                public void success(AccessToken accessToken, Response response) {
                                                    Logger.d("User Not A Citrus Member. Please Sign Up User.");
                                                    sendResponse(callback, false);
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Logger.d("User Already A Citrus Member. Please Sign In User.");
                                                    sendResponse(callback, true);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_LINK_USER, Status.FAILED));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * @param emailId
     * @param password
     * @param callback
     */
    public synchronized void signIn(final String emailId, final String password, final Callback<CitrusResponse> callback) {

        //grant Type username token saved
        retrofitClient.getSignInToken(signinId, signinSecret, emailId, OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                if (accessToken.getAccessToken() != null) {
                    OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                    token.createToken(accessToken.getJSON());///grant Type username token saved

                    retrofitClient.getSignInWithPasswordResponse(signinId, signinSecret, emailId, password, OAuth2GrantType.password.toString(), new retrofit.Callback<AccessToken>() {
                        @Override
                        public void success(AccessToken accessToken, Response response) {
                            Logger.d("SIGN IN RESPONSE " + accessToken.getJSON().toString());
                            if (accessToken.getAccessToken() != null) {
                                OauthToken token = new OauthToken(mContext, PREPAID_TOKEN);
                                token.createToken(accessToken.getJSON());///grant Type password token saved
                                RetroFitClient.setInterCeptor();
                                EventBus.getDefault().register(CitrusClient.this);
                                retrofitClient.getCookie(emailId, password, "true", new retrofit.Callback<String>() {
                                    @Override
                                    public void success(String s, Response response) {
                                        // NOOP
                                        // This method will never be called.
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (prepaidCookie != null) {
                                            cookieManager = CookieManager.getInstance();
                                            PersistentConfig config = new PersistentConfig(mContext);
                                            if (config.getCookieString() != null) {
                                                cookieManager.getInstance().removeSessionCookie();
                                            }
                                            config.setCookie(prepaidCookie);
                                        } else {
                                            Logger.d("PREPAID LOGIN UNSUCCESSFUL");
                                        }
                                        EventBus.getDefault().unregister(CitrusClient.this);

                                        // Since we have a got the cookie, we are giving the callback.
                                        sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SIGNIN, Status.SUCCESSFUL));
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("SIGN IN RESPONSE ERROR **" + error.getMessage());
                            sendError(callback, error);
                        }
                    });

                }
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(callback, error);
            }
        });
    }

    /**
     * Signout the existing logged in user.
     */
    public synchronized void signOut(Callback<CitrusResponse> callback) {
        if (validate()) {
            if (User.logoutUser(mContext)) {
                CitrusResponse citrusResponse = new CitrusResponse("User Logged Out Successfully.", Status.SUCCESSFUL);
                sendResponse(callback, citrusResponse);
            } else {
                CitrusError citrusError = new CitrusError("Failed to logout.", Status.FAILED);
                callback.error(citrusError);
            }
        }
    }

    /**
     * Set the user password.
     *
     * @param emailId
     * @param mobileNo
     * @param password
     * @param callback
     */
    public synchronized void setPassword(String emailId, String mobileNo, String password, final Callback<CitrusResponse> callback) {

        if (validate()) {
            OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
            JSONObject jsontoken = token.getuserToken();
            try {
                String header = "Bearer " + jsontoken.getString("access_token");
                RandomPassword pwd = new RandomPassword();
                String random_pass = pwd.generate(emailId, mobileNo);
                retrofitClient.setPasswordResponse(header, random_pass, password, new retrofit.Callback<ResponseCallback>() {
                    @Override
                    public void success(ResponseCallback responseCallback, Response response) {
                        Logger.d("SET PASSWORD RESPONSE **" + String.valueOf(response.getStatus()));
                        sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SET_PASSWORD, Status.SUCCESSFUL));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.d("SET PASSWORD ERROR **" + error.getMessage());
                        sendError(callback, error);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset the user password. The password reset link will be sent to the user.
     *
     * @param emailId
     * @param callback
     */
    public synchronized void resetPassword(final String emailId, @NonNull final Callback<CitrusResponse> callback) {

        oauthToken.getSignUpToken(new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                if (accessToken != null) {

                    retrofitClient.resetPassword(accessToken.getHeaderAccessToken(), emailId, new retrofit.Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement element, Response response) {
                            sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_RESET_PASSWORD, Status.SUCCESSFUL));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, error);
                        }
                    });
                } else {
                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_RESET_PASSWORD, Status.FAILED));
                }
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });
    }

    /**
     * Get the user saved payment options.
     *
     * @param callback - callback
     */
    public synchronized void getWallet(final Callback<List<PaymentOption>> callback) {
        /*
         * Get the saved payment options of the user.
         */
        if (validate()) {

            oauthToken.getSignInToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {

                    retrofitClient.getWallet(accessToken.getHeaderAccessToken(), new retrofit.Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement element, Response response) {
                            if (element != null) {
                                ArrayList<PaymentOption> walletList = new ArrayList<>();
                                try {

                                    JSONObject jsonObject = new JSONObject(element.toString());
                                    JSONArray paymentOptions = jsonObject.optJSONArray("paymentOptions");

                                    if (paymentOptions != null) {
                                        for (int i = 0; i < paymentOptions.length(); i++) {
                                            PaymentOption option = PaymentOption.fromJSONObject(paymentOptions.getJSONObject(i));

                                            // Check whether the merchant supports the user's payment option and then only add this payment option.
                                            if (merchantPaymentOption != null) {
                                                Set<CardOption.CardScheme> creditCardSchemeSet = merchantPaymentOption.getCreditCardSchemeSet();
                                                Set<CardOption.CardScheme> debitCardSchemeSet = merchantPaymentOption.getDebitCardSchemeSet();
                                                List<NetbankingOption> netbankingOptionList = merchantPaymentOption.getNetbankingOptionList();

                                                if (option instanceof CreditCardOption && creditCardSchemeSet != null &&
                                                        creditCardSchemeSet.contains(((CreditCardOption) option).getCardScheme())) {
                                                    walletList.add(option);
                                                } else if (option instanceof DebitCardOption && debitCardSchemeSet != null &&
                                                        debitCardSchemeSet.contains(((DebitCardOption) option).getCardScheme())) {
                                                    walletList.add(option);
                                                } else if (option instanceof NetbankingOption && netbankingOptionList != null &&
                                                        netbankingOptionList.contains(option)) {
                                                    walletList.add(option);
                                                }
                                            } else {
                                                // If the merchant payment options are not found, save all the options.
                                                walletList.add(option);
                                            }
                                        }
                                    }

                                    sendResponse(callback, walletList);

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                                }
                            } else {
                                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, new CitrusError(error.getMessage(), Status.FAILED));
                        }
                    });
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Get the balance of the user.
     *
     * @param callback
     */
    public synchronized void getBalance(final Callback<Amount> callback) {
        if (validate()) {
            oauthToken.getSignInToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {

                    retrofitClient.getBalance(accessToken.getHeaderAccessToken(), new retrofit.Callback<Amount>() {
                        @Override
                        public void success(Amount amount, Response response) {
                            sendResponse(callback, amount);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, error);
                        }
                    });
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Save the paymentOption.
     *
     * @param paymentOption - PaymentOption to be saved.
     * @param callback
     */

    public synchronized void savePaymentOption(final PaymentOption paymentOption, final Callback<CitrusResponse> callback) {
        if (validate()) {

            if (paymentOption != null) {
                oauthToken.getSignInToken(new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken accessToken) {
                        retrofitClient.savePaymentOption(accessToken.getAccessToken(), new TypedString(paymentOption.getSavePaymentOptionObject()), new retrofit.Callback<CitrusResponse>() {
                            @Override
                            public void success(CitrusResponse citrusResponse, Response response) {
                                sendResponse(callback, citrusResponse);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    }

                    @Override
                    public void error(CitrusError error) {
                        sendError(callback, error);
                    }
                });
            } else {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_NULL_PAYMENT_OPTION, Status.FAILED));
            }
        }
    }

    /**
     * Get the payment bill for the transaction.
     *
     * @param amount   - Transaction amount
     * @param callback
     */
    public synchronized void getBill(String billUrl, Amount amount, final Callback<PaymentBill> callback) {
        // Get the bill from the merchant server.

        new GetJSONBill(billUrl, amount, new retrofit.Callback<PaymentBill>() {
            @Override
            public void success(PaymentBill paymentBill, Response response) {
                Logger.d("GETBILL RESPONSE **" + paymentBill.toString());
                callback.success(paymentBill);
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(callback, error);

            }
        }).getJSONBill();

    }


    /**
     * Send money to your friend.
     *
     * @param amount   - Amount to be sent
     * @param toUser   - The user detalis. Enter emailId if send by email or mobileNo if send by mobile.
     * @param message  - Optional message
     * @param callback - Callback
     */
    public synchronized void sendMoney(final Amount amount, final CitrusUser toUser, final String message, final Callback<PaymentResponse> callback) {
        if (validate()) {

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_AMOUNT, Status.FAILED));
                return;
            }

            if (toUser == null || (TextUtils.isEmpty(toUser.getEmailId()) && TextUtils.isEmpty(toUser.getMobileNo()))) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_EMAIL_ID_MOBILE_NO, Status.FAILED));
                return;
            }

            final retrofit.Callback<PaymentResponse> callbackSendMoney = new retrofit.Callback<PaymentResponse>() {
                @Override
                public void success(PaymentResponse paymentResponse, Response response) {
                    sendResponse(callback, paymentResponse);
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            };

            oauthToken.getSignInToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {
                    if (!TextUtils.isEmpty(toUser.getEmailId())) {
                        retrofitClient.sendMoneyByEmail(accessToken.getAccessToken(), amount.getValue(), amount.getCurrency(), message, toUser.getEmailId(), callbackSendMoney);
                    } else {
                        long mobileNo = com.citrus.card.TextUtils.isValidMobileNumber(toUser.getMobileNo());
                        if (mobileNo != -1) {
                            retrofitClient.sendMoneyByMobile(accessToken.getAccessToken(), amount.getValue(), amount.getCurrency(), message, String.valueOf(mobileNo), callbackSendMoney);
                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_MOBILE_NO, Status.FAILED));
                        }
                    }
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Returns the access token of the currently logged in user.
     *
     * @return
     */
    public void getPrepaidToken(final Callback<AccessToken> callback) {

        oauthToken.getPrepaidToken(new Callback<com.citrus.sdk.classes.AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                sendResponse(callback, accessToken);
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });
    }


    /**
     * Get the merchant available payment options. You need to show the user available payment option in your app.
     *
     * @param callback
     */
    public synchronized void getMerchantPaymentOptions(final Callback<MerchantPaymentOption> callback) {
        if (validate()) {

            retrofitClient.getMerchantPaymentOptions(vanity, new retrofit.Callback<JsonElement>() {
                @Override
                public void success(JsonElement element, Response response) {

                    MerchantPaymentOption merchantPaymentOption = null;

                    if (element.isJsonObject()) {
                        JsonObject paymentOptionObj = element.getAsJsonObject();
                        if (paymentOptionObj != null) {
                            merchantPaymentOption = MerchantPaymentOption.getMerchantPaymentOptions(paymentOptionObj);

                            saveMerchantPaymentOptions(merchantPaymentOption);

                            sendResponse(callback, merchantPaymentOption);

                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_FAILED_MERCHANT_PAYMENT_OPTIONS, Status.FAILED));
                        }
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }
    }


    // Public APIS end

    private synchronized boolean validate() {
        if (!TextUtils.isEmpty(signinId) && !TextUtils.isEmpty(signinSecret)
                && !TextUtils.isEmpty(signupId) && !TextUtils.isEmpty(signupSecret)
                && !TextUtils.isEmpty(vanity)) {
            return true;
        } else {
            throw new IllegalArgumentException(ResponseMessages.ERROR_MESSAGE_BLANK_CONFIG_PARAMS);
        }
    }

    private <T> void sendResponse(Callback callback, T t) {
        if (callback != null) {
            callback.success(t);
        }
    }

    private void sendError(Callback callback, CitrusError error) {
        if (callback != null) {
            callback.error(error);
        }
    }

    private void sendError(Callback callback, RetrofitError error) {
        if (callback != null) {
            callback.error(new CitrusError(error.getMessage(), Status.FAILED));
        }
    }

    private void saveMerchantPaymentOptions(MerchantPaymentOption merchantPaymentOption) {
        this.merchantPaymentOption = merchantPaymentOption;

        // TODO Save these values in DB
    }

    private void showToast(String message) {
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Getters and setters.
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

    public MerchantPaymentOption getMerchantPaymentOption() {
        return merchantPaymentOption;
    }

    public void setMerchantPaymentOption(MerchantPaymentOption merchantPaymentOption) {
        this.merchantPaymentOption = merchantPaymentOption;
    }

    //this event is triggered from ReceivedCookiesInterceptor
    public void onEvent(CookieEvents cookieEvents) {
        // Logger.d("COOKIE IN CITRUS CLIENT  ****" + cookieEvents.getCookie());
        prepaidCookie = cookieEvents.getCookie();
    }
}
