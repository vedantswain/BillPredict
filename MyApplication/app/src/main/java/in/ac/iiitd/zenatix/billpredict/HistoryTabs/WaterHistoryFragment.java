package in.ac.iiitd.zenatix.billpredict.HistoryTabs;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;

import in.ac.iiitd.zenatix.billpredict.CustomUI.HistoryArrayAdapter;
import in.ac.iiitd.zenatix.billpredict.Database.BillPredictDbHelper;
import in.ac.iiitd.zenatix.billpredict.Database.DatabaseContract;
import in.ac.iiitd.zenatix.billpredict.Database.DbEntryObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaterHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaterHistoryFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    HistoryArrayAdapter adapter;
    BillPredictDbHelper mDbHelper;
    private ArrayList<DbEntryObject> list=new ArrayList<DbEntryObject>();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaterHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaterHistoryFragment newInstance(String param1, String param2) {
        WaterHistoryFragment fragment = new WaterHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WaterHistoryFragment() {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.WaterEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(
                query, null);

        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Float reading = cursor.getFloat(1);
                String date=cursor.getString(2);
                list.add(new DbEntryObject(reading,date));
                cursor.moveToNext();
            }

            adapter=new HistoryArrayAdapter(getActivity(),list);
            setListAdapter(adapter);
        }
    }



}
