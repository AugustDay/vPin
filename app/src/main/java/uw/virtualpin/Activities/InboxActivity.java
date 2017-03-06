package uw.virtualpin.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import uw.virtualpin.Adapters.ListViewAdapter;
import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.Data.CurrentUser;
import uw.virtualpin.Data.Pin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.FilterManager;
import uw.virtualpin.HelperClasses.LocationManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class InboxActivity extends AppCompatActivity implements OnCompletionListener, LocationListener{

    private ListView listView;
    private ArrayList<Pin> inboxPins;
    private ArrayList<Pin> postHistoryPins;
    private ArrayList<Pin> favoritePins;
    private AsyncManager asyncManager;
    private FilterManager filterManagerInbox;
    private FilterManager filterManagerPostHistory;
    private FilterManager filterManagerFavorites;
    private LocationManager locationManager;
    private Snackbar snackbar;
    private EditText searchBar;
    private String currentHeader;
    private CurrentUser user;
    private FloatingActionButton dropPinFab;
    private View searchBarView;
    private boolean favoritesSelected;
    private boolean inboxSelected;
    private boolean pinHistorySelected;
    private TextView inboxTab;
    private TextView pinHistoryTab;
    private TextView favoritesTab;

    public InboxActivity() {
        inboxPins = new ArrayList<>();
        postHistoryPins = new ArrayList<>();
        favoritePins = new ArrayList<>();
        currentHeader = "inbox";
        favoritesSelected = false;
        inboxSelected = true;
        pinHistorySelected = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_inbox);

        user = new CurrentUser();
        searchBarView = findViewById(R.id.inboxSearchBar);
        listView = (ListView)findViewById(R.id.inboxList);
        locationManager = new LocationManager(this, this);
        searchBar = (EditText) findViewById(R.id.inboxSearchBarEditText);
        filterManagerInbox = new FilterManager(inboxPins);
        filterManagerPostHistory = new FilterManager(postHistoryPins);
        filterManagerFavorites = new FilterManager(favoritePins);
        dropPinFab = (FloatingActionButton) findViewById(R.id.dropPinFAB);
        inboxTab = (TextView) findViewById(R.id.inboxTab);
        pinHistoryTab = (TextView) findViewById(R.id.pinHistoryTab);
        favoritesTab = (TextView) findViewById(R.id.favoritesTab);

        snackbar = Snackbar.make(findViewById(android.R.id.content), "Getting your location.", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        searchBarView.setVisibility(View.GONE);

        setupPinTabSelector();
        setupDropPinFab();
        setupSearchBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        snackbar.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemTitle = item.getTitle().toString();

        if(itemTitle.equalsIgnoreCase("Logout")) {
            Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
            finishAffinity();
            startActivity(logoutIntent);
        }
        else if(itemTitle.equalsIgnoreCase("Profile")) {
            Intent profileIntent = new Intent(getApplicationContext(), ProfilePage.class);
            startActivity(profileIntent);
        }

        else if(itemTitle.equalsIgnoreCase("Search")) {
            if(searchBarView.getVisibility() == View.VISIBLE) {
                searchBarView.setVisibility(View.GONE);
            } else {
                searchBarView.setVisibility(View.VISIBLE);
            }
        }

        return true;
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inboxPins = (ArrayList) filterManagerInbox.filter(s.toString());
                postHistoryPins = (ArrayList) filterManagerPostHistory.filter(s.toString());
                favoritePins = (ArrayList) filterManagerFavorites.filter(s.toString());
                setupListView(listView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupListView(ListView listView) {
        ListViewAdapter adapter = new ListViewAdapter(getApplicationContext(), getSelectedPinList()
        , getSelectedTab());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pin pin;

                if(inboxSelected) {
                    pin = inboxPins.get(position);
                    CurrentPin currentPin = new CurrentPin(pin);
                    Intent intent = new Intent(getApplicationContext(), ViewPinActivity.class);
                    startActivity(intent);
                }

                else if(pinHistorySelected) {
                    pin = postHistoryPins.get(position);
                    CurrentPin currentPin = new CurrentPin(pin);
                    Intent intent = new Intent(getApplicationContext(), EditPinActivity.class);
                    startActivity(intent);
                }

                else {
                    pin = favoritePins.get(position);
                    CurrentPin currentPin = new CurrentPin(pin);
                    Intent intent = new Intent(getApplicationContext(), ViewPinActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //needs work to condense
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
                    pin.setDateTime(jsonObject.getString("date_posted"));

                    if(currentHeader.equalsIgnoreCase("inbox")) {
                        inboxPins.add(pin);
                    }

                    if(currentHeader.equalsIgnoreCase("post history")) {
                        postHistoryPins.add(pin);
                    }

                    if(currentHeader.equalsIgnoreCase("favorites")) {
                        favoritePins.add(pin);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON", "Error parsing JSON string." + jsonString);
            }

            if(currentHeader.equalsIgnoreCase("inbox")) {
                currentHeader = "post history";
                asyncManager.pinHistory(user.username);
            }

            else if(currentHeader.equalsIgnoreCase("post history")) {
                currentHeader = "favorites";
                asyncManager.getUserFavoritePins(user.username);
            }

            else if(currentHeader.equalsIgnoreCase("favorites")) {
                currentHeader = "";
                setupListView(listView);
            }
        }

    @Override
    public void onComplete(String result) {
        asyncManager = asyncManager.resetAsyncManager();
        asyncManager.showMessages(false);
        parseJSON(result);
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());

        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);
        asyncManager.nearbyPins(latitude, longitude);
        asyncManager.showMessages(false);
        locationManager.stopLocationManager();
        snackbar.dismiss();
    }

    private void setupDropPinFab() {
        dropPinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "MADE IT");
                Intent intent = new Intent(getApplicationContext(), DropPinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupPinTabSelector() {
        inboxTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllTabsSelected();
                inboxTab.setBackgroundColor(Color.parseColor("#ffffff"));
                inboxSelected = true;
                setupListView(listView);
            }
        });

        favoritesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllTabsSelected();
                favoritesTab.setBackgroundColor(Color.parseColor("#ffffff"));
                favoritesSelected = true;
                setupListView(listView);
            }
        });

        pinHistoryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllTabsSelected();
                pinHistoryTab.setBackgroundColor(Color.parseColor("#ffffff"));
                pinHistorySelected = true;
                setupListView(listView);
            }
        });
    }

    private void clearAllTabsSelected() {
        pinHistorySelected = false;
        inboxSelected = false;
        favoritesSelected = false;

        pinHistoryTab.setBackgroundColor(Color.parseColor("#808080"));
        inboxTab.setBackgroundColor(Color.parseColor("#808080"));
        favoritesTab.setBackgroundColor(Color.parseColor("#808080"));
    }

    private ArrayList<Pin> getSelectedPinList() {
        if(inboxSelected) {
            return inboxPins;
        }

        else if(pinHistorySelected) {
            return postHistoryPins;
        }

        else {
            return favoritePins;
        }
    }

    private String getSelectedTab() {
        if(inboxSelected) {
            return "inbox";
        }

        else if(pinHistorySelected) {
            return "pin history";
        }

        else {
            return "favorites";
        }
    }

}
