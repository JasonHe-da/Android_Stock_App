package com.example.csci_571_hw9_nihe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class buySuccess extends AppCompatDialogFragment {
    private int shares;
    private String company_ticker;

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setCompany_ticker(String company_ticker) {
        this.company_ticker = company_ticker;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.buy_success, null);
        view.setClipToOutline(true);
        builder.setView(view);

        TextView ticker = (TextView)view.findViewById(R.id.ticker_bought);
        TextView shares = (TextView)view.findViewById(R.id.ticker_shares);

        ticker.setText(this.company_ticker);
        shares.setText(String.valueOf(this.shares));
        Button done = (Button) view.findViewById(R.id.done_button);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }
}
