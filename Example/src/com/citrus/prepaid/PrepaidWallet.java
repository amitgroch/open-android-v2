package com.citrus.prepaid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.citrus.asynch.CashOutAsynch;
import com.citrus.asynch.ForgotPass;
import com.citrus.asynch.LinkUser;
import com.citrus.asynch.SendMoneyAsync;
import com.citrus.asynch.SetPassword;
import com.citrus.asynch.SignIn;
import com.citrus.asynch.WalletStatus;
import com.citrus.card.Card;
import com.citrus.cash.LoadMoney;
import com.citrus.cash.Prepaid;
import com.citrus.cash.PrepaidPg;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.Month;
import com.citrus.mobile.User;
import com.citrus.mobile.Year;
import com.citrus.netbank.Bank;
import com.citrus.netbank.BankPaymentType;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citrus.sample.GetBill;
import com.citrus.sample.R;
import com.citrus.sample.WebPage;
import com.citrus.sdk.CitrusActivity;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.Constants;
import com.citrus.sdk.PaymentParams;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BillGeneratorPOJO;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.PaymentType;

import org.json.JSONException;
import org.json.JSONObject;

import static com.citrus.sdk.CitrusClient.Environment;
import static com.citrus.sdk.CitrusClient.getInstance;

public class PrepaidWallet extends Activity {

    private static final String bill_url = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php?amount=3.0";

    Button isSignedin, linkuser, setpass, forgot, signin, getbalance, card_load, card_loadWebView, token_load, bank_load, token_bank_Load, citrus_cashpay, citruscashWebView, get_prepaidToken, withdrawMoney, sendMoneyByEmail, sendMoneyByMobile, getMerchantPaymentOptions, getWallet;

    Button btnlogoutUser;
    Callback callback;

    String prepaid_bill;

    JSONObject customer;


    CitrusClient citrusClient;

    private final  String emailID = "developercitrus@mailinator.com";
    private final String mobileNo = "9769507476";
    private final String password = "Citrus@123";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepaid);

        isSignedin = (Button) this.findViewById(R.id.issignedin);

        linkuser = (Button) this.findViewById(R.id.linkuser);

        setpass = (Button) this.findViewById(R.id.setpassword);

        forgot = (Button) this.findViewById(R.id.forgot);

        signin = (Button) this.findViewById(R.id.signin);

        getbalance = (Button) this.findViewById(R.id.getbalance);

        card_load = (Button) this.findViewById(R.id.cardload);

        card_loadWebView = (Button) this.findViewById(R.id.cardloadWebView);

        token_load = (Button) this.findViewById(R.id.tokenload);

        bank_load = (Button) this.findViewById(R.id.bankload);

        token_bank_Load = (Button) this.findViewById(R.id.tokenbankload);

        citrus_cashpay = (Button) this.findViewById(R.id.citruscash);

        citruscashWebView = (Button) this.findViewById(R.id.citruscashWebView);

        withdrawMoney = (Button) this.findViewById(R.id.withdraw_money);
        sendMoneyByEmail = (Button) this.findViewById(R.id.send_money_by_email);
        sendMoneyByMobile = (Button) this.findViewById(R.id.send_money_by_mobile);
//        getMerchantPaymentOptions = (Button) this.findViewById(R.id.get_merchant_payment_options);
//        getWallet = (Button) this.findViewById(R.id.get_wallet);

        btnlogoutUser = (Button) this.findViewById(R.id.logoutUser);

        customer = new JSONObject();
        citrusClient = getInstance(this);


        callback = new Callback() {

            @Override
            public void onTaskexecuted(String success, String error) {
                showToast(success, error);
            }
        };

        init();

        initconfig();

        initcustdetails();

    }

    private void init() {


        isSignedin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new WalletStatus(PrepaidWallet.this, callback).execute();
            }
        });

        linkuser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new LinkUser(PrepaidWallet.this, callback)
                        .execute(new String[]{emailID, mobileNo});
            }
        });

        setpass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new SetPassword(PrepaidWallet.this, callback)
                        .execute(new String[]{emailID, mobileNo, password});
            }
        });

        forgot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new ForgotPass(PrepaidWallet.this, emailID, callback)
                        .execute();
            }
        });

        signin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new SignIn(PrepaidWallet.this, callback)
                        .execute(new String[]{emailID, password});
            }
        });

        getbalance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Prepaid user = new Prepaid(emailID);
                user.getBalance(PrepaidWallet.this, callback);
            }
        });

        card_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Card card = new Card("4111111111111111", "04", "21", "778", "Bruce Banner", "debit");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(card, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        card_loadWebView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusUser citrusUser = new CitrusUser("mangesh.kadam@citruspay.com", "8692862420");

                Amount amount = new Amount("5");
                PaymentType paymentType = new PaymentType.LoadMoney(amount, "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");
                DebitCardOption debitCardOption = new DebitCardOption("My Debit Card", "4111111111111111", "123", Month.getMonth("05"), Year.getYear("17"));
                PaymentParams paymentParams = PaymentParams.builder(amount, paymentType, debitCardOption)
                        .environment(PaymentParams.Environment.SANDBOX)
                        .user(citrusUser)
                        .build();

                startCitrusActivity(paymentParams);

            }
        });

        token_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Card card = new Card("c210ecd40f9837e7895068a69f1129d4", "808");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectURL.sandbox.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(card, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        bank_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Bank netbank = new Bank("CID002");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectURL.sandbox.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(netbank, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        token_bank_Load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                Bank netbank = new Bank("48ec899d5dd14be93dce01038a8af60d", BankPaymentType.TOKEN);


                LoadMoney load = new LoadMoney("1", "http://yourwebsite.com/return_url.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(netbank, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        citrus_cashpay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetBill(bill_url, new Callback() {

                    @Override
                    public void onTaskexecuted(String bill, String error) {
                        if (!TextUtils.isEmpty(bill))
                            walletpay(bill);

                        showToast(bill, error);
                    }
                })
                        .execute();
            }
        });


        citruscashWebView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusUser citrusUser = new CitrusUser(emailID, "");

                Amount amount = new Amount("5");

                PaymentType paymentType = new PaymentType.CitrusCash(amount, "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php?" + "amount=" + amount.getValue());


                PaymentParams paymentParams = PaymentParams.builder(amount, paymentType, null)
                        .environment(PaymentParams.Environment.SANDBOX)
                        .user(citrusUser)
                        .build();

                startCitrusActivity(paymentParams);


            }
        });

        withdrawMoney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new CashOutAsynch(PrepaidWallet.this, 10, "Salil Godbole", "042401523201", "ICIC0000424", callback).execute();

            }
        });

        sendMoneyByEmail.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Amount amount = new Amount("37");
                                                    CitrusUser user = new CitrusUser("salil.godbole@citruspay.com", "");

                                                    new SendMoneyAsync(PrepaidWallet.this, amount, user, "My contribution", callback).execute();
                                                }
                                            }
        );

        sendMoneyByMobile.setOnClickListener(new OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     Amount amount = new Amount("30");
                                                     CitrusUser user = new CitrusUser("", "9970950374");

                                                     new SendMoneyAsync(PrepaidWallet.this, amount, user, "My contribution", callback).execute();
                                                 }
                                             }
        );

//        getMerchantPaymentOptions.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getInstance(PrepaidWallet.this).getMerchantPaymentOptions(new com.citrus.sdk.Callback<MerchantPaymentOption>() {
//                    @Override
//                    public void success(MerchantPaymentOption merchantPaymentOption) {
//                        Toast.makeText(PrepaidWallet.this, "merchantPaymentOption received...", Toast.LENGTH_SHORT).show();
//                        Log.d("Citrus", merchantPaymentOption.toString());
//                    }
//
//                    @Override
//                    public void error(CitrusError error) {
//                        Toast.makeText(PrepaidWallet.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });


//        getWallet.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                citrusClient.getWallet(new com.citrus.sdk.Callback<List<PaymentOption>>() {
//                    @Override
//                    public void success(List<PaymentOption> paymentOptionList) {
//                        Toast.makeText(PrepaidWallet.this, "getWallet received...", Toast.LENGTH_SHORT).show();
////                        Log.d("Citrus", merchantPaymentOption.toString());
//                    }
//
//                    @Override
//                    public void error(CitrusError error) {
//                        Toast.makeText(PrepaidWallet.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });
        btnlogoutUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.logoutUser(PrepaidWallet.this))
                    Toast.makeText(getApplicationContext(), Constants.LOGOUT_SUCCESS_MESSAGE, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), Constants.LOGOUT_FAIL_MESSAGE, Toast.LENGTH_LONG).show();
            }
        });

    }



    private void processresponse(String response, String error) {

        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                Intent i = new Intent(PrepaidWallet.this, WebPage.class);

                if (!TextUtils.isEmpty(redirect.getString("redirectUrl"))) {

                    i.putExtra("url", redirect.getString("redirectUrl"));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }

    }


    private void initconfig() {
        Config.setEnv("sandbox"); //replace it with "production" when you are ready
        
        /*Replace following details with oauth details provided to you*/
        Config.setupSignupId("test-signup");
        Config.setupSignupSecret("c78ec84e389814a05d3ae46546d16d2e");

        Config.setSigninId("test-signin");
        Config.setSigninSecret("52f7e15efd4208cf5345dd554443fd99");


        citrusClient.init("test-signup", "c78ec84e389814a05d3ae46546d16d2e", "test-signin", "52f7e15efd4208cf5345dd554443fd99", "prepaid", Environment.SANDBOX);


    }

    private void initcustdetails() {
        /*All the below mentioned parameters are mandatory - missing anyone of them may create errors
         * Do not change the key in the json below - only change the values*/

        try {
            customer.put("firstName", "Tester");
            customer.put("lastName", "Citrus");
            customer.put("email", emailID);
            customer.put("mobileNo", mobileNo);
            customer.put("street1", "streetone");
            customer.put("street2", "streettwo");
            customer.put("city", "Mumbai");
            customer.put("state", "Maharashtra");
            customer.put("country", "India");
            customer.put("zip", "400052");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void walletpay(String bill_string) {
        Bill bill = new Bill(bill_string);

        Prepaid prepaid = new Prepaid(emailID);

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(prepaid, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                prepaidPayment(success, error);
            }
        });
    }


    private void walletpay(BillGeneratorPOJO billGeneratorPOJO) {
        Bill bill = new Bill(billGeneratorPOJO);

        Prepaid prepaid = new Prepaid(emailID);

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(prepaid, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                prepaidPayment(success, error);
            }
        });
    }


    private void prepaidPayment(String response, String error) {

        if (TextUtils.isEmpty(response.toString())) {
            return;
        }

        Callback prepaidCb = new Callback() {

            @Override
            public void onTaskexecuted(String success, String error) {
                showToast(success, error);
            }
        };

        PrepaidPg paymentPg = new PrepaidPg(PrepaidWallet.this);

        paymentPg.pay(prepaidCb, response, error);
    }

    private void showToast(String message, String error) {
        if (!TextUtils.isEmpty(message))
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        if (!TextUtils.isEmpty(error))
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }


    private void startCitrusActivity(PaymentParams paymentParams) {
        Intent intent = new Intent(PrepaidWallet.this, CitrusActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TransactionResponse transactionResponse = data.getParcelableExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE);
        if (transactionResponse != null) {
            Toast.makeText(getApplicationContext(), transactionResponse.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


  /*  citrus_cashpay.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
            new GetJSONBill("https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php", "3.0", new retrofit.Callback<BillGeneratorPOJO>() {
                @Override
                public void success(BillGeneratorPOJO billGeneratorPOJO, Response response) {
                    Log.d("BILLPOJO**", billGeneratorPOJO.getAmount().getValue());

                    walletpay(billGeneratorPOJO);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            }).getJSONBill();
        }
    });*/


}
