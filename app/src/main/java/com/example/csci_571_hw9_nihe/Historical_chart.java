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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Objects;


public class Historical_chart extends Fragment {
    public String ticker;
    public RequestQueue myQueue;
    public JSONObject history;
    public Historical_chart(RequestQueue requestQueue, String ticker, JSONObject history) {
        this.myQueue = requestQueue;
        this.ticker = ticker;
        this.history = history;
        // Required empty public constructor
    }

    public void setHistory(JSONObject history) {
        this.history = history;
    }

//    private RequestQueue myQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_historical_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        fetch_history(ticker, view);
        getHistoryAndPopulate(this.history, view, ticker);
    }


    private void getHistoryAndPopulate(JSONObject history, View view, String ticker){
        this.history = history;

        Log.i("LogHere!",String.valueOf(history));
        WebView myWebView = (WebView) view.findViewById(R.id.historical_chart);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/historical_chart.html");
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                String here = ticker;
                JSONObject ticker_json;
                try {
                    ticker_json = history.put("ticker", here);
                    Log.i("ticker",String.valueOf(ticker_json.getString("ticker")));
                    Log.i("msg1", String.valueOf(ticker_json));
                    myWebView.loadUrl("javascript:init('"+ ticker_json + "')");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}