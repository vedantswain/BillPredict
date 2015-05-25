package com.app.android.zenatix.billpredict;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.app.android.zenatix.billpredict.Database.BillPredictDbHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    private static final String TAG = "SettingsFragment";
    BillPredictDbHelper mDbHelper;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!sharedPref.getBoolean("OPENED_BEFORE", false))
            openDialog();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("OPENED_BEFORE", true);
        editor.commit();

        mDbHelper = new BillPredictDbHelper(getActivity());

        setReadingValidation(SettingsActivity.LAST_CYCLE_END_READING_WATER);
        setReadingValidation(SettingsActivity.LAST_CYCLE_END_READING_ELECTRICITY);
        setDateValidation(SettingsActivity.LAST_DATE_ELECTRICITY);
        setDateValidation(SettingsActivity.LAST_DATE_WATER);

    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.welcome_dialog_message)
                .setTitle(R.string.welcome_dialog_title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setDateValidation(final String lastDateKey) {
        findPreference(lastDateKey).setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString() == null) {
                            return false;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        sdf.setLenient(false);

                        try {
                            //if not valid, it will throw ParseException
                            Date date = sdf.parse(newValue.toString());
//                            System.out.println(date);
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor=sharedPref.edit();

                            switch (lastDateKey){
                                case SettingsActivity.LAST_DATE_ELECTRICITY:
                                    editor.putInt(SettingsActivity.ELECTRICITY_CYCLE_MONTH_NO,1);
                                    break;
                                case SettingsActivity.LAST_DATE_WATER:
                                    editor.putInt(SettingsActivity.WATER_CYCLE_MONTH_NO,1);
                                    break;
                            }

                            editor.commit();

                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Please follow format yyyy-MM-dd", Toast.LENGTH_LONG).show();
                            return false;
                        }
//                        setNextDate(newValue.toString(), nextDateKey, getActivity());
                        return true;
                    }
                });
    }

//    public static void setNextDate(String lastDate, String nextDateKey, Context context) {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date startCycle = sdf.parse(lastDate);
//            Date endCycle = new Date(startCycle.getTime() + SettingsActivity.CYCLE_LENGTH * 24 * 60 * 60 * 1000);
//            editor.putString(nextDateKey, sdf.format(endCycle));
//            editor.commit();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    private void setReadingValidation(final String lastReadingKey) {
        findPreference(lastReadingKey).setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue == null) {
                            Toast.makeText(getActivity(), "Please enter a valid value", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        return true;
                    }
                });
    }

}
