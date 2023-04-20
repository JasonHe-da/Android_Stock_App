package com.example.csci_571_hw9_nihe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

public class TabAdapter extends FragmentStateAdapter {

    public RequestQueue requestQueue;
    public String ticker;
    public JSONObject history;
    public JSONObject candle;
    public boolean going_up;
    public int time;

    public void setTime(int time) {
        this.time = time;
    }

    public void setGoing_up(boolean going_up) {
        this.going_up = going_up;
    }

    public void setCandle(JSONObject candle) {
        this.candle = candle;
    }

    public void setHistory(JSONObject history) {
        this.history = history;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if(position== 0){
            fragment = new Hourly_chart(candle,ticker,going_up,requestQueue, time);
        }else{
            fragment = new Historical_chart(requestQueue, ticker, history);

        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
