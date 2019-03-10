package ru.rienel.clicker.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;


import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DialogDonut extends DialogFragment {



    private static final String TAG = GameFragment.class.getName();
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_changing, null);

        CarouselPicker carouselPicker = null;

        carouselPicker = view.findViewById(R.id.carousel);

        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.donut_for_game));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.donut_for_game));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.donut_for_game));

        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this.getContext(), imageItems, 0);

        carouselPicker.setAdapter(imageAdapter);


        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}

