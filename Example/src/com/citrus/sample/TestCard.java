package com.citrus.sample;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrus.card.Card;
import com.citrus.mobile.Callback;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class TestCard extends Activity {
	
	EditText cardnum, cvv, nameoncard, expdate;
	
	Button pay;
	
	String cardnumber, cvvstr, name, expdatestr;
	
	RadioGroup group;
	
	int id;
	
	String url = "http://103.13.97.20/citrus/production/sign.php";
	
	Callback callback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_card);
		
		cardnum = (EditText) this.findViewById(R.id.cardnum);
		
		cvv = (EditText) this.findViewById(R.id.cvv);
		
		nameoncard = (EditText) this.findViewById(R.id.nameoncard);
		
		expdate = (EditText) this.findViewById(R.id.expdate);
		
		pay = (Button) this.findViewById(R.id.paybutton);
		
		group = (RadioGroup) this.findViewById(R.id.radiogroup);
		
		init();
	}

	private void init() {
		pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 if (!checkfornull()) 
					 	cardpay();
					 
			}
		});
	}
	
	private boolean checkfornull() {
		
		id = group.getCheckedRadioButtonId();
		
		if (id == -1) {
			return true;
		}
		
		List<String> strarray = new ArrayList<String>();
			
		cardnumber = cardnum.getText().toString();
		
		cvvstr= cvv.getText().toString();
		
		name = nameoncard.getText().toString();
		
		expdatestr = expdate.getText().toString();
		
		strarray.add(cardnumber);
		
		strarray.add(cvvstr);
		
		strarray.add(name);
		
		strarray.add(expdatestr);
		
		for (String s : strarray) {
			if (TextUtils.isEmpty(s)) 
					return true;
		}
		
		return false;
	}
	
	private void cardpay() {
			
		callback = new Callback() {
			
			@Override
			public void onTaskexecuted(String success, String error) {
				cardpay(success);
			}
		};
		
		new GetBill(url, callback).execute();
	}
	
	private void cardpay(String bill_string) {
		
		String type;
		if (id == R.id.credit) {
			type = "credit";
		}
		else {
			type = "debit";
		}
		
        Bill bill = new Bill(bill_string);

        String month, year;
		
		month = expdatestr.split("/")[0];
		
		year = expdatestr.split("/")[1];
        
		Card card = new Card(cardnumber, month, year, cvvstr, name, type);
		
		JSONObject customer = new JSONObject();
		
		try {
			customer.put("firstName", "Tester");
	        customer.put("lastName", "Citrus");
	        customer.put("email", "testeremail@mailinator.com");
	        customer.put("mobileNo", "9787543290");
	        customer.put("street1", "streetone");
	        customer.put("street2", "streettwo");
	        customer.put("city", "Mumbai");
	        customer.put("state", "Maharashtra");
	        customer.put("country", "India");
	        customer.put("zip", "400052");
		} catch (JSONException e) {
			return;
		}
		
        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(card, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                processresponse(success, error);
            }
        });
    }
	
	private void processresponse(String response, String error) {

        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                Intent i = new Intent(TestCard.this, WebPage.class);

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