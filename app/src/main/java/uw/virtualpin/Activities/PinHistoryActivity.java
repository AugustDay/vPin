package uw.virtualpin.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.Data.CurrentUser;
import uw.virtualpin.Data.Pin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.FilterManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class PinHistoryActivity extends AppCompatActivity implements OnCompletionListener {

    private ListView postsList;
    private ArrayList<Pin> pins;
    private AsyncManager asyncManager;
    private String username;
    private EditText searchBar;
    private FilterManager filterManager;

    public PinHistoryActivity() {
        pins = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_history);

        postsList = (ListView) findViewById(R.id.postsList);
        searchBar = (EditText) findViewById(R.id.postHistorySearchBarEditText);
        filterManager = new FilterManager(pins);

        CurrentUser currentUser = new CurrentUser();
        username = currentUser.username;

        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);
        asyncManager.pinHistory(username);
        setupSearchBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        Snackbar.make(findViewById(android.R.id.content), "Exiting Pin History", Snackbar.LENGTH_SHORT);
        asyncManager.cancel(true);
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pins = (ArrayList) filterManager.filter(s.toString());
                setupListView(postsList, getMessages());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private ArrayList<String> getMessages() {
        ArrayList<String> pinMessages = new ArrayList<>();
        int messageNumber = 1;
        int maxWidth = 30;
        int padding = 3;

        for(Pin pin : pins) {
            String message = pin.getMessage();

            if(message.length() >= maxWidth) {
                StringBuilder stringBuilder = new StringBuilder(message);
                stringBuilder.setLength(maxWidth - padding);
                stringBuilder.append("...");
                message = stringBuilder.toString();
            }

            pinMessages.add(messageNumber + ": " + message);
            messageNumber++;
        }

        return pinMessages;
    }

    private void setupListView(ListView listView, ArrayList<String> stringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.listview_item, stringList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Pin pin = pins.get(position);
                CurrentPin currentPin = new CurrentPin(pin);
                Intent intent = new Intent(getApplicationContext(), EditPinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void parseJSON(String jsonString, int attempt) {

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
            if(attempt >= 2) {

                Snackbar.make(findViewById(android.R.id.content), "Error, please reload this page.", Snackbar.LENGTH_LONG);
                return;
            } else {
                attempt++;
                parseJSON(jsonString, attempt);
            }
        }

        setupListView(postsList, getMessages());
    }

    @Override
    public void onComplete(String result) {
        Log.e("RESULT", result);
        parseJSON(result, 0);
    }
}
