package in.ac.iiitd.zenatix.billpredict.MenuActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import in.ac.iiitd.zenatix.billpredict.HistoryActivity;
import in.ac.iiitd.zenatix.billpredict.R;
import in.ac.iiitd.zenatix.billpredict.SettingsActivity;


public class HelpActivity extends ActionBarActivity {

    private static final int MAX_VIEWS = 5;

    ViewPager mViewPager;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new WalkthroughPagerAdapter());
        mViewPager.setOnPageChangeListener(new WalkthroughPageChangeListener());

        context=this;
        Toast.makeText(this,"Keep swiping right",Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_about:
                openAbout();
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

    private void openAbout(){
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    private void openHistory(){
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    class WalkthroughPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_VIEWS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e("walkthrough", "instantiateItem(" + position + ");");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View imageViewContainer = inflater.inflate(R.layout.walkthrough_single_view, null);
            ImageView imageView = (ImageView) imageViewContainer.findViewById(R.id.image_view);

            switch(position) {
                case 0:
                    imageView.setImageResource(R.drawable.help_1);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.help_2);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.help_3);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.help_4);
                    break;

                case 4:
                    imageView.setImageResource(R.drawable.help_5);
                    break;
            }

            ((ViewPager) container).addView(imageViewContainer, 0);
            return imageViewContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }
    }


    class WalkthroughPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            // Here is where you should show change the view of page indicator
            switch(position) {

                case MAX_VIEWS - 1:
                    Toast.makeText(context,"You're ready. Press back to exit.",Toast.LENGTH_LONG).show();
                    break;

                default:

            }

        }

    }
}
