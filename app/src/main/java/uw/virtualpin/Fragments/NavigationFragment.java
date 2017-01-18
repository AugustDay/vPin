package uw.virtualpin.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uw.virtualpin.Activities.DropPinActivity;
import uw.virtualpin.Activities.InboxActivity;
import uw.virtualpin.Activities.PinHistoryActivity;
import uw.virtualpin.Activities.ProfilePage;
import uw.virtualpin.R;

public class NavigationFragment extends Fragment {

    public NavigationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        setupDropPinButton(view);
        setupPinHistoryButton(view);
        setupProfilePageButton(view);
        setupInboxButton(view);
        return view;
    }

    private void setupDropPinButton(View view) {
        Button button = (Button) view.findViewById(R.id.dropPinButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DropPinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupInboxButton(View view) {
        Button button = (Button) view.findViewById(R.id.inboxButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InboxActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupPinHistoryButton(View view) {
        Button button = (Button) view.findViewById(R.id.pinHistoryButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PinHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupProfilePageButton(View view) {
        Button button = (Button) view.findViewById(R.id.profileButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        });
    }
}
