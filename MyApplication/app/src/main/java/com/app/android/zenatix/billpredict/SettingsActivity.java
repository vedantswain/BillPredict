package com.app.android.zenatix.billpredict;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.app.android.zenatix.billpredict.MenuActivities.AboutActivity;
import com.app.android.zenatix.billpredict.MenuActivities.HelpActivity;


public class SettingsActivity extends ActionBarActivity {

    public static final String WATER_CYCLE_MONTH_NO="WATER_CYCLE_MONTH_NO";
    public static final String ELECTRICITY_CYCLE_MONTH_NO="ELECTRICITY_CYCLE_MONTH_NO";
    public static final String LAST_CYCLE_END_READING_WATER="LAST_CYCLE_END_READING_WATER";
    public static final String LAST_CYCLE_END_READING_ELECTRICITY="LAST_CYCLE_END_READING_ELECTRICITY";
    public static final String LAST_DATE_WATER="LAST_DATE_WATER";
    public static final String LAST_DATE_ELECTRICITY="LAST_DATE_ELECTRICITY";
//    public static final String NEXT_DATE_WATER="NEXT_DATE_WATER";
//    public static final String NEXT_DATE_ELECTRICITY="NEXT_DATE_ELECTRICITY";
    public static final float CYCLE_LENGTH =30f;
    public static final long REMINDER_UPLOAD_INTERVAL= AlarmManager.INTERVAL_DAY;//Milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity com AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_help:
                openHelp();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
            case R.id.action_history:
                openHistory();
                return true;
            case R.id.action_average:
                openAverage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openHelp(){
        Intent intent = new Intent(this,HelpActivity.class);
        startActivity(intent);
    }

    private void openAbout(){
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    private void openHistory(){
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    private void openAverage(){
        Intent intent = new Intent(this,DailyAverageActivity.class);
        startActivity(intent);
    }
}
