package com.example.csci_571_hw9_nihe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import android.os.Handler;
import android.widget.Toast;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

// this Recycler View referenced to video https://www.youtube.com/watch?v=x5afKIu0JmY&list=WL&index=2&t=170s
// credit by youtuber yoursTRULY
public class MainRecyclerAdapter extends RecyclerView.Adapter implements RecyclerViewClickInterface{
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    PortSection portSection;
    List<Section> sectionList;
    Handler myHandler_fav;
    Handler myHandler_port;
    List<String> items;
    List<String> items_port;
    ChildRecyclerAdapter childRecyclerAdapter;
    public SharedPreferences sharedPreferences;

    public void setFav_company_name(JSONObject fav_company_name) {
        this.fav_company_name = fav_company_name;
    }

    public JSONObject fav_company_name;
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setmRunnable_fav(Runnable mRunnable_fav) {
        this.mRunnable_fav = mRunnable_fav;
    }

    Runnable mRunnable_fav;

    public void setmRunnable_port(Runnable mRunnable_port) {
        this.mRunnable_port = mRunnable_port;
    }

    Runnable mRunnable_port;
    public void setMyHandler_port(Handler myHandler_port) {
        this.myHandler_port = myHandler_port;
    }

    public void setMyHandler_fav(Handler myHandler_fav) {
        this.myHandler_fav = myHandler_fav;
    }


    private static int Port = 1;
    private static int Fav = 2;
    public MainRecyclerAdapter(PortSection portSection, List<Section> sectionList) {
        this.portSection = portSection;
        this.sectionList = sectionList;
    }

    public MainRecyclerAdapter(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return Port;
        }else{
            return Fav;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == Port){
            view = layoutInflater.inflate(R.layout.section_row,parent, false);
            return new ViewHolder_port(view);
        }else{
            view = layoutInflater.inflate(R.layout.section_row,parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            // bind port view
            Section section = sectionList.get(0);
            ViewHolder_port viewHolder_port = (ViewHolder_port) holder;
            PortSection portSection_temp = this.portSection;
            String sectionName = portSection_temp.getSectionName();
            items_port = section.getSectionItems();
            JSONArray items = portSection_temp.getPortItems();
            double cash = portSection_temp.getCash();
            double netWorth = portSection_temp.getNetWorth();
            viewHolder_port.sectionNameTextView.setText(sectionName);
            viewHolder_port.sectionNameTextView.setBackgroundColor(ContextCompat.getColor(viewHolder_port.sectionNameTextView.getContext(), R.color.light_grey));

            PortfolioRecyclerAdapter portfolioRecyclerAdapter = new PortfolioRecyclerAdapter(cash, netWorth, items, this);
            portfolioRecyclerAdapter.setHandler_port(this.myHandler_port);
            portfolioRecyclerAdapter.setRunnable_port(this.mRunnable_port);
            List<String> items_port = section.getSectionItems();
            portfolioRecyclerAdapter.setTicker_keys(items_port);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback_port);
            itemTouchHelper.attachToRecyclerView(viewHolder_port.portRecyclerView);
            viewHolder_port.portRecyclerView.setAdapter(portfolioRecyclerAdapter);
            viewHolder_port.portRecyclerView.addItemDecoration(new DividerItemDecoration(viewHolder_port.sectionNameTextView.getContext(), DividerItemDecoration.VERTICAL));
        }
        if(position == 1){
            // fav_part
            ViewHolder viewHolder = (ViewHolder) holder;
            Section section = sectionList.get(1);
            String sectionName = section.getSectionName();
            items = section.getSectionItems();
            viewHolder.sectionNameTextView.setText(sectionName);
            viewHolder.sectionNameTextView.setBackgroundColor(ContextCompat.getColor(viewHolder.sectionNameTextView.getContext(), R.color.light_grey));
            childRecyclerAdapter = new ChildRecyclerAdapter(items, this);
            childRecyclerAdapter.setmHandler(this.myHandler_fav);
            childRecyclerAdapter.setmRunnable(this.mRunnable_fav);
            childRecyclerAdapter.setFav_company_name(this.fav_company_name);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback_fav);
            itemTouchHelper.attachToRecyclerView(viewHolder.childRecyclerView);
            viewHolder.childRecyclerView.setAdapter(childRecyclerAdapter);
            viewHolder.childRecyclerView.addItemDecoration(new DividerItemDecoration(viewHolder.sectionNameTextView.getContext(), DividerItemDecoration.VERTICAL));

        }

    }

    // https://www.youtube.com/watch?v=rcSNkSJ624U&list=RDCMUCr0y1P0-zH2o3cFJyBSfAKg&index=5 referenecd to yoursTRULY youtube channel
    ItemTouchHelper.SimpleCallback simpleCallback_fav = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getBindingAdapterPosition();
            int toPosition = target.getBindingAdapterPosition();
//            SharedPreferences sharedPreferences = MainActivity.getSharedPreferences("myPref",MainActivity.MODE_PRIVATE);
            Collections.swap(items,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.i("swiped", " spwide");
            int position = viewHolder.getBindingAdapterPosition();
            items.remove(position);
            try {
                JSONArray jsonArray = new JSONArray(sharedPreferences.getString("fav", "[]"));
                JSONArray jsonArray1 = new JSONArray(String.valueOf(items));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fav",jsonArray1.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            childRecyclerAdapter.notifyItemRemoved(position);
            return;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerView.getContext() , R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        }
    };
    ItemTouchHelper.SimpleCallback simpleCallback_port = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getBindingAdapterPosition();
            int toPosition = target.getBindingAdapterPosition();
            if(toPosition == 0){
                return false;
            }else{
                Collections.swap(items_port,fromPosition,toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            }
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            return;
        }
    };
    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onItemClick(int position, View view) {
        Intent intent = new Intent(view.getContext(), ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, items.get(position));
        Log.i("destroy both", "quit");
        myHandler_fav.removeCallbacksAndMessages(null);
        myHandler_port.removeCallbacksAndMessages(null);
        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemClick_port(int position, View view) {
        Intent intent = new Intent(view.getContext(), ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, items_port.get(position));
        Log.i("destroy both", "quit");
        myHandler_fav.removeCallbacksAndMessages(null);
        myHandler_port.removeCallbacksAndMessages(null);
        view.getContext().startActivity(intent);
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sectionNameTextView;
        RecyclerView childRecyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionNameTextView = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }

    class ViewHolder_port extends RecyclerView.ViewHolder{

        TextView sectionNameTextView;
        RecyclerView portRecyclerView;
        public ViewHolder_port(@NonNull View itemView) {
            super(itemView);
            sectionNameTextView = itemView.findViewById(R.id.sectionNameTextView);
            portRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }

}
