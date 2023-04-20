package com.example.csci_571_hw9_nihe;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class newsRecyclerAdapter extends RecyclerView.Adapter{

    private static int first_row = 1;
    private static int else_row = 2;
    FirstRowNews firstRowNews;
    ElseRowsNews elseRowsNews;
    JSONArray news_list;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public newsDialog newsDialog = new newsDialog();

    public newsRecyclerAdapter(JSONArray news_list) {
        this.news_list = news_list;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return first_row;
        }else{
            return else_row;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == first_row){
            view = layoutInflater.inflate(R.layout.first_news_row,parent, false);
            return new newsRecyclerAdapter.FirstRowNews(view);
        }else{
            view = layoutInflater.inflate(R.layout.news_row,parent, false);
            return new newsRecyclerAdapter.ElseRowsNews(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            firstRowNews = (FirstRowNews) holder;
            try {
                JSONObject jsonObject = news_list.getJSONObject(position);
                String title = jsonObject.getString("headline");
                String source = jsonObject.getString("source");
                String time = jsonObject.getString("datetime");
                String image = jsonObject.getString("image");
                String summary = jsonObject.getString("summary");
                String url = jsonObject.getString("url");
                long unixTime = Instant.now().getEpochSecond();
                int report_time = Integer.parseInt(time);
                int timePassd = Math.round((unixTime - report_time) / 3600);
                String hour_before;
                if(timePassd <= 0){
                    hour_before = "less than one hour ago";
                }else if(timePassd == 1){
                    hour_before = "1 hour ago";
                }else{
                    hour_before = timePassd + " hours ago";
                }
                ((FirstRowNews) holder).content.setText(title);
                ((FirstRowNews) holder).source.setText(source);
                ((FirstRowNews) holder).report_time.setText(hour_before);
//                Log.i("hereF", image);
                Picasso.get().load(image).resize(370,190).into(((FirstRowNews) holder).first_row_image);

                View viewById = holder.itemView.findViewById(R.id.first_row);
                viewById.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsDialog.setContent_dialog(summary);
                        newsDialog.setTime_dialog(time);
                        newsDialog.setSource_dialog(source);
                        newsDialog.setUrl_dialog(url);
                        newsDialog.setHeadline(title);
                        openDialog(viewById);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            elseRowsNews = (ElseRowsNews) holder;
            try {
                JSONObject jsonObject = news_list.getJSONObject(position);
                String title = jsonObject.getString("headline");
                String source = jsonObject.getString("source");
                String time = jsonObject.getString("datetime");
                String image = jsonObject.getString("image");
                String summary = jsonObject.getString("summary");
                String url = jsonObject.getString("url");
                long unixTime = Instant.now().getEpochSecond();
                int report_time = Integer.parseInt(time);
                int timePassd = Math.round((unixTime - report_time) / 3600);
                String hour_before;
                if(timePassd <= 0){
                    hour_before = "less than one hour ago";
                }else if(timePassd == 1){
                    hour_before = "1 hour ago";
                }else{
                    hour_before = timePassd + " hours ago";
                }
                ((ElseRowsNews) holder).content.setText(title);
                ((ElseRowsNews) holder).source.setText(source);
                ((ElseRowsNews) holder).report_time.setText(hour_before);
                Picasso.get().load(image).resize(102,99).into(((ElseRowsNews) holder).else_row_image);
                View viewById = holder.itemView.findViewById(R.id.else_row);
                viewById.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsDialog.setContent_dialog(summary);
                        newsDialog.setTime_dialog(time);
                        newsDialog.setSource_dialog(source);
                        newsDialog.setUrl_dialog(url);
                        newsDialog.setHeadline(title);
                        openDialog(viewById);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDialog(View view) {
        newsDialog.show((((AppCompatActivity)context).getSupportFragmentManager()),"news dialog");
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class FirstRowNews extends RecyclerView.ViewHolder{

        TextView source;
        TextView report_time;
        TextView content;
        ImageView first_row_image;
        public FirstRowNews(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.first_row_title);
            report_time = itemView.findViewById(R.id.first_news_time);
            content = itemView.findViewById(R.id.first_row_content);
            first_row_image = itemView.findViewById(R.id.first_news_image);
        }
    }

    class ElseRowsNews extends RecyclerView.ViewHolder{

        TextView source;
        TextView report_time;
        TextView content;
        ImageView else_row_image;
        public ElseRowsNews(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.source);
            report_time = itemView.findViewById(R.id.time_pass);
            content = itemView.findViewById(R.id.news_title);
            else_row_image = itemView.findViewById(R.id.news_image);
        }
    }


}
