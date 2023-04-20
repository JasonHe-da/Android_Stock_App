package com.example.csci_571_hw9_nihe;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import android.widget.Toast;
import android.view.MenuItem;
import android.view.Menu;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.JsonHttpResponseHandler;

//import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private TextView mTextViewResult;
    private RequestQueue myQueue;
    private String userInput;
    public static final String local_storage = "myPref";
    public SharedPreferences sharedPreferences;
    private boolean auto_done = false;
    public static boolean reloadNeeded = false;
    private ArrayList<String> auto_sugg_list = new ArrayList<String>();
    RecyclerView mainRecyclerView;
    PortSection portSection = new PortSection("Portfolio",null,0,0);
    List<Section> sectionList = new ArrayList<>();
    Handler handler_port = new Handler(Looper.getMainLooper());
    Handler handler_fav = new Handler(Looper.getMainLooper());
    List<String> sectionOneItems;
    JSONObject fav_name;
    private Runnable mRunnable;
    private Runnable Runnable_port;

    @Override
    public void onResume() {
        super.onResume();
        if (this.reloadNeeded)
            startActivity(getIntent());

        this.reloadNeeded = false;
        // do not reload anymore, unless I tell you so...
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myQueue = Volley.newRequestQueue(this);
        Date today_date = new Date();
        TextView today = (TextView)findViewById(R.id.today_date);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout constraintLayout = findViewById(R.id.Today_date_layout);
                RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
                ConstraintLayout constraintLayout1 = findViewById(R.id.footer_layout);
                constraintLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                constraintLayout1.setVisibility(View.VISIBLE);
                ConstraintLayout pregress = findViewById(R.id.pregress_view);
                pregress.setVisibility(View.GONE);
            }
        }, 2500);
        SimpleDateFormat simpleformat = new SimpleDateFormat(" dd MMMM yyyy");
        String str = simpleformat.format(today_date);
        today.setText(str);
        TextView footer = (TextView)findViewById(R.id.footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chrome = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.finnhub.io/"));
                view.getContext().startActivity(chrome);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(local_storage,
                Context.MODE_PRIVATE);

        iniData();
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(portSection,sectionList);
        mainRecyclerAdapter.setMyHandler_fav(handler_fav);
        mainRecyclerAdapter.setmRunnable_fav(mRunnable);
        mainRecyclerAdapter.setmRunnable_port(Runnable_port);
        mainRecyclerAdapter.setMyHandler_port(handler_port);
        mainRecyclerAdapter.setSharedPreferences(sharedPreferences);
        mainRecyclerAdapter.setFav_company_name(fav_name);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
        mainRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN| ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = viewHolder.getAdapterPosition();
            Collections.swap(sectionOneItems,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            return;
        }
    };
//    private void updateData(int i) {
//        int temp = i + 1;
//        TextView test = findViewById(R.id.port_marketValue);
//
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try{
////                    test.setText(String.valueOf(temp));
//                    Log.i("looper", "here# "+ temp + "times");
//                }finally {
//                    updateData(temp);
//                }
//            }
//        };
//        handler2.postDelayed(mRunnable,7000);
//    }

    public void iniData(){
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
//        sharedPreferences.edit().clear().commit();
        String sectionOneName = "Portfolio";
        sectionOneItems = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        sectionOneItems.add("netWorthAndCash");
        if(sharedPreferences.contains("port")){
            try {
                jsonObject = new JSONObject(sharedPreferences.getString("port",""));
                JSONArray jsonArray = jsonObject.getJSONArray("ticker_info");
                for(int i = 0; i<jsonArray.length();i++){
                    Iterator<String> keys = jsonArray.getJSONObject(i).keys();
                    while (keys.hasNext()){
                        String key = keys.next();
                        sectionOneItems.add(key);
                    }
                }
//                for(int i = 0; i < sectionOneItems.size();i++){
//                    Log.i("here!!!!!!", String.valueOf(sectionOneItems.get(i)));
//                }
                Log.i("here!!!!!!", String.valueOf(jsonArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                jsonObject.put("Cash", 25000.00);
                jsonObject.put("NetWorth", 25000.00);
                jsonObject.put("ticker_info", new JSONArray());
                editor.putString("port",jsonObject.toString());
                editor.commit();
//                Log.i("here!!!!!!",jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        portSection.setSectionName("Portfolio");
        try{
            portSection.setCash(jsonObject.getDouble("Cash"));
            JSONArray jsonArray = jsonObject.getJSONArray("ticker_info");
            if(jsonArray.length() == 0){
                portSection.setNetWorth(jsonObject.getDouble("Cash"));
            }else{
                portSection.setNetWorth(jsonObject.getDouble("NetWorth"));
            }
            portSection.setPortItems(jsonObject.getJSONArray("ticker_info"));
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String sectionTwoName = "Favorites";
        List<String> sectionTwoItems = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        if(sharedPreferences.contains("fav")){
            try {
                jsonArray = new JSONArray(sharedPreferences.getString("fav", "[]"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(sharedPreferences.contains("fav_company_name")){
            try {
                fav_name = new JSONObject(sharedPreferences.getString("fav_company_name", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("here", String.valueOf(jsonArray));
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                sectionTwoItems.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sectionList.add(new Section(sectionOneName,sectionOneItems));
        sectionList.add(new Section(sectionTwoName,sectionTwoItems));
    }
    // create Options Menu bar highly referenced to the youtube video
    // https://www.youtube.com/watch?v=cId3IrdAg7k credit by Technical Skillz
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        };
        menu.findItem(R.id.search_button).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();
        // this line can make search bar expand automatically when user click it!
        searchView.setIconified(false);
        searchView.setQueryHint("Search...");
        // autocomplete popup referenced to https://www.dev2qa.com/android-actionbar-searchview-autocomplete-example/
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userInput = charSequence.toString();
                processing_user_input(userInput, searchAutoComplete);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //referenced to https://developer.android.com/training/basics/firstapp/starting-activity
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {

                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                String[] arrOfStr = queryString.split("\\|", 2);
                String query = arrOfStr[0];
                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(EXTRA_MESSAGE, query);
                Log.i("destroy both", "quit");
                handler_port.removeCallbacksAndMessages(null);
                handler_fav.removeCallbacksAndMessages(null);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showMsg(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return true;
    }

    public void http_request_autoComplete(String input, SearchView.SearchAutoComplete searchAutoComplete){
        String url = "https://nihe-hw8-new.wl.r.appspot.com/search/autocomplete/" + input;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            auto_sugg_list.clear();
                            JSONArray arr = response.getJSONArray("result");
                            for(int i = 0; i < arr.length(); i ++){
                                String company_des = arr.getJSONObject(i).getString("description");
                                String company_symbol = arr.getJSONObject(i).getString("symbol");
                                String auto_suggest = company_symbol + "|" + company_des;
                                auto_sugg_list.add(auto_suggest);

                            }
                            String objects[] = auto_sugg_list.toArray(new String[auto_sugg_list.size()]);

                            ArrayAdapter<String> arrayAdapter;
                            arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,objects);
                            arrayAdapter.notifyDataSetChanged();
                            for(int d = 0; d < arrayAdapter.getCount() ; d ++){
                                Log.i("here",arrayAdapter.getItem(d) );
                            }

                            searchAutoComplete.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myQueue.add(request);
    }
    public void processing_user_input(String input, SearchView.SearchAutoComplete searchAutoComplete){
        http_request_autoComplete(input,searchAutoComplete);

    }
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void showMsg(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        Log.i("destroy", "quit");
        handler_port.removeCallbacksAndMessages(null);
        handler_fav.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}