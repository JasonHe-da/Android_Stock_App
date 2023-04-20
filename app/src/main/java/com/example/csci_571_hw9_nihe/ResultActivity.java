package com.example.csci_571_hw9_nihe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
//import com.highsoft.highcharts.core.*;
//import com.highsoft.highcharts.common.hichartsclasses.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "PEERS COMPANY";
    private int star_state = 0;
    public String company_name;
    private RequestQueue myQueue;
    private JSONObject summary;
    public SharedPreferences sharedPreferences;
    private Quote myQuote = new Quote();
    public tradeDialog tradedialog = new tradeDialog();
    public TabAdapter tabAdapter;
    public Handler res_handler = new Handler(Looper.getMainLooper());;
    public Runnable res_runnable;
    public boolean going_up;
    public String ticker_symobl;
    public TabLayout tabLayout;
    public ViewPager2 viewPager2;
    public int shares_own;
    public double bought_price;
    TextView shares_outside_dialog ;
    TextView avgCS_outside;
    TextView total_cost_outside;
    TextView marketValue_outside;
    public String Company_name = "";
    int count = 0;
    public ArrayList<String> peers_list = new ArrayList<>();
    public RecyclerView newsRecycler;
    private int[] tab_icons = {
            R.drawable.chart_line,
            R.drawable.clock_time_three
    };

//    private boolean profile_fetch
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo: check if ticker in fav

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        ticker_symobl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(ticker_symobl == null){
            ticker_symobl = intent.getStringExtra(ResultActivity.EXTRA_MESSAGE);
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout constraintLayout = findViewById(R.id.tradeSection);
                ConstraintLayout constraintLayout1 = findViewById(R.id.tab_chart);
                ConstraintLayout constraintLayout2 = findViewById(R.id.basic_info);
                TabLayout tabLayout = findViewById(R.id.tabLayout);
                constraintLayout.setVisibility(View.VISIBLE);
                constraintLayout1.setVisibility(View.VISIBLE);
                constraintLayout2.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                ConstraintLayout pregress = findViewById(R.id.pregress_res);
                pregress.setVisibility(View.GONE);
            }
        }, 4500);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_result);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(ticker_symobl == "AAPL"){
                    MainActivity.reloadNeeded = true;
//                }
                Log.d("here??","up");
                finish();
            }
        });
        getSupportActionBar().setTitle(ticker_symobl);
        checkfavTicker(ticker_symobl);
        getFav(ticker_symobl);
        updatePort(ticker_symobl);
        fetch_all_data(ticker_symobl);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        shares_outside_dialog = (TextView)findViewById(R.id.Shares_value);
        avgCS_outside = (TextView)findViewById(R.id.Avg_value);
        total_cost_outside = (TextView)findViewById(R.id.Cost_value);
        marketValue_outside = (TextView)findViewById(R.id.Market_Value_value);

        Button trade_button = findViewById(R.id.trade_button);
        trade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home){
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void updatePort(String ticker) {
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        if(sharedPreferences.contains("port")){
            try {
                JSONObject jsonObject_port = new JSONObject(sharedPreferences.getString("port", ""));
                JSONArray jsonArray_port = jsonObject_port.getJSONArray("ticker_info");
                JSONObject temp = hasValue(jsonArray_port, ticker);
                if(temp!= null){
                    JSONObject owned_ticker = temp.getJSONObject(ticker);
                    shares_own = owned_ticker.getInt("shares");
                    bought_price = owned_ticker.getDouble("bought_price");
                    bought_price = round2(bought_price);
                    TextView sharesOwn = (TextView) findViewById(R.id.Shares_value);
                    TextView avgCS = (TextView) findViewById(R.id.Avg_value);
                    TextView TotalCost = (TextView) findViewById(R.id.Cost_value);
                    sharesOwn.setText(String.valueOf(shares_own));
                    avgCS.setText("$" + String.valueOf(bought_price));
                    TotalCost.setText("$" + String.valueOf(round2(shares_own * bought_price)));

                }else{
                    return;
                }
//                for(int i = 0; i < jsonArray_port.length();i++){
//                    if(jsonArray_port.getJSONObject(i).has(ticker)){
//                        shares_own = jsonArray_port.getJSONObject(i).getString("shares");
//                        bought_price = jsonArray_port.getJSONObject(i).getString("bought_price");
//                        TextView sharesOwn = (TextView) findViewById(R.id.Shares_value);
//                        TextView avgCS = (TextView) findViewById(R.id.Avg_value);
//                        TextView TotalCost = (TextView) findViewById(R.id.Cost_value);
//                        sharesOwn.setText(String.valueOf(sharesOwn));
//                        avgCS.setText(String.valueOf(bought_price));
//                        TotalCost.setText(String.valueOf(round2(shares_own * bought_price)));
//                    }else{
//                        continue;
//                    }
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void openDialog(){
        tradedialog.setShares(shares_outside_dialog);
        tradedialog.setAvg_outside(avgCS_outside);
        tradedialog.setMarketValue_outside(marketValue_outside);
        tradedialog.setTotalCost_outside(total_cost_outside);
        tradedialog.show(getSupportFragmentManager(),"trade dialog");
    };

    void checkfavTicker(String ticker){
        ImageButton favorite_button = (ImageButton) findViewById(R.id.star_nofill);
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        if(sharedPreferences.contains("fav")){
            try {
                jsonArray = new JSONArray(sharedPreferences.getString("fav", "[]"));
                for(int i = 0; i < jsonArray.length(); i++){
                    if(jsonArray.getString(i).equals(ticker) ){
                        star_state = 1;
                        favorite_button.setImageResource(R.drawable.ic_baseline_star_24);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    void getFav(String ticker){
        ImageButton favorite_button = (ImageButton) findViewById(R.id.star_nofill);
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(
                        star_state == 0
                ){
                    // add ticker to the favorite part;
                    favorite_button.setImageResource(R.drawable.ic_baseline_star_24);
                    add_ticker_fav(ticker);
                    star_state = 1;
                }else{
                    favorite_button.setImageResource(R.drawable.ic_baseline_star_border_24);
                    remove_ticker_fav(ticker);
                    star_state = 0;
                }

            }
        });
    }



    void add_ticker_fav(String ticker){
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
        if(sharedPreferences.contains("fav")){
            try {
                jsonArray = new JSONArray(sharedPreferences.getString("fav", "[]"));
                jsonArray.put(ticker);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fav",jsonArray.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            jsonArray.put(ticker);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fav",jsonArray.toString());
            editor.commit();
        }
        Log.i("fav",sharedPreferences.getString("fav","[]"));
        if(sharedPreferences.contains("fav_company_name")) {
            try{
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("fav_company_name",""));
                jsonObject.put(ticker,Company_name);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putString("fav_company_name",jsonObject.toString());
                editor1.commit();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(ticker, Company_name);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putString("fav_company_name", jsonObject.toString());
                editor1.commit();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showMsgAdd(ticker);
        Log.i("fav_name",sharedPreferences.getString("fav_company_name","{}"));
    }


    void remove_ticker_fav(String ticker){
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(sharedPreferences.getString("fav", "[]"));
            for(int i = 0; i < jsonArray.length(); i++){
//                Log.i("fav_re",jsonArray.getString(i));
                if(jsonArray.getString(i).equals(ticker) ){
                    jsonArray.remove(i);

                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fav",jsonArray.toString());
        editor.commit();
        Log.i("fav_re",sharedPreferences.getString("fav", "[]"));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(sharedPreferences.getString("fav_company_name", ""));
            jsonObject.remove(ticker);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putString("fav_company_name",jsonObject.toString());
        editor1.commit();
        Log.i("fav_re_name",sharedPreferences.getString("fav_company_name", "{}"));
        showMsgRemove(ticker);
    };















    void fetch_all_data(String ticker){

        myQueue = Volley.newRequestQueue(this);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());

        fetch_profile(ticker);
        fetch_quote(ticker);
//        fetch_quote_15s(ticker);
        fetch_social(ticker);
        fetch_recommend(ticker);
        fetch_earning(ticker);
        fetch_news(ticker);
        fetch_peers(ticker);
//        fetch_history(ticker);



    }




    private void fetch_quote_15s(String ticker) {
        res_runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    fetch_quote(ticker);
                    Log.i("msg", "15s");
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    fetch_quote_15s(ticker);
                }
            }
        };
        this.res_handler.postDelayed(res_runnable,60000);
    }


    private void getProfileAndPopulate(JSONObject profile){

        String Company_ticker = "";
        String Company_logo = "";

        try {
            Company_name = profile.getString("name");
            tradedialog.setCompany_name(Company_name);
            Company_ticker = profile.getString("ticker");
            tradedialog.setCompany_ticker(Company_ticker);
            Company_logo = profile.getString("logo");
            TextView name_view = findViewById(R.id.company_name);
            TextView ticker_view = findViewById(R.id.company_ticker);
            ImageView logo_view = findViewById(R.id.company_logo);
            Picasso.get().load(Company_logo).resize(68,73).into(logo_view);
            name_view.setText(Company_name);
            ticker_view.setText(Company_ticker);


            // about section

            String ipo = profile.getString("ipo");


            String industry = profile.getString("finnhubIndustry");
            String weburl = profile.getString("weburl");

            Date date1;
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(ipo);
//            long time = Long.parseLong(ipo)*1000;
//            java.sql.Date date = new Date(time);
            SimpleDateFormat simpleformat = new SimpleDateFormat("dd-MM-yyyy");
            String str = simpleformat.format(date1);
            TextView ipo_field = findViewById(R.id.ipo_date);
            TextView industry_field = findViewById(R.id.industry_value);
            TextView web_field = findViewById(R.id.web_page_value);
//            web_field.setClickable(true);
//            web_field.setMovementMethod(LinkMovementMethod.getInstance());
//            web_field.setText(Html.fromHtml(weburl));
            ipo_field.setText(str);
            industry_field.setText(industry);
            web_field.setText(weburl);

            // social section
            TextView social_name = (TextView) findViewById(R.id.social_company);
            social_name.setText(Company_name);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void getSocialAndPopulate(JSONObject social){
        try {
            int reddit_mention = 0;
            int reddit_pos_mention = 0;
            int reddit_neg_mention = 0;
            int twitter_mention = 0;
            int twitter_pos_mention = 0;
            int twitter_neg_mention = 0;
            JSONArray reddit = social.getJSONArray("reddit");
            JSONArray twitter = social.getJSONArray("twitter");
            for(int i = 0;i < reddit.length();i++){
                JSONObject time_interval = reddit.getJSONObject(i);
                reddit_mention = reddit_mention + time_interval.getInt("mention");
                reddit_pos_mention = reddit_pos_mention + time_interval.getInt("positiveMention");
                reddit_neg_mention = reddit_neg_mention + time_interval.getInt("negativeMention");
            }

            for(int i = 0;i < twitter.length();i++){
                JSONObject time_interval_twitter = twitter.getJSONObject(i);
                twitter_mention = twitter_mention + time_interval_twitter.getInt("mention");
                twitter_pos_mention = twitter_pos_mention + time_interval_twitter.getInt("positiveMention");
                twitter_neg_mention = twitter_neg_mention + time_interval_twitter.getInt("negativeMention");
            }
            TextView reddit_m = (TextView) findViewById(R.id.total_reddit);
            TextView reddit_m_p = (TextView) findViewById(R.id.pos_reddit);
            TextView reddit_m_n = (TextView) findViewById(R.id.neg_reddit);

            TextView twitter_m = (TextView) findViewById(R.id.total_twitter);
            TextView twitter_m_p = (TextView) findViewById(R.id.pos_twitter);
            TextView twitter_m_n = (TextView) findViewById(R.id.neg_twitter);

            reddit_m.setText(String.valueOf(reddit_mention));
            reddit_m_p.setText(String.valueOf(reddit_pos_mention));
            reddit_m_n.setText(String.valueOf(reddit_neg_mention));

            twitter_m.setText(String.valueOf(twitter_mention));
            twitter_m_p.setText(String.valueOf(twitter_pos_mention));
            twitter_m_n.setText(String.valueOf(twitter_neg_mention));


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void getQuoteAndPopulate(JSONObject quote){
        try {
            myQuote = new Quote(quote.getDouble("c"),
                    quote.getDouble("d"),
                    quote.getDouble("dp"),
                    quote.getDouble("h"),
                    quote.getDouble("l"),
                    quote.getDouble("o"),
                    quote.getDouble("pc"),
                    quote.getInt("t"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        myQuote.current_price = round2(myQuote.current_price);
        tradedialog.setCurrent_price(myQuote.current_price);
        Log.i("here", String.valueOf(myQuote.change));
        TextView current_price = findViewById(R.id.CurrentPrice);
        String price = "$" + myQuote.current_price;
        current_price.setText(price);
        TextView change = findViewById(R.id.Change);

        String danddp = "$" + round2(myQuote.change) + "(" + round2(myQuote.percentage_change) + "%)";
        change.setText(danddp);

        if(myQuote.change > 0){
            ImageView trend = findViewById(R.id.Trending);
            this.going_up = true;
            change.setTextColor(ContextCompat.getColor(this, R.color.green));
            trend.setImageResource(R.drawable.ic_baseline_trending_up_24);
        }else if(myQuote.change < 0){
            this.going_up = false;
            ImageView trend = findViewById(R.id.Trending);
            change.setTextColor(ContextCompat.getColor(this, R.color.red));
            trend.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }


        // Stats section

        TextView high_price = findViewById(R.id.high_price_num);
        TextView low_price = findViewById(R.id.low_price_num);
        TextView open_price = findViewById(R.id.open_price_num);
        TextView prev_price = findViewById(R.id.previous_price_num);
        String high_price_string = "$" + round2(myQuote.high_price);
        String low_price_string = "$" + round2(myQuote.low_price);
        String open_price_string = "$" + round2(myQuote.open_price);
        String prev_price_string = "$" + round2(myQuote.previous_close);

        high_price.setText(high_price_string);
        low_price.setText(low_price_string);
        open_price.setText(open_price_string);
        prev_price.setText(prev_price_string);

        //port section
        TextView Change = (TextView) findViewById(R.id.Change_value);
        TextView MarketValue = (TextView) findViewById(R.id.Market_Value_value);

        double marketValue = shares_own * myQuote.current_price;
        marketValue = round2(marketValue);
        String doll_marketValue= "$" + marketValue;
        MarketValue.setText(doll_marketValue);
        String doll_change= "$" + round2(myQuote.change);
        Change.setText(doll_change);
        if(myQuote.change < 0){
            Change.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else if(myQuote.change > 0){
            Change.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
        if(round2(shares_own * bought_price) < marketValue){
            MarketValue.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else if(round2(shares_own * bought_price) > marketValue){
            MarketValue.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        tabAdapter.setRequestQueue(myQueue);
        tabAdapter.setTicker(ticker_symobl);

        tabAdapter.setGoing_up(this.going_up);
        tabAdapter.setTime(myQuote.last_stock_time);

    }
    private void getNewsAndPopulate(JSONArray response) {
//        Log.i("jsonArray!!!!!!", String.valueOf(response));
        JSONArray jsonArray = new JSONArray();
        int number = 0;
        for(int i = 0; i < response.length();i ++){
            if(number <= 19){
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    if(jsonObject.getString("source").equals("Nasdaq")){
                        continue;
                    }
                    if(!jsonObject.getString("headline").equals("") &&
                            !jsonObject.getString("datetime").equals("") &&
                            !jsonObject.getString("url").equals("") &&
                            !jsonObject.getString("image").equals("")
                    ){
                        Log.i("here jsonArray", String.valueOf(jsonObject));
                        jsonArray.put(jsonObject);
                        number += 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        newsRecycler = findViewById(R.id.newsRecycler);
        newsRecyclerAdapter newsRecyclerAdapter = new newsRecyclerAdapter(jsonArray);
        newsRecyclerAdapter.setContext(this);
        newsRecycler.setAdapter(newsRecyclerAdapter);
    }
    private void fetch_news(String ticker) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/news/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        getNewsAndPopulate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }



    private void fetch_earning(String ticker) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/earnings/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        getEarningAndPopulate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }

    private void getEarningAndPopulate(JSONArray response) {
        WebView eps_chart = (WebView) findViewById(R.id.eps_chart);
        eps_chart.getSettings().setJavaScriptEnabled(true);
        eps_chart.loadUrl("file:///android_asset/eps_chart.html");
        eps_chart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                JSONArray ticker_json = response;
                Log.i("json", String.valueOf(ticker_json));
                eps_chart.loadUrl("javascript:init_eps('"+ ticker_json + "')");

            }
        });
    }
    private void fetch_peers(String ticker) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/peers/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("json!!!!!", String.valueOf(response));
                        getPeersAndPopulate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }

    private void getPeersAndPopulate(JSONArray response) {

        for(int i = 0; i < response.length(); i ++){
            try {
                if(String.valueOf(response.get(i))!="" || String.valueOf(response.get(i))!=null){

                    String list_item = String.valueOf(response.get(i));
                    peers_list.add( list_item );
                }else{
                    continue;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<TextView> peers_list_view = new ArrayList<>();
        for(int i = 0; i < peers_list.size();i++){
            TextView textView = new TextView(this);
            String list_item = peers_list.get(i);
            TextView textView_space = new TextView(this);
            textView.setText(list_item);
            textView.setTextColor(ContextCompat.getColor(this, R.color.blue));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textView_space.setText(", ");
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String peer = String.valueOf(textView.getText());
                    Intent intent = new Intent(ResultActivity.this, ResultActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, peer);
                    startActivity(intent);
                }
            });
            peers_list_view.add(textView);
            peers_list_view.add(textView_space);
        }
        LinearLayout peer_linear = findViewById(R.id.peers_linear);
        for(int i = 0; i <peers_list_view.size();i ++){
            peer_linear.addView(peers_list_view.get(i));
        }
        Log.i("peers_list", String.valueOf(peers_list));
    }

    private void fetch_social(String ticker) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/social/" + ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getSocialAndPopulate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }
    private void fetch_recommend(String ticker) {
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/recommendation/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Log.i("json!!", String.valueOf(response));
                        getRecommendAndPopulate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }

    private void getRecommendAndPopulate(JSONArray response) {
        WebView recommend = (WebView) findViewById(R.id.recommend_chart);
        recommend.getSettings().setJavaScriptEnabled(true);
        recommend.loadUrl("file:///android_asset/recommendation_chart.html");
        recommend.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                JSONArray ticker_json = response;
//                Log.i("json", String.valueOf(ticker_json));
                recommend.loadUrl("javascript:init_recommend('"+ ticker_json + "')");

            }
        });
    }

    private void fetch_quote(String ticker){
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/quote/" + ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getQuoteAndPopulate(response);
                        fetch_history(ticker);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }

    private void fetch_candle(String ticker) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamp_string = String.valueOf(timestamp.getTime());
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/candle/" + ticker + "/" + timestamp_string;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                            tabAdapter.setCandle(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
    }
    private void fetch_profile(String ticker){
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/profile/" + ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject jsonObject = response.getJSONObject("id");
                        getProfileAndPopulate(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }

    private void fetch_history(String ticker1) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamp_string = String.valueOf(timestamp.getTime());
        Log.i("time", timestamp_string);
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/history/" + ticker1 + "/" + timestamp_string;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        tabAdapter.setHistory(response);
                        viewPager2.setAdapter(tabAdapter);
                        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> {
                            tab.setIcon(tab_icons[position]);
                        }).attach();
//                        tabAdapter.setHistory(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }
    private void showMsgAdd(String msg) {
        Toast toast = Toast.makeText(this, msg + " is added to favorites", Toast.LENGTH_LONG);
        toast.show();
    }

    private void showMsgRemove(String msg) {
        Toast toast = Toast.makeText(this, msg + " is removed from favorites", Toast.LENGTH_LONG);
        toast.show();
    }

    public double round2(double number) {
        double roundOff = Math.round(number * 100.0) / 100.0;
        return roundOff;
    }
    @Override
    protected void onDestroy() {
        Log.i("destroy", "quit");
        res_handler.removeCallbacksAndMessages(null);
        super.onDestroy();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
//        finish();
    }


}