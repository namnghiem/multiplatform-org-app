package com.trinitysmf.mysmf.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.widget.EditText;

/**
 * Created by namnghiem on 06/01/2018.
 */

public class FeedDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setHint("hint");
        alertDialog.setTitle("title");
        alertDialog.setView(input);
        return alertDialog.create();
    }
}
