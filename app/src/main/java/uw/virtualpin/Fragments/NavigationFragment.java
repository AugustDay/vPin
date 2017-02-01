package uw.virtualpin.Fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uw.virtualpin.Activities.DropPinActivity;
import uw.virtualpin.Activities.InboxActivity;
import uw.virtualpin.Activities.LoginActivity;
import uw.virtualpin.Activities.PinHistoryActivity;
import uw.virtualpin.Activities.ProfilePage;
import uw.virtualpin.R;

public class NavigationFragment extends Fragment {

    private Button dropPinButton;
    private Button inboxButton;
    private Button pinHistoryButton;
    private Button profilePageButton;

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
        applySelectedColor();
        return view;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemTitle = item.getTitle().toString();

        if(itemTitle.equalsIgnoreCase("Logout")) {
            Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
            getActivity().finishAffinity();
            startActivity(logoutIntent);
        }
        else if(itemTitle.equalsIgnoreCase("Profile")) {
            Intent profileIntent = new Intent(getActivity(), ProfilePage.class);
            startActivity(profileIntent);
        }

        return true;
    }

    private void setupDropPinButton(View view) {
        dropPinButton = (Button) view.findViewById(R.id.dropPinButton);
        dropPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DropPinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupInboxButton(View view) {
        inboxButton =  (Button) view.findViewById(R.id.inboxButton);
        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InboxActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupPinHistoryButton(View view) {
        pinHistoryButton = (Button) view.findViewById(R.id.pinHistoryButton);
        pinHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PinHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupProfilePageButton(View view) {
        profilePageButton = (Button) view.findViewById(R.id.profileButton);
        profilePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        });
    }

    private void applySelectedColor() {
        String className = getActivity().getLocalClassName();
        if(className.contains("DropPin")) {
            dropPinButton.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        }
        else if(className.contains("ProfilePage")) {
            profilePageButton.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        }
        else if(className.contains("PinHistory") || className.contains("EditPin")) {
            pinHistoryButton.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        }
        else if(className.contains("Inbox") || className.contains("ViewPin")) {
            inboxButton.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        }
    }
}
