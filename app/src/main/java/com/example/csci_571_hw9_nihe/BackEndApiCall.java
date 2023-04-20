package com.example.csci_571_hw9_nihe;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackEndApiCall {
    private static final String BASE_URL = "https://nihe-hw8-new.wl.r.appspot.com/search/";
    private String ticker;
    private RequestQueue myQueue;
    private JSONObject summary;
    public BackEndApiCall(String ticker, RequestQueue queue) {
        this.ticker = ticker;
        this.myQueue = queue;
    }

    public JSONObject getSummary() {
        return summary;
    }

    public void fetch_summary(Response.Listener<JSONObject> temp){
        String url = BASE_URL + "profile/" + this.ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        summary = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
    }
}
