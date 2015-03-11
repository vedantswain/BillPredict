package in.ac.iiitd.zenatix.billpredict.CustomUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.iiitd.zenatix.billpredict.Database.DbEntryObject;
import in.ac.iiitd.zenatix.billpredict.R;

/**
 * Created by vedantdasswain on 10/03/15.
 */
public class HistoryArrayAdapter extends ArrayAdapter<DbEntryObject> {
    private final Context context;
    private final ArrayList<DbEntryObject> entryObjectList;

    public HistoryArrayAdapter(Context context, ArrayList<DbEntryObject> objectList) {
        super(context, R.layout.history_list_item, objectList);
        this.context = context;
        this.entryObjectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_list_item, parent, false);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.date);
        dateTextView.setText(entryObjectList.get(position).getDate());
        TextView readingTextView = (TextView) rowView.findViewById(R.id.reading);
        readingTextView.setText(String.format("%.2f",entryObjectList.get(position).getReading()));

        return rowView;
    }
}
