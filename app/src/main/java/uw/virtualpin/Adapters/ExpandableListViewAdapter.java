package uw.virtualpin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import uw.virtualpin.Data.Pin;
import uw.virtualpin.R;

/**
 * Created by tyler on 2/14/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> headers;
    private HashMap<String, List<Pin>> headerChildren;

    public ExpandableListViewAdapter(Context context, List<String> headers, HashMap<String, List<Pin>> headerChildren) {
        this.context = context;
        this.headers = headers;
        this.headerChildren = headerChildren;
    }

    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return headerChildren.get(headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return headerChildren.get(headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_header, null);
        }

        TextView headerTitleView = (TextView) convertView.findViewById(R.id.listview_header);
        headerTitleView.setText(headerTitle);
        formatTitle(headerTitleView);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Pin pin = (Pin) getChild(groupPosition, childPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row, null);
        }

        TextView username = (TextView) convertView.findViewById(R.id.listview_username);
        TextView message = (TextView) convertView.findViewById(R.id.listview_message);
        TextView score = (TextView) convertView.findViewById(R.id.listview_score);

        int pinScore = pin.getScore();
        String pinUsername = formatUsername(pin.getUserName());
        formatScore(score, pinScore);

        username.setText(pinUsername + ": ");
        message.setText("'" + pin.getMessage() + "'");
        score.setText(score.getText() + Integer.toString(pinScore));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void formatScore(TextView scoreText, int score) {
        scoreText.setText("");

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

    public void formatTitle(TextView titleText) {
        String title = titleText.getText().toString();

        if(title.equalsIgnoreCase("Inbox")) {
            titleText.setTextColor(Color.parseColor("#4da6ff"));
        }
        else if(title.equalsIgnoreCase("Post History")) {
            titleText.setTextColor(Color.parseColor("#e68a00"));
        }
    }
}
