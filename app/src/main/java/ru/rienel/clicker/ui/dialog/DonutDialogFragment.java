package ru.rienel.clicker.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameFragment;
import ru.rienel.clicker.activity.main.MainContract;
import ru.rienel.clicker.common.CarouselPicker;
import ru.rienel.clicker.common.ImageDonut;

public class DonutDialogFragment extends DialogFragment {

    private static final String TAG = GameFragment.class.getName();
    private int idSelectDonut;
    private MainContract.View mainContract;
    private Button selectButton;
    private ImageView rightArrow, leftArrow;





    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainContract = (MainContract.View) context;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_changing, null);

        CarouselPicker carouselPicker = null;

        carouselPicker = view.findViewById(R.id.carousel);
        selectButton = view.findViewById(R.id.select_donut);
        leftArrow = view.findViewById(R.id.left_arrow);
        rightArrow = view.findViewById(R.id.right_arrow);


        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.pink_donut_menu));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.apple_menu));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.burger_menu));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.yellow_donut_menu));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.blue_donut_menu));


        setVisibilityArrow(rightArrow, leftArrow, 0, imageItems.size());

        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this.getContext(), imageItems, 0);

        carouselPicker.setAdapter(imageAdapter);


        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                idSelectDonut = position;
                setVisibilityArrow(rightArrow, leftArrow, position, imageItems.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        View.OnClickListener selectListener = v -> {
            for (ImageDonut donut : ImageDonut.values()){
                if (donut.keyForDialog == idSelectDonut) {
                    mainContract.replaceDonut(donut.resourceId);
                    this.dismiss();
                    break;
                }
            }
        };

        CarouselPicker finalCarouselPicker = carouselPicker;
        View.OnClickListener setNextPosition = v -> {
            finalCarouselPicker.setCurrentItem(idSelectDonut + 1);
        };


        View.OnClickListener setAntecedentPosition = v -> {
            finalCarouselPicker.setCurrentItem(idSelectDonut - 1);
        };

        selectButton.setOnClickListener(selectListener);
        rightArrow.setOnClickListener(setNextPosition);
        leftArrow.setOnClickListener(setAntecedentPosition);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }


    public void setVisibilityArrow(ImageView rArrow, ImageView lArrow, int position, int countDonut){
        if (position == 0) lArrow.setVisibility(View.INVISIBLE);
        else lArrow.setVisibility(View.VISIBLE);
        if (position == countDonut - 1) rArrow.setVisibility(View.INVISIBLE);
        else rArrow.setVisibility(View.VISIBLE);
    }
}

