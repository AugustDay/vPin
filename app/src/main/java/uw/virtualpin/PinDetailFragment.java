package uw.virtualpin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.virtualpin.pin.Pin;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinDetailFragment extends Fragment {
    private TextView mPinIdTextView;
    private TextView mPinCreatorView;
    private TextView mPinLatitudeView;
    private TextView mPinLongitudeTextView;
    private TextView mPinMessageTextView;
    public final static String PIN_ITEM_SELECTED = "pin_selected";

    public PinDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin_detail, container, false);

        mPinIdTextView = (TextView) view.findViewById(R.id.pin_id);;
        mPinCreatorView = (TextView) view.findViewById(R.id.pin_creator);
        mPinLatitudeView = (TextView) view.findViewById(R.id.pin_latitude);
        mPinLongitudeTextView = (TextView) view.findViewById(R.id.pin_longitude);
        mPinMessageTextView = (TextView) view.findViewById(R.id.pin_message);

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

    public void updateView(Pin pin) {
        if (pin != null) {
            mPinIdTextView.setText(String.valueOf(pin.getPinId()));
            mPinCreatorView.setText(pin.getCreator());
            mPinLatitudeView.setText(String.valueOf(pin.getLatitude()));
            mPinLongitudeTextView.setText(String.valueOf(pin.getLongitude()));
            mPinMessageTextView.setText(pin.getMessage());
        }
    }
}
