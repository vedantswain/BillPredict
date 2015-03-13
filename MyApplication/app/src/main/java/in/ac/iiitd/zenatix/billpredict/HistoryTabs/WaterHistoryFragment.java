package in.ac.iiitd.zenatix.billpredict.HistoryTabs;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.iiitd.zenatix.billpredict.CustomUI.HistoryArrayAdapter;
import in.ac.iiitd.zenatix.billpredict.Database.BillPredictDbHelper;
import in.ac.iiitd.zenatix.billpredict.Database.DatabaseContract;
import in.ac.iiitd.zenatix.billpredict.Database.DbEntryObject;
import in.ac.iiitd.zenatix.billpredict.R;

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

    private static final String TAG="WHistoryFragment";
    Context context;
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
        context=getActivity();
        mDbHelper = new BillPredictDbHelper(getActivity());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        openDialog(list.get(position));
    }

    private void setUpList(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.WaterEntry.TABLE_NAME
                +" ORDER BY "+DatabaseContract.WaterEntry.READING_DATE+" DESC";

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

        if(list.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.no_data_dialog_message)
                    .setTitle(R.string.no_data_dialog_title);
        }

        setListShown(true);
    }

    private void openDialog(final DbEntryObject entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText input = new EditText(context);
        input.setText(Float.toString(entry.getReading()));
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.edit_reading_dialog_message)
                .setTitle(R.string.edit_reading_dialog_title);

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                long oldId=getId(entry.getDate());
                input.getText();
                if(update(oldId,Float.parseFloat(input.getText().toString()))>0) {
                    list.clear();
                    setUpList();
                    Toast.makeText(context, "Entry updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked DELETE button
                long oldId=getId(entry.getDate());
                if(delete(oldId)){
                    list.clear();
                    setUpList();
                    Toast.makeText(context,"Entry deleted",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked CANCEL button
                //Do nothing
            }
        });


        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
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

    public long update(long rowId,float meterReading){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WaterEntry.METER_READING, meterReading);

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

    public boolean delete(long rowId){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.delete(DatabaseContract.WaterEntry.TABLE_NAME,
                DatabaseContract.WaterEntry._ID + "=" + rowId, null) > 0;
    }


}
