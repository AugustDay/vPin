package uw.virtualpin.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.Data.Pin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.LocationManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class InboxActivity extends AppCompatActivity implements OnCompletionListener, LocationListener{

    private String username;
    private ListView listView;
    private ArrayList<Pin> pins;
    private AsyncManager asyncManager;
    private LocationManager locationManager;
    private Snackbar snackbar;

    public InboxActivity() {
        pins = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        listView = (ListView)findViewById(R.id.inboxList);
        locationManager = new LocationManager(this, this);
        snackbar = Snackbar.make(findViewById(android.R.id.content), "Getting your location.", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                username = null;
            } else {
                username = extras.getString("USERNAME");
            }
        } else {
            username = (String) savedInstanceState.getSerializable("USERNAME");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        snackbar.dismiss();
    }

    private void setupListView(ListView listView, ArrayList<String> stringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.listview_item, stringList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Pin pin = pins.get(position);
                CurrentPin currentPin = new CurrentPin(pin);

                currentPin.id = pin.getId();
                currentPin.userName = pin.getUserName();
                currentPin.coordinates = "(" + pin.getLatitude() + ", " + pin.getLongitude() + ")";
                currentPin.message = pin.getMessage();

                Intent intent = new Intent(getApplicationContext(), ViewPinActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> getPinNames() {
        ArrayList<String> names = new ArrayList<>();
        int index = 1;

        for(Pin pin : pins) {
            names.add(index + ". " + pin.getMessage());
            index++;
        }

        return names;
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
                            ,null);

                    pin.setId(jsonObject.getString("pinID"));
                    pins.add(pin);
                }
            } catch (JSONException e) {
                Log.e("JSON", "Error parsing JSON string." + jsonString);
            }
        setupListView(listView, getPinNames());
        }

    @Override
    public void onComplete(String result) {
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
}
