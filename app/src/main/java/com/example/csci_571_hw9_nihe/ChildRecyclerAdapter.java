package com.example.csci_571_hw9_nihe;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.List;

// this Recycler View referenced to video https://www.youtube.com/watch?v=x5afKIu0JmY&list=WL&index=2&t=170s
// credit by youtuber yoursTRULY
public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {
    List<String> items;
    private Handler mHandler;
    private Runnable mRunnable;
    int counter = 1;
    private JSONObject fav_company_name;

    public void setFav_company_name(JSONObject fav_company_name) {
        this.fav_company_name = fav_company_name;
    }

    private RecyclerViewClickInterface recyclerViewClickInterface;
    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
    public void setmRunnable(Runnable mRunnable) {
        this.mRunnable = mRunnable;
    }

    public ChildRecyclerAdapter(List<String> items, RecyclerViewClickInterface recyclerViewClickInterface1) {
        this.recyclerViewClickInterface = recyclerViewClickInterface1;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_row,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestQueue myQueue = Volley.newRequestQueue(holder.itemView.getContext());

        RequestFavoriteInfo(holder, items.get(position), myQueue);
//        RequestFavoriteInfo_Name(holder, items.get(position), myQueue);
        if(counter == 1){
            counter = counter + 1;
            timer15s(myQueue, holder, position);
        }

        holder.itemTextView.setText(items.get(position));
        String company_name = "";
        try {
            company_name = fav_company_name.getString(items.get(position));
        }catch (JSONException e){
            e.printStackTrace();
        }

        holder.favCompanyName.setText(company_name);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemTextView;
        TextView favCompanyName;
        TextView favCurrentPrice;
        TextView favPriceChange;
        ImageView favTrending;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.port_ticker);
            favCompanyName = itemView.findViewById(R.id.SharesOwn);
            favCurrentPrice = itemView.findViewById(R.id.port_marketValue);
            favPriceChange = itemView.findViewById(R.id.Changefromtotal);
            favTrending = itemView.findViewById(R.id.portTrending);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition(), itemView);
                }
            });
        }
    }
    public void timer15s(RequestQueue myQueue, ViewHolder holder, int position){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    notifyDataSetChanged();
                }finally {
//                    notifyDataSetChanged();
                    timer15s(myQueue, holder, position);
                }
            }
        };
        this.mHandler.postDelayed(mRunnable,15000);
    }

    public void RequestFavoriteInfo(@NonNull ViewHolder holder, String ticker, RequestQueue myQueue){
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/quote/" + ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double change = response.getDouble("d");
                            double Precentchange = response.getDouble("dp");
                            String temp2 = "$" + response.getString("c");
                            holder.favCurrentPrice.setText(temp2);
                            Formatter formatter = new Formatter();
                            formatter.format("%.2f", change);
                            Formatter formatter2 = new Formatter();
                            formatter2.format("%.2f", Precentchange);
                            String temp = "$" + formatter + "(" + formatter2 + "%)";
                            holder.favPriceChange.setText(temp);
                            if(change > 0){
                                holder.favTrending.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                holder.favPriceChange.setTextColor(ContextCompat.getColor(holder.favPriceChange.getContext(), R.color.green));
                            }else if(change < 0){
                                holder.favPriceChange.setTextColor(ContextCompat.getColor(holder.favPriceChange.getContext(), R.color.red));
                                holder.favTrending.setImageResource(R.drawable.ic_baseline_trending_down_24);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
    }

    public void RequestFavoriteInfo_Name(@NonNull ViewHolder holder, String ticker, RequestQueue myQueue){
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/profile/" + ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject jsonObject = response.getJSONObject("id");
                        try {
                            holder.favCompanyName.setText(response.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
