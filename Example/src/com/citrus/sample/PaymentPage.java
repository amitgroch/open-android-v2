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
package com.citrus.sample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.citrus.card.Card;
import com.citrus.mobile.Callback;
import com.citrus.netbank.Bank;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;

public class PaymentPage extends Activity {
    public static final String BILL_URL = "http://yourwebsite.com/bill.php";

    Button cardpayment, tokenpayment, bankpay;

    JSONObject customer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        cardpayment = (Button) this.findViewById(R.id.cardpayment);

        tokenpayment = (Button) this.findViewById(R.id.tokenpayment);

        bankpay = (Button) this.findViewById(R.id.bankpay);
                                
        customer = new JSONObject();

        cardpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetBill(BILL_URL, new Callback() {
                    @Override
                    public void onTaskexecuted(String bill, String error) {
                        if (TextUtils.isEmpty(error)) {
                            cardpay(bill);
                        }
                    }
                }).execute();
            }
        });

        tokenpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetBill(BILL_URL, new Callback() {
                    @Override
                    public void onTaskexecuted(String bill, String error) {
                          tokenpay(bill);
                    }
                }).execute();
            }
        });

        bankpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetBill(BILL_URL, new Callback() {
                    @Override
                    public void onTaskexecuted(String bill, String error) {
                          bankpay(bill);
                    }
                }).execute();
            }
        });
               
        filluserDetails();
    }

    private void cardpay(String bill_string) {
        Bill bill = new Bill(bill_string);

        Card card = new Card("4111111111111111", "11", "21", "000", "Tony Stark", "debit");

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(card, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                processresponse(success, error);
            }
        });
    }

    private void tokenpay(String bill_string) {
        Bill bill = new Bill(bill_string);

        Card card = new Card("e8c18a9aac39cfeb6f0d02f28ed4660b", "123");

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(card, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                processresponse(success, error);
            }
        });
    }

    private void bankpay(String bill_string) {
        Bill bill = new Bill(bill_string);

        Bank netbank = new Bank("CID002");

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(netbank, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                processresponse(success, error);
            }
        });
    }
    
    private void filluserDetails() {
        /*All the below mentioned parameters are mandatory - missing anyone of them may create errors
        * Do not change the key in the json below - only change the values*/

		try {
			customer.put("firstName", "Tester");
			customer.put("lastName", "Citrus");
			customer.put("email", "tester@gmail.com");
			customer.put("mobileNo", "9170164284");
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

    private void processresponse(String response, String error) {

        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                Intent i = new Intent(PaymentPage.this, WebPage.class);

                if (!TextUtils.isEmpty(redirect.getString("redirectUrl"))) {

                    i.putExtra("url", redirect.getString("redirectUrl"));
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }

    }
    
}
