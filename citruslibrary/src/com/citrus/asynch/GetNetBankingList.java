package com.citrus.asynch;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MANGESH KADAM on 2/16/2015.
 */
public class GetNetBankingList {

    Context context;
    RequestQueue queue;
    final String URL = "https://sandboxadmin.citruspay.com/service/v1/merchant/pgsetting";
    String response = null;
    Response.Listener successListener;
    Response.ErrorListener errorListener;

    public GetNetBankingList(Context context, Response.Listener successListener, Response.ErrorListener errorListener) {
        this.context = context;
        this.successListener = successListener;
        this.errorListener = errorListener;
        queue = Volley.newRequestQueue(context);
    }

    public void getBankList(final String vanity) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, successListener, errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("vanity", vanity);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
