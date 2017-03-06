package uw.virtualpin.Adapters;

import android.content.Context;
import android.graphics.Color;
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
    private final String tab;

    public ListViewAdapter(Context context, ArrayList<Pin> pins, String tab) {
        super(context, R.layout.listview_row, pins);

        this.context = context;
        this.pins = pins;
        this.tab = tab;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listview_row, parent, false);
        //formatRowViewColor(rowView);

        TextView username = (TextView) rowView.findViewById(R.id.listview_username);
        TextView message = (TextView) rowView.findViewById(R.id.listview_message);
        TextView score = (TextView) rowView.findViewById(R.id.listview_score);

        int pinScore = pins.get(position).getScore();
        String pinUsername = formatUsername(pins.get(position).getUserName());
        formatScore(score, pinScore);

        username.setText(pinUsername + ": ");
        message.setText("'" + pins.get(position).getMessage() + "'");
        score.setText(score.getText() + Integer.toString(pinScore));

        return rowView;
    }

    public void formatScore(TextView scoreText, int score) {
        if(score >= 0) {
            scoreText.setText("+");
            scoreText.setTextColor(Color.parseColor("#33cc33"));
        } else {
           scoreText.setTextColor(Color.parseColor("#e60000"));
        }
    }

    public String formatUsername(String username) {
        username = username.substring(0,1).toUpperCase() + username.substring(1);
        return username;
    }

    public void formatRowViewColor(View view) {
        if(tab.equalsIgnoreCase("inbox")) {
            view.setBackgroundColor(Color.parseColor("#e6f5ff"));
        }
        else if(tab.equalsIgnoreCase("pin history")) {
            view.setBackgroundColor(Color.parseColor("#ffe0cc"));
        }
        else {
            view.setBackgroundColor(Color.parseColor("#ffb3b3"));
        }
    }
}
