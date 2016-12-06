package uw.virtualpin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfilePage extends AppCompatActivity {

    UserLocalStore userLocalStore;

    String username;
    String email;
    int postCount;
    String userFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        TextView usernameTV = (TextView) findViewById(R.id.user_profile_username);
        TextView emailTV = (TextView) findViewById(R.id.user_profile_email);
        TextView numPostsTV = (TextView) findViewById(R.id.user_profile_postCount);
        TextView fullNameTV = (TextView) findViewById(R.id.user_profile_name);

        userLocalStore = new UserLocalStore(this);


        if (savedInstanceState == null) {
            if (userLocalStore.getUserLoggedIn())
            {
                username = userLocalStore.getLoggedinUser().mUsername;
                userFullName = userLocalStore.getLoggedinUser().mFirstName + " " + userLocalStore.getLoggedinUser().mLastName;
                email = userLocalStore.getLoggedinUser().mEmail;
            }
            else {
                username = null;
                email = null;
                postCount = 0;
                userFullName = null;
            }
        }

        usernameTV.setText("Username: " + username.toUpperCase());
        usernameTV.setTextSize(16);

        emailTV.setText("Email: " + email.toUpperCase());
        emailTV.setTextSize(16);

        fullNameTV.setText((userFullName.toUpperCase()));
        fullNameTV.setTextSize(16);

//      numPostsTV.setText(postCount.toUpperCase());
//      numPostsTV.setTextSize(20);


    }
}
