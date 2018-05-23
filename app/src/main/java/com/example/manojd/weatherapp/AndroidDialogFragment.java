package com.example.manojd.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class AndroidDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sorry!!")
                .setMessage("There was an error, please try again!")
                .setPositiveButton("OK",null);
        return builder.create();
    }
}
