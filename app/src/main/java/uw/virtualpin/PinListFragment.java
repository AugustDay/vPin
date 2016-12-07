package uw.virtualpin;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uw.virtualpin.data.PinDB;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PinListFragment extends Fragment implements LocationListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private List<Pin> mPinList;
    private OnListFragmentInteractionListener mListener;
    LocationManager locationManager;
    double mLatitude;
    double mLongitude;
    ConnectivityManager connMgr;
    public final static String CURRENT_LATITUDE = "current_latitude";
    public final static String CURRENT_LONGITUDE = "current_longitude";

    private static final String BASE_URL
            = "http://cssgate.insttech.washington.edu/~_450team8/info.php?";
    private static String cmdUrl;
    private PinDB mPinDB;
    private Pin mFirstPin;
    private Location mLocation;
    MyPinRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinListFragment() {
        mLatitude = 0.0;
        mLongitude = 0.0;


    }

    /**
     * Creates a new instance of the PinListFragment
     * @param columnCount
     * @return MessageFragment
     */
    public static PinListFragment newInstance(int columnCount) {
        PinListFragment fragment = new PinListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *  Initializes the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        locationManager = new LocationManager(getActivity(), this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // get current location form MainActivity
        /*Bundle args = getArguments();
        if (args != null) {
            mLatitude = args.getDouble(PinListFragment.CURRENT_LATITUDE);
            mLongitude = args.getDouble(PinListFragment.CURRENT_LONGITUDE);
        }*/
        
        mLocation = locationManager.getLocation();

        //getPins();
    }

    /**
     * Initializes the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

        }

        Context context = view.getContext();

        connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        getPins();

        return view;
    }

    private void getPins()
    {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && mLocation != null) {
            cmdUrl = BASE_URL + "cmd=nearby_pins&latitude=" + mLocation.getLatitude() +
                    "&longitude=" + mLocation.getLongitude();
            DownloadCoursesTask task = new DownloadCoursesTask();
            task.execute(cmdUrl);
        }
        else {
            /*Toast.makeText(view.getContext(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();*/

            if (mPinDB == null) {
                mPinDB = new PinDB(getActivity());
            }
            if (mPinList == null) {
                mPinList = mPinDB.getPins();
            }

            adapter = new MyPinRecyclerViewAdapter(mPinList, mListener);
            adapter.notifyDataSetChanged();

            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        locationManager.stopLocationManager();
    }

    public void parseJSON(String jsonString, int attempt) {

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

                mPinList.add(pin);
            }
        } catch (JSONException e) {
            if(attempt >= 2) {

                Snackbar.make(getView(), "Error, please reload this page.", Snackbar.LENGTH_LONG);
                return;
            } else {
                attempt++;
                parseJSON(jsonString, attempt);
            }
        }

        //setupListView(postsList, getMessages());
    }

    /**
     *  Called once the fragment is associated with its activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     *  Release resources
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOC", location.toString());
        this.mLocation = location;
        getPins();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Pin item);
    }

    private class DownloadCoursesTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to download the list of courses, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

//        @Override
//        protected void onPostExecute(String result) {
//            // Something wrong with the network or the URL.
//            if (result.startsWith("Unable to")) {
//                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
//                        .show();
//                return;
//            }
//
//            List<Course> courseList = new ArrayList<Course>();
//            result = Course.parseCourseJSON(result, courseList);
//            // Something wrong with the JSON returned.
//            if (result != null) {
//                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
//                        .show();
//                return;
//            }
//
//            // Everything is good, show the list of courses.
//            if (!courseList.isEmpty()) {
//                mRecyclerView.setAdapter(new MyCourseRecyclerViewAdapter(courseList, mListener));
//            }
//        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        //.show();
                return;
            }

            mPinList = new ArrayList<Pin>();
            parseJSON(result, 1);
            /*// Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }*/

            // Everything is good, show the list of courses.
            if (!mPinList.isEmpty()) {

                if (mPinDB == null) {
                    mPinDB = new PinDB(getActivity());
                }

                // Delete old data so that you can refresh the local
                // database with the network data.
                mPinDB.deletePins();

                // Also, add to the local database
                for (int i=0; i<mPinList.size(); i++) {
                    Pin pin = mPinList.get(i);
                    mPinDB.insertCourse(pin.getId(),
                            pin.getUserName(),
                            pin.getLatitude(),
                            pin.getLongitude(),
                            pin.getMessage(),
                            pin.getEncodedImage());
                }


                mFirstPin = mPinList.get(0);
                mRecyclerView.setAdapter(new MyPinRecyclerViewAdapter(mPinList, mListener));
            }
        }
    }
}
