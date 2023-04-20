package com.example.csci_571_hw9_nihe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class Hourly_chart extends Fragment {

    public JSONObject candle;
    public String ticker;
    public boolean going_up;
    public RequestQueue myQueue;
    public int time;
    public Hourly_chart(JSONObject candle, String ticker , boolean going_up, RequestQueue requestQueue, int time) {
        this.myQueue = requestQueue;
        this.time = time;
        this.candle = candle;
        this.ticker = ticker;
        this.going_up = going_up;
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hourly_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetch_candle(ticker,view, time);

    }

    private void getCandleAndPopulate(JSONObject candle, View view, String ticker, boolean going_up){
        this.candle = candle;

        WebView myWebView = (WebView) view.findViewById(R.id.hourly_chart);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/Hourly_chart.html");
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                String here = ticker;
                boolean going_up1 = going_up;
                JSONObject ticker_json;
                try {
                    ticker_json = candle.put("ticker", here);
                    ticker_json = ticker_json.put("going_up", going_up1);
                    Log.i("json", String.valueOf(ticker_json));
                    myWebView.loadUrl("javascript:init_hour('"+ ticker_json + "')");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetch_candle(String ticker,View view, int time) {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        String timestamp_string = String.valueOf(timestamp.getTime());
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/candle/" + ticker + "/" + time;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject jsonObject = response.getJSONObject("id");
                        getCandleAndPopulate(response,view,ticker, going_up);
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