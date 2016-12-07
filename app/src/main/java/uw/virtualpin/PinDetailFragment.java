package uw.virtualpin;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.LocationSource;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinDetailFragment extends Fragment implements LocationSource.OnLocationChangedListener {
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

            ImageManager manager = new ImageManager();
            mPinImageView.setImageBitmap(manager.convertEncodedImageToBitmap(pin.getEncodedImage()));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
