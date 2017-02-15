package uw.virtualpin.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uw.virtualpin.Adapters.ExpandableListViewAdapter;
import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.Data.CurrentUser;
import uw.virtualpin.Data.Pin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.FilterManager;
import uw.virtualpin.HelperClasses.LocationManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class InboxActivity extends AppCompatActivity implements OnCompletionListener, LocationListener{

    private ExpandableListView expandableListView;
    private ArrayList<Pin> inboxPins;
    private ArrayList<Pin> postHistoryPins;
    private AsyncManager asyncManager;
    private FilterManager filterManagerInbox;
    private FilterManager filterManagerPostHistory;
    private LocationManager locationManager;
    private Snackbar snackbar;
    private EditText searchBar;
    private List<String> headers;
    private HashMap<String, List<Pin>> headersPinMap;
    private String currentHeader;
    private CurrentUser user;
    private boolean isFirstTimeSetup;

    public InboxActivity() {
        headers = new ArrayList<>();
        headersPinMap = new HashMap<>();
        inboxPins = new ArrayList<>();
        postHistoryPins = new ArrayList<>();
        currentHeader = "inbox";
        isFirstTimeSetup = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        user = new CurrentUser();
        expandableListView = (ExpandableListView)findViewById(R.id.inboxList);
        locationManager = new LocationManager(this, this);
        searchBar = (EditText) findViewById(R.id.inboxSearchBarEditText);
        filterManagerInbox = new FilterManager(inboxPins);
        filterManagerPostHistory = new FilterManager(postHistoryPins);
        snackbar = Snackbar.make(findViewById(android.R.id.content), "Getting your location.", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        setupSearchBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        snackbar.dismiss();
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearAllViewData();
                inboxPins = (ArrayList) filterManagerInbox.filter(s.toString());
                postHistoryPins = (ArrayList) filterManagerPostHistory.filter(s.toString());
                setupExpandableListView(expandableListView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupExpandableListView(ExpandableListView expandableListView) {
        int numToExpand = getViewsToExpand();
        setupAllViewData();
        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(this, headers, headersPinMap);
        expandableListView.setAdapter(adapter);
        persistViewExpansion(numToExpand);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Pin pin = headersPinMap.get(headers.get(groupPosition)).get(childPosition);
                CurrentPin currentPin = new CurrentPin(pin);

                currentPin.id = pin.getId();
                currentPin.userName = pin.getUserName();
                currentPin.coordinates = "(" + pin.getLatitude() + ", " + pin.getLongitude() + ")";
                currentPin.message = pin.getMessage();

                Intent intent = new Intent(getApplicationContext(), ViewPinActivity.class);
                startActivity(intent);

                return false;
            }
        });
    }

    private void parseJSON(String jsonString) {

            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);

                    Pin pin = new Pin(jsonObject.getString("creator")
                            ,Double.parseDouble(jsonObject.getString("latitude"))
                            ,Double.parseDouble(jsonObject.getString("longitude"))
                            ,jsonObject.getString("message")
                            ,null
                            ,Integer.parseInt(jsonObject.getString("upvotes"))
                            ,Integer.parseInt(jsonObject.getString("downvotes"))
                            ,Integer.parseInt(jsonObject.getString("views")));

                    pin.setId(jsonObject.getString("pinID"));

                    if(currentHeader.equalsIgnoreCase("inbox")) {
                        inboxPins.add(pin);
                    }

                    if(currentHeader.equalsIgnoreCase("post history")) {
                        postHistoryPins.add(pin);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON", "Error parsing JSON string." + jsonString);
            }

            if(currentHeader.equalsIgnoreCase("inbox")) {
                currentHeader = "post history";
                asyncManager.pinHistory(user.username);
                Log.e("USERNAME", user.username);
            }

            else if(currentHeader.equalsIgnoreCase("post history")) {
                currentHeader = "";
                setupExpandableListView(expandableListView);
            }
        }

    @Override
    public void onComplete(String result) {
        asyncManager = asyncManager.resetAsyncManager();
        parseJSON(result);
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());

        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);
        asyncManager.nearbyPins(latitude, longitude);
        locationManager.stopLocationManager();
        snackbar.dismiss();
    }

    private void clearAllViewData() {
        headers = new ArrayList<>();
        headersPinMap = new HashMap<>();
    }

    private void setupAllViewData() {
        setupInboxViewData();
        setupPostHistoryViewData();
    }

    private void setupInboxViewData() {
        headers.add("Inbox");
        headersPinMap.put(headers.get(0), inboxPins);
    }

    private void setupPostHistoryViewData() {
        headers.add("Post History");
        headersPinMap.put(headers.get(1), postHistoryPins);
    }

    private void persistViewExpansion(int numToExpand) {
        for(int i = 0; i < numToExpand; i++) {
            expandableListView.expandGroup(i);
        }
    }

    private int getViewsToExpand() {
        if(isFirstTimeSetup) {
            isFirstTimeSetup = false;
            return 1;
        }

        int count = 0;

        if(expandableListView.isGroupExpanded(0)) {
            count++;
        }

        if(expandableListView.isGroupExpanded(1)) {
            count++;
        }

        return count;
    }
}
