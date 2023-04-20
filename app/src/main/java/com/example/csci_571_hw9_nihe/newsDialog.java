package com.example.csci_571_hw9_nihe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class newsDialog extends AppCompatDialogFragment {
    public void setSource_dialog(String source_dialog) {
        this.source_dialog = source_dialog;
    }

    public void setTime_dialog(String time_dialog) {
        this.time_dialog = time_dialog;
    }

    public void setContent_dialog(String content_dialog) {
        this.content_dialog = content_dialog;
    }

    public void setUrl_dialog(String url_dialog) {
        this.url_dialog = url_dialog;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String source_dialog;
    public String time_dialog;
    public String content_dialog;
    public String url_dialog;
    public String headline;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.news_dialog, null);
        view.setClipToOutline(true);
        
//        AlertDialog.
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView viewSource = view.findViewById(R.id.dialog_source);
        TextView viewTime = view.findViewById(R.id.dialog_time);
        TextView viewContent = view.findViewById(R.id.news_content_dialog);
        long time = Long.parseLong(time_dialog)*1000;
        Date date = new Date(time);
        SimpleDateFormat simpleformat = new SimpleDateFormat("MMMM dd, yyyy");
        String str = simpleformat.format(date);

        viewTime.setText(str);
        ImageView chrome = view.findViewById(R.id.chrome);
        ImageView twitter = view.findViewById(R.id.twitter);
        ImageView facebook = view.findViewById(R.id.facebook);
        chrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chrome = new Intent(Intent.ACTION_VIEW, Uri.parse(url_dialog));
                startActivity(chrome);
                dismiss();
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tweet = new Intent(Intent.ACTION_VIEW);
                tweet.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + headline + " - sources " + url_dialog));
                startActivity(tweet);
                dismiss();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebook = new Intent(Intent.ACTION_VIEW);
                facebook.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u="+ url_dialog));
                startActivity(facebook);
                dismiss();
            }
        });
        viewSource.setText(source_dialog);

        viewContent.setText(content_dialog);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));





        return alertDialog;
    }
}
