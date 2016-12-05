package uw.virtualpin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uw.virtualpin.data.PinDB;
import uw.virtualpin.pin.Pin;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PinListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private List<Pin> mPinList;
    private OnListFragmentInteractionListener mListener;

    private static final String COURSE_URL
            = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=select*pins";
    private PinDB mPinDB;
    private Pin mFirstPin;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinListFragment() {
    }

    /**
     * Creates a new instance of the MessageFragment
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
            //recyclerView.setAdapter(new MyMessageRecyclerViewAdapter(Pin.ITEMS, mListener));
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadCoursesTask task = new DownloadCoursesTask();
            task.execute(new String[]{COURSE_URL});
        }
        else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT) .show();

            if (mPinDB == null) {
                mPinDB = new PinDB(getActivity());
            }
            if (mPinList == null) {
                mPinList = mPinDB.getPins();
            }
            mRecyclerView.setAdapter(new MyPinRecyclerViewAdapter(mPinList, mListener));

        }

        //Read from file and show the text
/*        try {
            InputStream inputStream = getActivity().openFileInput(
                    getString(R.string.LOGIN_FILE));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                Toast.makeText(getActivity(), stringBuilder.toString(), Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return view;
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
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mPinList = new ArrayList<Pin>();
            result = Pin.parseCourseJSON(result, mPinList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

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
                    mPinDB.insertCourse(pin.getPinId(),
                            pin.getCreator(),
                            pin.getLatitude(),
                            pin.getLongitude(),
                            pin.getMessage());
                }


                mFirstPin = mPinList.get(0);
                mRecyclerView.setAdapter(new MyPinRecyclerViewAdapter(mPinList, mListener));
            }
        }
    }
}
