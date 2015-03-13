package in.ac.iiitd.zenatix.billpredict;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.ac.iiitd.zenatix.billpredict.Database.BillPredictDbHelper;
import in.ac.iiitd.zenatix.billpredict.MainTabs.ElectricityFragment;
import in.ac.iiitd.zenatix.billpredict.MainTabs.WaterFragment;
import in.ac.iiitd.zenatix.billpredict.MenuActivities.AboutActivity;
import in.ac.iiitd.zenatix.billpredict.MenuActivities.HelpActivity;
import in.ac.iiitd.zenatix.billpredict.Receivers.ReminderAlarmReceiver;


public class MainTabActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Intent reminderServiceIntent;
    private PendingIntent reminderServicePendingIntent;
    private AlarmManager reminderAlarmMgr;
    private int waterCycleMonthNo=1,electricityCycleMonthNo=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        setDefaultDates(); //In case the user hasn't set them up

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        waterCycleMonthNo=sharedPref.getInt(SettingsActivity.WATER_CYCLE_MONTH_NO,1);
        electricityCycleMonthNo=sharedPref.getInt(SettingsActivity.ELECTRICITY_CYCLE_MONTH_NO,1);
        if(!sharedPref.getBoolean("OPENED_BEFORE",false))
            openSettings();

        setReminderAlarm(); //Set's alarm to remind user of to update reading
        resetCycle();   //Resets cycles and clears DB every 30 days
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }
        else if (id == R.id.action_help) {
            openHelp();
            return true;
        }
        else if (id == R.id.action_about) {
            openAbout();
            return true;
        }
        else if (id == R.id.action_history) {
            openHistory();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment=null;
            switch(position){
                case 0:
                    fragment=new ElectricityFragment();
                    break;
                case 1:
                    fragment=new WaterFragment();
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 0:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    private void setDefaultDates(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Date curDate = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String date=sdf.format(curDate);

        if(sharedPref.getString(SettingsActivity.LAST_DATE_WATER,"").isEmpty()) {
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString(SettingsActivity.LAST_DATE_WATER, date);
            editor.commit();

//            SettingsFragment.setNextDate(sharedPref.getString(SettingsActivity.LAST_DATE_WATER,"")
//                    ,SettingsActivity.NEXT_DATE_WATER,this);
        }
        else
            Log.i(TAG,"Water date already set");

        if(sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,"").isEmpty()) {
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString(SettingsActivity.LAST_DATE_ELECTRICITY, date);
            editor.commit();

//            SettingsFragment.setNextDate(sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,"")
//                    ,SettingsActivity.NEXT_DATE_ELECTRICITY,this);
        }
        else
            Log.i(TAG,"Electricity date already set");


    }

    private void setReminderAlarm(){
        reminderServiceIntent = new Intent(this, ReminderAlarmReceiver.class);
        reminderServiceIntent.setAction("BillPredict.reminderAlarm");
        reminderServiceIntent.putExtra("message", "BillPredict.reminderAlarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                reminderServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT) != null);

        if (alarmUp)
            Log.d(TAG, "Alarm is already active");
        else
            Log.d(TAG, "Alarm is not active");


        reminderServicePendingIntent = PendingIntent.getBroadcast(this,
                4816, reminderServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        reminderAlarmMgr= (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        reminderAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+SettingsActivity.REMINDER_UPLOAD_INTERVAL,
                SettingsActivity.REMINDER_UPLOAD_INTERVAL, reminderServicePendingIntent);

    }

    private void resetCycle(){
        Date curDate=new Date();
        Date startDate= null;
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            BillPredictDbHelper mDbHelper=new BillPredictDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            startDate = sdf.parse(sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,""));
            float lastDateElectricity=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
            electricityCycleMonthNo= ((int) (lastDateElectricity/30))+1;
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putInt(SettingsActivity.ELECTRICITY_CYCLE_MONTH_NO,electricityCycleMonthNo);

            if(lastDateElectricity==30*electricityCycleMonthNo){
                openDialog("Electricity");
//                SettingsFragment.setNextDate(sharedPref.getString(SettingsActivity.LAST_DATE_ELECTRICITY,"")
//                        ,SettingsActivity.NEXT_DATE_ELECTRICITY,this);

//                setNewLastReadingElectricity();
            }
            else
                Log.i(TAG,"During Electricity cycle");

            startDate = sdf.parse(sharedPref.getString(SettingsActivity.LAST_DATE_WATER,""));
            float lastDateWater=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
            waterCycleMonthNo= ((int) (lastDateWater/30))+1;
            editor.putInt(SettingsActivity.WATER_CYCLE_MONTH_NO,waterCycleMonthNo);
            editor.commit();

            if(lastDateWater==30*waterCycleMonthNo){
                openDialog("Water");
//                SettingsFragment.setNextDate(sharedPref.getString(SettingsActivity.LAST_DATE_WATER,"")
//                        ,SettingsActivity.NEXT_DATE_WATER,this);

//                setNewLastReadingWater();
            }
            else
                Log.i(TAG,"During Water cycle");
            } catch (ParseException e) {
            e.printStackTrace();
        }

    }

//    private void setNewLastReadingWater() {
//        BillPredictDbHelper mDbHelper=new BillPredictDbHelper(this);
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        String query = "SELECT MAX" + "(" + DatabaseContract.WaterEntry.METER_READING + ")"
//                + " FROM " + DatabaseContract.WaterEntry.TABLE_NAME;
//
//        Cursor c = db.rawQuery(
//                query, null);
//
//        if (c != null && c.moveToFirst()) {
//            float last = c.getFloat(0);
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor=sharedPref.edit();
//            editor.putString(SettingsActivity.LAST_CYCLE_END_READING_WATER,Float.toString(last));
//            editor.commit();
//        }
//    }

//    private void setNewLastReadingElectricity() {
//        BillPredictDbHelper mDbHelper=new BillPredictDbHelper(this);
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        String query = "SELECT MAX" + "(" + DatabaseContract.ElectricityEntry.METER_READING + ")"
//                + " FROM " + DatabaseContract.ElectricityEntry.TABLE_NAME;
//
//        Cursor c = db.rawQuery(
//                query, null);
//
//        if (c != null && c.moveToFirst()) {
//            float last = c.getFloat(0);
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor=sharedPref.edit();
//            editor.putString(SettingsActivity.LAST_CYCLE_END_READING_ELECTRICITY,Float.toString(last));
//            editor.commit();
//        }
//    }

    private void openSettings(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    private void openHelp(){
        Intent intent = new Intent(this,HelpActivity.class);
        startActivity(intent);
    }

    private void openHistory(){
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    private void openAbout(){
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    private void openDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.monthly_notice)
                .setTitle(title);

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
