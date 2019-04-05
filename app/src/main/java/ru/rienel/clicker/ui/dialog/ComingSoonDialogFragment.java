package ru.rienel.clicker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import ru.rienel.clicker.R;


public class ComingSoonDialogFragment extends DialogFragment {

    private View view;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_coming_soon, null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
