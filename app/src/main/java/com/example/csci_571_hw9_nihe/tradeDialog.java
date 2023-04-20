package com.example.csci_571_hw9_nihe;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Formatter;

public class tradeDialog extends AppCompatDialogFragment {
    public String company_ticker;
    public String company_name;
    public double money_left;
    public double current_price;
    public double money_cost;
    public int shares;

    public Context parent;
    public TextView shares_outside;
    public TextView avg_outside;
    public TextView totalCost_outside;
    public TextView marketValue_outside;
    public double bought_price;
    public void setAvg_outside(TextView avg_outside) {
        this.avg_outside = avg_outside;
    }

    public void setTotalCost_outside(TextView totalCost_outside) {
        this.totalCost_outside = totalCost_outside;
    }

    public void setMarketValue_outside(TextView marketValue_outside) {
        this.marketValue_outside = marketValue_outside;
    }

    public void setShares(TextView shares) {
        this.shares_outside = shares;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }

    public void setMoney_left(double money_left) {
        this.money_left = money_left;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public void setCompany_ticker(String company_ticker) {
        this.company_ticker = company_ticker;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myPref",getActivity().MODE_PRIVATE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tradedialog, null);
        view.setClipToOutline(true);
        builder.setView(view);
        TextView tradeName = view.findViewById(R.id.Trade_Name);
        String trade_name = "Trade "+ this.company_name + " shares";
        tradeName.setText(trade_name);
        TextView price = (TextView)view.findViewById(R.id.estimate_price);
        TextView cost = (TextView)view.findViewById(R.id.estimate_cost);
        TextView money = (TextView)view.findViewById(R.id.moneyleft);
        TextView wanted_ticker = (TextView) view.findViewById(R.id.wanted_ticker);
        wanted_ticker.setText(company_ticker);
        if(sharedPreferences.contains("port")){
            try {
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("port", ""));
                money.setText(jsonObject.getString("Cash"));
                money_left = Double.valueOf(jsonObject.getString("Cash"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String Current_ = "*$"+round2(current_price);
        price.setText(Current_);

        EditText text = (EditText)view.findViewById(R.id.editTextNumber);
        text.setHint(String.valueOf(0));
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().matches("")){
                    shares = 0;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().matches("")){
                    TextView numOfShares = (TextView)view.findViewById(R.id.estimate_shares);
                    numOfShares.setText(String.valueOf(0));
                    cost.setText(String.valueOf(0));
                }else{
                    double userInputNum = Double.valueOf(editable.toString());
                    shares = (int)(userInputNum);
                    String userInputString = editable.toString();
                    TextView numOfShares = (TextView)view.findViewById(R.id.estimate_shares);
                    double totalCost = userInputNum * current_price;
                    totalCost = round2(totalCost);
                    money_cost = totalCost;
                    cost.setText(String.valueOf(totalCost));
                    numOfShares.setText(userInputString);
                }
            }
        });


        Button buy_transaction = view.findViewById(R.id.buy_button);
        buy_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shares <= 0){
                    CharSequence not_enough_money = "Cannot buy non-positive shares";
                    Toast toast = Toast.makeText(getContext(),not_enough_money,Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myPref",getActivity().MODE_PRIVATE);
                boolean success = false;
                if(sharedPreferences.contains("port")){
                    try {
                        JSONObject jsonObject = new JSONObject(sharedPreferences.getString("port", ""));
                        JSONArray jsonArray = jsonObject.getJSONArray("ticker_info");
                        JSONObject temp = hasValue(jsonArray, company_ticker);
                        if(temp != null){
                            JSONObject owned_ticker = temp.getJSONObject(company_ticker);
                            int shares_owned = owned_ticker.getInt("shares");
                            bought_price = owned_ticker.getDouble("bought_price");
                            int new_shares_owned = shares_owned + shares;
                            double new_bought_price =( (bought_price * shares_owned)  + (current_price *shares) )/ new_shares_owned;
                            new_bought_price = round2(new_bought_price);
                            owned_ticker.put("shares",new_shares_owned );
                            owned_ticker.put("bought_price", new_bought_price);
                            update_ticker(jsonArray,owned_ticker,company_ticker);
                            double total_cost = current_price * shares;
                            if(total_cost > money_left){
                                CharSequence not_enough_money = "Not enough money to buy";
                                Toast toast = Toast.makeText(getContext(),not_enough_money,Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            total_cost = round2(total_cost);
                            money_left = money_left - total_cost;
                            money_left = round2(money_left);
                            jsonObject.put("Cash",money_left);
                            jsonObject.put("ticker_info", jsonArray);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("port",jsonObject.toString());
                            editor.commit();
                            success = true;
                            update_port_buy(new_shares_owned, new_bought_price);
                        }else{
                            JSONObject stock_port_info = new JSONObject();
                            stock_port_info.put("shares",shares );
                            current_price = round2(current_price);
                            double price = current_price;
                            stock_port_info.put("bought_price", current_price);
                            JSONObject stock_port = new JSONObject();
                            stock_port.put(company_ticker, stock_port_info);
                            jsonArray.put(stock_port);
                            double total_cost = current_price * shares;
                            if(total_cost > money_left){
                                CharSequence not_enough_money = "Not enough money to buy";
                                Toast toast = Toast.makeText(getContext(),not_enough_money,Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            total_cost = round2(total_cost);
                            money_left = money_left - total_cost;
                            money_left = round2(money_left);
                            jsonObject.put("Cash",money_left);
                            jsonObject.put("ticker_info", jsonArray);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("port",jsonObject.toString());
                            editor.commit();
                            success = true;
                            update_port_buy(shares, price);
                        }
                        if(success){
                            openSuccessDialog(company_ticker,shares);
                            dismiss();
                        }
                        JSONObject jsonObject1 = new JSONObject(sharedPreferences.getString("port", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        Button sell_transaction = (Button) view.findViewById(R.id.sell_button);
        sell_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shares <= 0){
                    CharSequence not_valid_sellAmount = "Cannot sell non-positive shares";
                    Toast toast = Toast.makeText(getContext(),not_valid_sellAmount,Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myPref",getActivity().MODE_PRIVATE);
                if(sharedPreferences.contains("port")){
                    try {
                        JSONObject jsonObject = new JSONObject(sharedPreferences.getString("port", ""));
                        JSONArray jsonArray = jsonObject.getJSONArray("ticker_info");
                        JSONObject temp = hasValue(jsonArray, company_ticker);
                        if(temp != null){
                            JSONObject owned_ticker = temp.getJSONObject(company_ticker);
                            int shares_owned = owned_ticker.getInt("shares");
                            double bought_price = owned_ticker.getDouble("bought_price");
                            if(shares_owned < shares){
                                CharSequence not_enough_shares = "Not enough shares to sell";
                                Toast toast = Toast.makeText(getContext(),not_enough_shares,Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            double total_earn = current_price * shares;
                            total_earn = round2(total_earn);
                            money_left = money_left + total_earn;
                            money_left = round2(money_left);
                            jsonObject.put("Cash",money_left);
                            int shares_left = shares_owned - shares;
                            if(shares_left == 0){
                                remove_ticker(jsonArray,owned_ticker,company_ticker);
                            }else{
                                owned_ticker.put("shares", shares_left);
                                update_ticker(jsonArray, owned_ticker, company_ticker);
                            }
                            jsonObject.put("ticker_info", jsonArray);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("port",jsonObject.toString());
                            editor.commit();
                            openSellSuccessDialog(company_ticker,shares);
                            Log.i("shares_left", String.valueOf(shares_left));
                            update_port_sell(shares_left,bought_price);
                            dismiss();
                        }else{
                            Toast toast = Toast.makeText(getContext(),"You don't own any shares of this stock",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }

                        JSONObject jsonObject1 = new JSONObject(sharedPreferences.getString("port", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    private void update_port_sell(int shares_owned, double bought_price) {
//        TextView shares = parent..findViewById(R.id.Shares_value);
//        shares.setText(String.valueOf(shares_owned));
        shares_outside.setText(String.valueOf(shares_owned));
        double new_market_value = shares_owned * current_price;
        marketValue_outside.setText("$" + String.valueOf(round2(new_market_value)));
        if(shares_owned == 0){
            avg_outside.setText("$0.00" );
        }else{
            avg_outside.setText("$" + String.valueOf( round2(bought_price)));
        }
        totalCost_outside.setText("$" + String.valueOf( round2(shares_owned* bought_price)));
        if(round2(new_market_value) == round2(shares_owned* bought_price)){
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }else if(round2(new_market_value) > round2(shares_owned * bought_price)){
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
    }

    private void update_port_buy(int shares_owned, double buyPrice) {
//        TextView shares = parent..findViewById(R.id.Shares_value);
//        shares.setText(String.valueOf(shares_owned));
        shares_outside.setText(String.valueOf(shares_owned));
        double new_market_value = shares_owned * current_price;
        marketValue_outside.setText("$" + String.valueOf(round2(new_market_value)));
        avg_outside.setText("$" + String.valueOf( round2(buyPrice)));
        totalCost_outside.setText("$" + String.valueOf( round2(shares_owned* buyPrice)));
        if(round2(new_market_value) == round2(shares_owned* buyPrice)){
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }else if(round2(new_market_value) > round2(shares_owned * buyPrice)){
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            marketValue_outside.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
    }

    public JSONObject hasValue(JSONArray json, String key) {
        for(int i = 0; i < json.length(); i++) {
            JSONObject obj;
            try {
                obj = json.getJSONObject(i);
                if(obj.has(key)){
                    return obj;
                }else{
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void update_ticker(JSONArray json,JSONObject new_buy, String key) {
        for(int i = 0; i < json.length(); i++) {
            JSONObject obj;
            try {
                obj = json.getJSONObject(i);
                if(obj.has(key)){
                    obj.put(key,new_buy );
                    return;
                }else{
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public void remove_ticker(JSONArray json,JSONObject new_sell, String key) {
        for(int i = 0; i < json.length(); i++) {
            JSONObject obj;
            try {
                obj = json.getJSONObject(i);
                if(obj.has(key)){
                    json.remove(i);
                    return;
                }else{
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return;
    }
    public void openSuccessDialog(String ticker, int shares){
        buySuccess buySuccess = new buySuccess();
        buySuccess.setCompany_ticker(ticker);
        buySuccess.setShares(shares);
        buySuccess.show(getActivity().getSupportFragmentManager(),"buySuccess");
    }

    public void openSellSuccessDialog(String ticker, int shares){
        sellSuccess sellSuccess = new sellSuccess();
        sellSuccess.setShares(shares);
        sellSuccess.setCompany_ticker(ticker);
        sellSuccess.show(getActivity().getSupportFragmentManager(),"sellSuccess");
    }

    public double round2(double number) {
        double roundOff = Math.round(number * 100.0) / 100.0;
        return roundOff;
    }
}
