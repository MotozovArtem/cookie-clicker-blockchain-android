package ru.rienel.clicker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import ru.rienel.clicker.R;

import static ru.rienel.clicker.common.Configuration.SharedPreferencesKeys.PREFERENCES_NAME;
import static ru.rienel.clicker.common.Configuration.SharedPreferencesKeys.PREFERENCES_VOLUME_EFFECT;
import static ru.rienel.clicker.common.Configuration.SharedPreferencesKeys.PREFERENCES_VOLUME_MUSIC;


public class SettingsDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private SharedPreferences cookieSettings;
    private SeekBar volumeE, volumeM;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_settings, null);

        volumeM = (SeekBar)view.findViewById(R.id.seekBarM);
        volumeE = (SeekBar)view.findViewById(R.id.seekBarE);

        cookieSettings = view.getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);



        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float noiseVol = (float) progress / (float) volumeM.getMax();
//        if (seekBar.getId() == R.id.seekBarM)
//        {
            SharedPreferences.Editor editor = cookieSettings.edit();
            editor.putFloat(PREFERENCES_VOLUME_MUSIC, noiseVol);
            editor.apply();
//        }
//        else
//        {
//            SharedPreferences.Editor editor = cookieSettings.edit();
//            editor.putFloat(PREFERENCES_VOLUME_EFFECT, noiseVol);
//            editor.apply();
//        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
