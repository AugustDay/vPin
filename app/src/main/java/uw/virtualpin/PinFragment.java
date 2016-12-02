package uw.virtualpin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment {

    ArrayList<String> pinDetails;
    private TextView creatorText;
    private TextView locationText;
    private EditText messageText;
    private ImageView imageView;
    private ImageManager imageManager;


    public PinFragment() {
        pinDetails = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pin, container, false);
        pinDetails = getArguments().getStringArrayList("PINS");

        imageManager = new ImageManager();
        creatorText = (TextView) view.findViewById(R.id.creatorTextHistory);
        locationText = (TextView) view.findViewById(R.id.locationTextHistory);
        messageText = (EditText) view.findViewById(R.id.messageTextHistory);
        imageView = (ImageView) view.findViewById(R.id.imageViewHistory);

        setupPinDetails();
        return view;
    }

    private void setupPinDetails() {
        try {

            creatorText.setText("Created by: " + pinDetails.get(0));
            locationText.setText("Location: " + pinDetails.get(1));
            messageText.setText(pinDetails.get(2));
            imageView.setImageBitmap(imageManager.convertEncodedImageToBitmap(pinDetails.get(3)));

        } catch (Exception e) {
            Snackbar.make(getView(), "Error loading pin, please try again.", Snackbar.LENGTH_LONG);
        }
    }
}
