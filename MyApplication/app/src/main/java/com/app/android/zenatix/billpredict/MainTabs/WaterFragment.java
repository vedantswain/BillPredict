package com.app.android.zenatix.billpredict.MainTabs;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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

import com.app.android.zenatix.billpredict.CustomUI.MeterView;
import com.app.android.zenatix.billpredict.Database.BillPredictDbHelper;
import com.app.android.zenatix.billpredict.Database.DatabaseContract;
import com.app.android.zenatix.billpredict.R;
import com.app.android.zenatix.billpredict.RequestTasks.StoreTask;
import com.app.android.zenatix.billpredict.SettingsActivity;
import com.app.android.zenatix.billpredict.TaskCompletedListeners.StoreCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaterFragment extends Fragment implements StoreCompleteListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    MeterView waterMeterView;
    EditText waterEditText;
    Button waterBtn;
    TextView waterEstimate,billNote;
    Float lastCycleReading= -1f,scale=30f,monthScale=30f;
    int waterCycleMonthNo=1;
    String lastCycleDate="";
    long lastCycleID=-1;

    BillPredictDbHelper mDbHelper;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG="WaterFragment";
    private final String TYPE="water";
    private String location;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaterFragment newInstance(String param1, String param2) {
        WaterFragment fragment = new WaterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WaterFragment() {
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
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG,"Building api");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void setupLocation(){
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.v(TAG,"finding location");
        if (mLastLocation != null) {
            Log.v(TAG,"last location");
            location=String.valueOf(mLastLocation.getLatitude())+","+String.valueOf(mLastLocation.getLongitude());
            storeReading(lastCycleReading, lastCycleDate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView=inflater.inflate(R.layout.fragment_water, container, false);

        waterMeterView=(MeterView)inflateView.findViewById(R.id.waterMeterView);
        waterMeterView.setTitle(getString(R.string.title_section1));
        waterMeterView.setUnit("kL");

        waterEditText=(EditText)inflateView.findViewById((R.id.waterEditText));
        waterEstimate=(TextView)inflateView.findViewById(R.id.waterEstimate);
        billNote=(TextView)inflateView.findViewById(R.id.billNote);
        waterBtn=(Button)inflateView.findViewById(R.id.waterBtn);
        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReading();
            }
        });

        return inflateView;
    }

    @Override
    public void onPause(){
        super.onResume();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        mGoogleApiClient.connect();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        waterCycleMonthNo=sharedPref.getInt(SettingsActivity.WATER_CYCLE_MONTH_NO,1);
        scale=scale*waterCycleMonthNo;

        waterMeterView.setScale(scale);
        waterMeterView.setMonthScale(monthScale);

        lastCycleDate=sharedPref.getString(SettingsActivity.LAST_DATE_WATER,"");
        try {
            lastCycleReading = Float.parseFloat(sharedPref.getString(SettingsActivity.LAST_CYCLE_END_READING_WATER, ""));
            if(!lastCycleDate.isEmpty()) {
                insert(lastCycleReading, lastCycleDate);
            }
            if(getId(lastCycleDate)<0) {
                insert(lastCycleReading, lastCycleDate);
            }

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

        String query = "SELECT MAX" + "(" + DatabaseContract.WaterEntry.METER_READING + ")"
                + " FROM " + DatabaseContract.WaterEntry.TABLE_NAME;

        Cursor c = db.rawQuery(
                query, null);

        if (c != null && c.moveToFirst()) {
            float consumption=
                    c.getFloat(0)
                            -lastCycleReading;
            if(consumption>=0) {
                Float prediction = getPrediction(consumption);
                waterMeterView.setProgress((consumption / scale) * 100);
                waterMeterView.setTarget((prediction / (monthScale)) * 100);
                waterEstimate.setText("Rs." + Integer.toString((int)getEstimate(prediction)));
            }
            else
                waterEstimate.setText("Rs." + 0);
        }
    }

    private void setReading(){
        String reading=waterEditText.getText().toString();
        if(reading==null||reading.isEmpty())
            Toast.makeText(getActivity(), "Field is invalid", Toast.LENGTH_SHORT);
        else{
            float mReading=Float.parseFloat(reading);
            float consumption=mReading-lastCycleReading;
            Date curDate = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String date=sdf.format(curDate);

            if(consumption>=0) {
                storeReading(mReading, date);
                insert(mReading, date);
            }

            float mPrediction=getPrediction(consumption);

            if(lastCycleReading>-1 && consumption>=0) {
                waterMeterView.setProgress((consumption / scale) * 100);
                waterMeterView.setTarget((mPrediction / (monthScale)) * 100);
                waterEstimate.setText("Rs." + String.format("%.2f", getEstimate(mPrediction)));
            }
            else{
                openDialog("You will see your consumption and estimate " +
                        "after you update the next day's meter reading.\n" +
                        "Or you could update your previous cycle's meter reading com the settings");
            }

            waterEditText.setText("");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(waterEditText.getWindowToken(), 0);

        }
    }

    private void storeReading(float mReading,String date){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cno_water=sharedPref.getString(SettingsActivity.CUSTOMER_NO_WATER, "");

        if(cno_water.isEmpty()){
            Toast.makeText(getActivity(),"Fill in customer details in settings",Toast.LENGTH_SHORT);
        }
        else {
            (new StoreTask(cno_water, TYPE, mReading, date, lastCycleReading, lastCycleDate, location,this)).execute();
        }
    }

    public long insert(float meterReading,String readingDate){
        // Gets the data repository com write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WaterEntry.METER_READING, meterReading);
        values.put(DatabaseContract.WaterEntry.READING_DATE, readingDate);
        values.put(DatabaseContract.WaterEntry.CYCLE_START_ID, lastCycleID);

        Log.v(TAG,"Inserted with: "+lastCycleID);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("LAST_UPDATE_WATER",readingDate);
        if(lastCycleReading==-1)
            editor.putString(SettingsActivity.LAST_CYCLE_END_READING_WATER,Float.toString(meterReading));
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
                DatabaseContract.WaterEntry.TABLE_NAME,
                null,
                values);

        Log.d(TAG,"Entry inserted");
        return newRowId;
    }

    public long update(long rowId,float meterReading){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WaterEntry.METER_READING, meterReading);
        values.put(DatabaseContract.WaterEntry.CYCLE_START_ID,lastCycleID);

        // Which row to update, based on the ID
        String selection = DatabaseContract.WaterEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        int count = db.update(
                DatabaseContract.WaterEntry.TABLE_NAME,
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
                DatabaseContract.WaterEntry._ID,
        };
        String selection = DatabaseContract.WaterEntry.READING_DATE + " LIKE ?";
        String[] selectionArgs = { readingDate };

        Cursor c = db.query(
                DatabaseContract.WaterEntry.TABLE_NAME,  // The table to query
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

            float cycle=SettingsActivity.CYCLE_LENGTH;

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Date curDate=new Date();
            SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity());
            try {
                Date startDate=sdf.parse(sharedPref.getString(SettingsActivity.LAST_DATE_WATER,""));
                float factor=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
                factor++;
                waterMeterView.setDays((int)factor);
                Log.v(TAG,"Equation: ("+max+"/"+factor+")*"+cycle+"="+((max/factor)*cycle));
                return (max/factor)*cycle;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
    }

    public float getEstimate(float prediction){
        float estimate=0,tariff1=2.66f,tariff2=3.99f,tariff3=19.97f,tariff4=33.28f;
        float slab1=10,slab2=20,slab3=30;

        if(prediction<=slab1)
            estimate=prediction*tariff1;
        else if(prediction<=slab2)
            estimate=(slab1*tariff1)+(prediction-slab1)*tariff2;
        else if(prediction<=slab3)
            estimate=(slab1*tariff1)+(slab2*tariff2)+(prediction-slab2)*tariff3;
        else
            estimate=(slab1*tariff1)+(slab2*tariff2)+(slab3*tariff3)+(prediction-slab3)*tariff4;

        if(prediction<=slab2)
            billNote.setText("Your bill will be subsidised to Rs.0");
        else
            billNote.setText("You miss the subsidy by "+Integer.toString((int)(prediction-slab2))+" units");

        Log.v(TAG,"Estimate: "+estimate);
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

    @Override
    public void onStoreComplete(String msg) {
        Log.v(TAG,msg);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG,"Connection Success");
        setupLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG,"Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG,"Connection Failed");
    }
}
