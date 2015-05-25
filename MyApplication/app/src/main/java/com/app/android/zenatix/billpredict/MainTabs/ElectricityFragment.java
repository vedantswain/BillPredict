package com.app.android.zenatix.billpredict.MainTabs;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.app.android.zenatix.billpredict.Database.BillPredictDbHelper;
import com.app.android.zenatix.billpredict.Database.DatabaseContract;
import com.app.android.zenatix.billpredict.CustomUI.MeterView;
import com.app.android.zenatix.billpredict.R;
import com.app.android.zenatix.billpredict.SettingsActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElectricityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElectricityFragment extends Fragment {
    MeterView electricityMeterView;
    EditText electricityEditText;
    Button electricityBtn;
    TextView electricityEstimate,billNote;
    Float lastCycleReading= -1f,scale=600f,monthScale=600f;
    int electricityCycleMonthNo=1;
    String lastCycleDate="";

    BillPredictDbHelper mDbHelper;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG="ElectricityFragment";
    private long lastCycleID=-1;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ElectricityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElectricityFragment newInstance(String param1, String param2) {
        ElectricityFragment fragment = new ElectricityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ElectricityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDbHelper = new BillPredictDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // Inflate the layout for this fragment
       View inflateView=inflater.inflate(R.layout.fragment_electricity, container, false);

        electricityMeterView=(MeterView)inflateView.findViewById(R.id.electricityMeterView);
        electricityMeterView.setTitle(getString(R.string.title_section2));
        electricityMeterView.setUnit("kWh");

        electricityEditText=(EditText)inflateView.findViewById((R.id.electricityEditText));
        electricityEstimate=(TextView)inflateView.findViewById(R.id.electricityEstimate);
        billNote=(TextView)inflateView.findViewById(R.id.billNote);
        electricityBtn=(Button)inflateView.findViewById(R.id.electricityBtn);
        electricityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReading();
            }
        });

        return inflateView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        electricityCycleMonthNo=sharedPref.getInt(SettingsActivity.ELECTRICITY_CYCLE_MONTH_NO,1);

        scale=scale*electricityCycleMonthNo;

        electricityMeterView.setScale(scale);
        electricityMeterView.setMonthScale(monthScale);

        lastCycleDate=sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,"");
        try {
            lastCycleReading = Float.parseFloat(sharedPref.getString(SettingsActivity.LAST_CYCLE_END_READING_ELECTRICITY, ""));
            if(!lastCycleDate.isEmpty())
                insert(lastCycleReading,lastCycleDate);
            if(getId(lastCycleDate)<0)
                insert(lastCycleReading,lastCycleDate);

            lastCycleID=getId(lastCycleDate);
        }
        catch(NumberFormatException e){
            e.printStackTrace();
        }
        finally {
            if(lastCycleReading>-1)
                setStoredReading();
        }
    }

    private void setStoredReading(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT MAX" + "(" + DatabaseContract.ElectricityEntry.METER_READING + ")"
                + " FROM " + DatabaseContract.ElectricityEntry.TABLE_NAME;

        Cursor c = db.rawQuery(
                query, null);

        if (c != null && c.moveToFirst()) {
            float consumption=c.getFloat(0)-lastCycleReading;
            if(consumption>=0) {
                Float prediction = getPrediction(consumption);
                electricityMeterView.setProgress((consumption / scale) * 100);
                electricityMeterView.setTarget((prediction / (monthScale)) * 100);
                electricityEstimate.setText("Rs." + Integer.toString((int) getEstimate(prediction)));
            }
            else
                electricityEstimate.setText("Rs." + 0);
            }
        }

    private void setReading(){
        String reading=electricityEditText.getText().toString();
        if(reading==null||reading.isEmpty())
            Toast.makeText(getActivity(), "Field is invalid", Toast.LENGTH_SHORT);
        else{
            float mReading=Float.parseFloat(reading);
            float consumption=mReading-lastCycleReading;
            Date curDate = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String date=sdf.format(curDate);

            if(consumption>=0)
                insert(mReading,date);

            float mPrediction=getPrediction(consumption);

            if(lastCycleReading>-1 && consumption>=0) {
                electricityMeterView.setProgress((consumption / scale) * 100);
                electricityMeterView.setTarget((mPrediction / (monthScale)) * 100);
                electricityEstimate.setText("Rs." + String.format("%.2f", getEstimate(mPrediction)));
            }
            else{
                openDialog("You will see your consumption and estimate " +
                        "after you update the next day's meter reading.\n" +
                        "Or you could update your previous cycle's meter reading com the settings");
            }

            electricityEditText.setText("");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(electricityEditText.getWindowToken(), 0);

            }
    }


    public long insert(float meterReading,String readingDate){
        // Gets the data repository com write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ElectricityEntry.METER_READING, meterReading);
        values.put(DatabaseContract.ElectricityEntry.READING_DATE, readingDate);
        values.put(DatabaseContract.ElectricityEntry.CYCLE_START_ID, lastCycleID);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("LAST_UPDATE_ELECTRICITY",readingDate);
        if(lastCycleReading==-1)
            editor.putString(SettingsActivity.LAST_CYCLE_END_READING_ELECTRICITY,Float.toString(meterReading));
        editor.commit();

        long oldRowId=getId(readingDate);
        if(oldRowId>0){
            Log.d(TAG,"Entry updated");
            if(update(oldRowId,meterReading)>0)
                return oldRowId;
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId=0;
        newRowId = db.insert(
                DatabaseContract.ElectricityEntry.TABLE_NAME,
                null,
                values);

        Log.d(TAG,"Entry inserted");
        return newRowId;
    }

    public long update(long rowId,float meterReading){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ElectricityEntry.METER_READING, meterReading);
        values.put(DatabaseContract.ElectricityEntry.CYCLE_START_ID,lastCycleID);

        // Which row to update, based on the ID
        String selection = DatabaseContract.ElectricityEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        int count = db.update(
                DatabaseContract.ElectricityEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if(count>0)
            return rowId;
        else
            return -1;
    }

    public long getId(String readingDate){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projection = {
                DatabaseContract.ElectricityEntry._ID,
        };
        String selection = DatabaseContract.ElectricityEntry.READING_DATE + " LIKE ?";
        String[] selectionArgs = { readingDate };

        Cursor c = db.query(
                DatabaseContract.ElectricityEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      //don't order  
        );

//        if(c!=null && c.moveToFirst()){
//            while(true){
//                Log.d(TAG,"Entry: "+c.getLong(0));
//                if(c.isLast())
//                    break;
//                c.move(1);
//            }
//        }

        if (c != null && c.moveToFirst()) {
            return c.getLong(0);
        }
        else
            return -1;
    }

    public float getPrediction(float max) {
            float cycle= SettingsActivity.CYCLE_LENGTH;

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Date curDate=new Date();
            SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity());
            try {
                Date startDate=sdf.parse(sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,""));
                float factor=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
                factor++;
                Log.v(TAG,"Equation: ("+max+"/"+factor+")*"+cycle);
                electricityMeterView.setDays((int)factor);
                return (max/factor)*cycle;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

    }

    public float getEstimate(float prediction){
        float estimate=0,tariff1=4f,tariff2=5.95f,tariff3=7.30f;
        float slab1=200,slab2=400;

        if(prediction<=slab1)
            estimate=prediction*tariff1;
        else if(prediction<=slab2)
            estimate=(slab1*tariff1)+(prediction-slab1)*tariff2;
        else
            estimate=(slab1*tariff1)+(slab2*tariff2)+(prediction-slab2)*tariff3;

        if(prediction<=slab2)
            billNote.setText("Your bill will be subsidised by 50%");
        else
            billNote.setText("You miss the subsidy by "+Integer.toString((int)(prediction-slab2))+" units");

        return estimate;
    }

    private void openDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
