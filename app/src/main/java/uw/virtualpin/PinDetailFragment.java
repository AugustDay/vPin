package uw.virtualpin;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.LocationSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinDetailFragment extends Fragment implements LocationSource.OnLocationChangedListener {
    private static final String GET_URL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=get_pin&id=";
    private TextView mPinIdTextView;
    private TextView mPinCreatorView;
    private TextView mPinLatitudeView;
    private TextView mPinLongitudeTextView;
    private TextView mPinMessageTextView;
    private ImageView mPinImageView;
    public final static String PIN_ITEM_SELECTED = "pin_selected";

    public PinDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin_detail, container, false);

        //mPinIdTextView = (TextView) view.findViewById(R.id.pin_id);;
        mPinCreatorView = (TextView) view.findViewById(R.id.pin_username);
        mPinLatitudeView = (TextView) view.findViewById(R.id.pin_latitude);
        mPinLongitudeTextView = (TextView) view.findViewById(R.id.pin_longitude);
        mPinMessageTextView = (TextView) view.findViewById(R.id.pin_message);
        mPinImageView = (ImageView) view.findViewById(R.id.pin_image);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((Pin) args.getSerializable(PIN_ITEM_SELECTED));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void updateView(Pin pin) {
        if (pin != null) {
            //mPinIdTextView.setText(String.valueOf(pin.getId()));
            mPinCreatorView.setText(pin.getUserName());
            mPinLatitudeView.setText(String.valueOf(pin.getLatitude()));
            mPinLongitudeTextView.setText(String.valueOf(pin.getLongitude()));
            mPinMessageTextView.setText(pin.getMessage());

            GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask();
            getImageAsyncTask.execute(GET_URL + pin.getId());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     * The async task to get the pin details from the web service.
     */
    private class GetImageAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to complete your request, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                ImageManager imageManager = new ImageManager();
                Bitmap image = imageManager.convertEncodedImageToBitmap(jsonObject.getString("image"));
                mPinImageView.setImageBitmap(image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
