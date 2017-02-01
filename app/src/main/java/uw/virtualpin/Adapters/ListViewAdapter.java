package uw.virtualpin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uw.virtualpin.Data.Pin;
import uw.virtualpin.R;

/**
 * Created by Tyler on 2/1/2017.
 */

public class ListViewAdapter extends ArrayAdapter<Pin> {

    private final Context context;
    private final ArrayList<Pin> pins;

    public ListViewAdapter(Context context, ArrayList<Pin> pins) {

        super(context, R.layout.listview_row, pins);

        this.context = context;
        this.pins = pins;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listview_row, parent, false);

        TextView username = (TextView) rowView.findViewById(R.id.listview_username);
        TextView message = (TextView) rowView.findViewById(R.id.listview_message);
        TextView score = (TextView) rowView.findViewById(R.id.listview_score);

        username.setText(pins.get(position).getUserName() + ": ");
        message.setText("'" + pins.get(position).getMessage() + "'");
        score.setText("+" + Integer.toString(pins.get(position).getScore()));

        return rowView;
    }
}
