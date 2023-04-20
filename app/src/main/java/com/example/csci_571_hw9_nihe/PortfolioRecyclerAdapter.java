package com.example.csci_571_hw9_nihe;

import android.content.SharedPreferences;
import android.os.Handler;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter{
    double Cash;
    double netWorth;
    double update_netWorth;
    JSONArray items;
    private static int basic_info = 1;
    private static int stock_owned = 2;
    private Handler handler_port;
    BasicViewHolder basicViewHolder;
    StockViewHolder stockViewHolder;
    List<String> ticker_keys;
    RecyclerViewClickInterface recyclerViewClickInterface;
    int counter = 1;
    public void setTicker_keys(List<String> ticker_keys) {
        this.ticker_keys = ticker_keys;
    }


    public void setHandler_port(Handler handler_port) {
        this.handler_port = handler_port;
    }
    private Runnable runnable_port;
    public void setRunnable_port(Runnable runnable_port) {
        this.runnable_port = runnable_port;
    }


    public PortfolioRecyclerAdapter(double cash, double netWorth, JSONArray items, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.Cash = cash;
        this.netWorth = netWorth;
        this.items = items;
    }


    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return basic_info;
        }else{
            return stock_owned;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == basic_info){
            view = layoutInflater.inflate(R.layout.basic_row,parent, false);
            return new BasicViewHolder(view);
        }else{
            view = layoutInflater.inflate(R.layout.port_row,parent, false);
            return new StockViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RequestQueue myQueue = Volley.newRequestQueue(holder.itemView.getContext());

        if(position == 0){
            basicViewHolder = (BasicViewHolder) holder;
            String cash ="$"+String.valueOf(this.Cash);
            String netWorth="$"+String.valueOf(this.netWorth);
            basicViewHolder.Cash.setText(cash);
            basicViewHolder.NetWorth.setText(netWorth);
        }else{
            stockViewHolder = (StockViewHolder) holder;
//            JSONObject ticker_info = new JSONObject();
//            try {
//                ticker_info = items.getJSONObject(position-1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Iterator<String> keys = ticker_info.keys();
//            this.update_netWorth = this.Cash;
//            while (keys.hasNext()){
//                String key = keys.next();
////                ticker_keys.add(key);
//                stockViewHolder.itemTextView.setText(key);
//                JSONObject bought_info = new JSONObject();
//                String bought_price;
//                try {
//                    bought_info = ticker_info.getJSONObject(key);
//                    stockViewHolder.Shares.setText(bought_info.getString("shares"));
//                    bought_price = bought_info.getString("bought_price");
//                    int shares = Integer.valueOf(bought_info.getString("shares"));
//                    RequestPortInfo(stockViewHolder, key, myQueue, Double.valueOf(bought_price), shares);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            String ticker = ticker_keys.get(position);
            try{
                update_netWorth = Cash;
                String target_ticker = ticker_keys.get(position);
                stockViewHolder.itemTextView.setText(target_ticker);
                for(int j = 0; j < items.length();j++){
                    JSONObject ticker_info = items.getJSONObject(j);
                    if(ticker_info.has(target_ticker)){
                        JSONObject specific_ticker = ticker_info.getJSONObject(target_ticker);
                        String bought_price = specific_ticker.getString("bought_price");
                        stockViewHolder.Shares.setText(specific_ticker.getString("shares") + " shares");
                        int shares = Integer.valueOf(specific_ticker.getString("shares"));
                        RequestPortInfo(stockViewHolder, target_ticker,myQueue,Double.valueOf(bought_price),shares);
                        break;
                    }else{
                        continue;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            }

            if(position!=0){
                update_timer15s( position, myQueue, stockViewHolder);
            }

        }



    private void update_timer15s(int position, RequestQueue myQueue, StockViewHolder holder) {
        runnable_port = new Runnable() {
            @Override
            public void run() {
                try{

                        update_netWorth = Cash;
                        String target_ticker = ticker_keys.get(position);
                        for(int j = 0; j < items.length();j++){
                            JSONObject ticker_info = items.getJSONObject(j);
                            if(ticker_info.has(target_ticker)){
                                JSONObject specific_ticker = ticker_info.getJSONObject(target_ticker);
                                String bought_price = specific_ticker.getString("bought_price");
                                int shares = Integer.valueOf(specific_ticker.getString("shares"));
                                RequestPortInfo(holder, target_ticker,myQueue,Double.valueOf(bought_price),shares);
                                break;
                            }else{
                                continue;
                            }
                        }
                    Log.i("portfolio ", String.valueOf(position));

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    update_timer15s(position, myQueue, holder);
                }
            }
        };
        this.handler_port.postDelayed(runnable_port,15000);
    }

    private void RequestPortInfo(@NonNull StockViewHolder holder, String key, RequestQueue myQueue, double bought_price, int shares) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/quote/" + key;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            double change = response.getDouble("d");
                            double percentChange = response.getDouble("dp");
                            double latestPrice = response.getDouble("c");

                            // calculate and set market value based on latest price
                            double marketValue = latestPrice * shares;
                            update_netWorth = update_netWorth + marketValue;
                            update_netWorth = round2(update_netWorth);
                            netWorth = update_netWorth;
                            basicViewHolder.NetWorth.setText("$"+String.valueOf(update_netWorth));
                            marketValue= round2(marketValue);
                            holder.portCurrentValue.setText(String.valueOf("$" + marketValue));
                            double AvgPurchasePrice = bought_price;
                            double changePricesFromTotalCost = (latestPrice - AvgPurchasePrice)*shares;
                            changePricesFromTotalCost = round2(changePricesFromTotalCost);
                            double changePricesFromTotalCost_P  = (changePricesFromTotalCost/(AvgPurchasePrice*shares))*100;
                            changePricesFromTotalCost_P = round2(changePricesFromTotalCost_P);
                            String change_ = "$" + changePricesFromTotalCost + "(" + changePricesFromTotalCost_P + "%)";
                            holder.portPriceChange.setText(change_);
                            if(changePricesFromTotalCost_P > 0){
                                holder.portTrending.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                holder.portPriceChange.setTextColor(ContextCompat.getColor(holder.portPriceChange.getContext(), R.color.green));
                            }else if(changePricesFromTotalCost_P < 0){
                                holder.portTrending.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                holder.portPriceChange.setTextColor(ContextCompat.getColor(holder.portPriceChange.getContext(), R.color.red));
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


    @Override
    public int getItemCount() {
        return items.length()+1;
    }

    class BasicViewHolder extends RecyclerView.ViewHolder{
        TextView Cash;
        TextView NetWorth;

        public BasicViewHolder(@NonNull View itemView) {
            super(itemView);
            Cash = itemView.findViewById(R.id.Cash);
            NetWorth = itemView.findViewById(R.id.NetWorth);
        }
    }

    class StockViewHolder extends RecyclerView.ViewHolder{
        TextView itemTextView;
        TextView Shares;
        TextView portCurrentValue;
        TextView portPriceChange;
        ImageView portTrending;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.port_ticker);
            Shares = itemView.findViewById(R.id.SharesOwn);
            portCurrentValue = itemView.findViewById(R.id.port_marketValue);
            portPriceChange = itemView.findViewById(R.id.Changefromtotal);
            portTrending = itemView.findViewById(R.id.portTrending);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.onItemClick_port(getAdapterPosition(), itemView);
                }
            });
        }
    }
    public double round2(double number) {
        double roundOff = Math.round(number * 100.0) / 100.0;
        return roundOff;
    }

}
