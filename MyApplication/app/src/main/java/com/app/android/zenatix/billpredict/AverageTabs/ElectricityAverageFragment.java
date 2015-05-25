package com.app.android.zenatix.billpredict.AverageTabs;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.app.android.zenatix.billpredict.DailyAverageActivity;
import com.app.android.zenatix.billpredict.Database.BillPredictDbHelper;
import com.app.android.zenatix.billpredict.Database.DatabaseContract;
import com.app.android.zenatix.billpredict.Database.DbEntryObject;
import com.app.android.zenatix.billpredict.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElectricityAverageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElectricityAverageFragment extends Fragment {

    static Context context;
    static View rootView;

    static XYMultipleSeriesRenderer mRenderer;
    static LinearLayout chart_container;
    static TimeSeries mSeries;

    BillPredictDbHelper mDbHelper;
    ArrayList<DbEntryObject> cycleDates=new ArrayList<DbEntryObject>();

    private static final String TAG="EAverageFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ElectricityAverageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElectricityAverageFragment newInstance(String param1, String param2) {
        ElectricityAverageFragment fragment = new ElectricityAverageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ElectricityAverageFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_electricity_average, container, false);
        context=getActivity();
        setupRenderer();
        getCycleEntries();
        drawChart(mSeries);

        return rootView;
    }

    public void addToSeries(long cycleStartID,float cycleStartReading,String cycleStartDate) {
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.ElectricityEntry.TABLE_NAME
                +" WHERE "+DatabaseContract.ElectricityEntry.CYCLE_START_ID+"="+cycleStartID
                +" ORDER BY "+DatabaseContract.ElectricityEntry.READING_DATE+" ASC";;

        Cursor cursor = db.rawQuery(
                query, null);

        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                long id=cursor.getLong(0);
                Float reading = cursor.getFloat(1);
                String date = cursor.getString(2);
                Log.v(TAG, "Reading entry: " + id + ", " + reading + ", " + date);

                //Daily average is calculated and added to series
                try {
                    Date startDate=sdf.parse(cycleStartDate);
                    Date mDate=sdf.parse(date);
                    float daysSince= ((mDate.getTime()-startDate.getTime())/1000/60/60/24);
                    float average;
                    if(daysSince==0)
                        average=0;
                    else
                        average=(reading-cycleStartReading)/daysSince;
                    mSeries.add(mDate, average);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                cursor.moveToNext();
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.no_data_dialog_message)
                    .setTitle(R.string.no_data_dialog_title);
        }
    }

    public void getCycleEntries(){
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.ElectricityEntry.TABLE_NAME
                +" WHERE "+DatabaseContract.ElectricityEntry.CYCLE_START_ID+"="+Integer.toString(-1)
                +" ORDER BY "+DatabaseContract.ElectricityEntry.READING_DATE+" ASC";

        Cursor cursor = db.rawQuery(
                query, null);

        if (cursor != null && cursor.moveToFirst()) {
            float prevCycleReading=cursor.getFloat(1);
            String prevCycleDate=cursor.getString(2);
            while (cursor.isAfterLast() == false) {
                long id=cursor.getLong(0);
                Float reading = cursor.getFloat(1);
                String date = cursor.getString(2);
//                Log.v(TAG,"Cycle entry: "+id+", "+reading+", "+date);
                try {
                    Date startDate=sdf.parse(prevCycleDate);
                    Date mDate=sdf.parse(date);
                    float daysSince= ((mDate.getTime()-startDate.getTime())/1000/60/60/24);
                    float average;
                    if(daysSince==0)
                        average=0;
                    else
                        average=(reading-prevCycleReading)/daysSince;

                    mSeries.add(mDate, average);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                addToSeries(id, reading, date);

                prevCycleReading=reading;
                prevCycleDate=date;

                cursor.moveToNext();
            }
        }
    }

    public void setupRenderer(){
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(getResources().getColor(R.color.accent));
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);

        renderer.setDisplayBoundingPoints(true);
        renderer.setDisplayChartValues(true);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, metrics);

        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        mRenderer.setXLabels(10);
        mRenderer.setMarginsColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));

        mRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setXLabelsAngle(-45);

        mRenderer.setZoomEnabled(true);
        mRenderer.setPanEnabled(true,true);
        mRenderer.setClickEnabled(false);
        mRenderer.setInScroll(true);

        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setYAxisMin(0);
        mRenderer.setChartTitleTextSize(val);
        mRenderer.setLabelsColor(Color.DKGRAY);
        mRenderer.setYLabelsColor(0, Color.DKGRAY);
        mRenderer.setXLabelsColor(Color.DKGRAY);
        mRenderer.setLabelsTextSize(val);
        mRenderer.setLegendTextSize(val);
        mRenderer.setAxisTitleTextSize(val);
        mRenderer.setYTitle("Consumption (kWh)");
        mRenderer.setXTitle("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n Time");
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setChartTitle("Electricity Daily Average");
        mRenderer.setShowGrid(true);
        int[] margins={20,80,80,0};
        mRenderer.setMargins(margins);

        mSeries=new TimeSeries("Daily Average");

        chart_container=(LinearLayout)rootView.findViewById(R.id.chartDailyAverage);

    }

    public static void drawChart(TimeSeries mSeries){

        final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(mSeries);

        Log.v(TAG,""+mSeries.getMaxY()+" "+mSeries.getMinY());

        mRenderer.setYAxisMax(mSeries.getMaxY()+(0.25*mSeries.getMaxY()));
        mRenderer.setXAxisMax(mSeries.getMaxX()+(0.0001*mSeries.getMaxX()));
        mRenderer.setPanLimits(new double[] {mSeries.getMinX(),mSeries.getMaxX(),(double)0,mSeries.getMaxY()+(0.25*mSeries.getMaxY())});

        final GraphicalView chartView = ChartFactory.getTimeChartView(context, dataset, mRenderer,"Electricity Daily Average");

        //To allow panning of chart without tab shifting
        chartView.setOnTouchListener(new View.OnTouchListener() {
            ViewPager mViewPager= DailyAverageActivity.mViewPager;
            @SuppressLint("WrongViewCast")
            ViewParent mParent= (ViewParent)rootView.findViewById(R.id.eAverageLayout);

            float mFirstTouchX,mFirstTouchY;

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                // save the position of the first touch so we can determine whether the user is dragging
                // left or right
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirstTouchX = event.getX();
                    mFirstTouchY = event.getY();
                }

                // when mViewPager.requestDisallowInterceptTouchEvent(true), the viewpager does not
                // intercept the events, and the drag events (pan, pinch) are caught by the GraphicalView

                // we want to keep the ViewPager from intercepting the event if:
                // 1- there are 2 or more touches, i.e. the pinch gesture
                // 2- the user is dragging to the left but there is no data to show to the right
                // 3- the user is dragging to the right but there is no data to show to the left
                if (event.getPointerCount() > 1
                        || (event.getX() < mFirstTouchX)
                        || (event.getX() > mFirstTouchX)
                        || (event.getY() < mFirstTouchY)
                        || (event.getY() > mFirstTouchY)) {
                    mViewPager.requestDisallowInterceptTouchEvent(true);
                    mParent.requestDisallowInterceptTouchEvent(true);
                }
                else {
                    mViewPager.requestDisallowInterceptTouchEvent(false);
                    mParent.requestDisallowInterceptTouchEvent(true);
                }
                // TODO Auto-generated method stub
                return false;
            }

        });

        chart_container.addView(chartView,0);
       }

}
