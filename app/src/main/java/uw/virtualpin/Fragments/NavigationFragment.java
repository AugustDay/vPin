package uw.virtualpin.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uw.virtualpin.Activities.DropPinActivity;
import uw.virtualpin.R;

public class NavigationFragment extends Fragment {

    public String username;

    public NavigationFragment() {
        username = "tylerkb2";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        setupDropPinButton(view);
        setupPinHistoryButton();
        setupProfilePageButton();

        return view;
    }

        private void setupDropPinButton(View view) {
            Button button = (Button) view.findViewById(R.id.dropPinButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DropPinActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
            });
        }

    private void setupPinHistoryButton() {

    }

    private void setupProfilePageButton() {

    }
}
