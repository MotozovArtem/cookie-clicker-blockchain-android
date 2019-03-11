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
import android.support.v4.view.ViewPager;
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
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.donut11));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.apple2));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.burger));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.yellow));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.blue));


        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this.getContext(), imageItems, 0);

        carouselPicker.setAdapter(imageAdapter);


        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, Integer.toString(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}

