package in.ac.iiitd.zenatix.billpredict.MenuActivities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import in.ac.iiitd.zenatix.billpredict.HistoryActivity;
import in.ac.iiitd.zenatix.billpredict.R;
import in.ac.iiitd.zenatix.billpredict.SettingsActivity;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_help:
                openHelp();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_history:
                openHistory();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

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
}
