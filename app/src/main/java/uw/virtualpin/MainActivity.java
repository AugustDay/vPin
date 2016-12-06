package uw.virtualpin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import uw.virtualpin.message.MessageContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MessageFragment.OnListFragmentInteractionListener {




    UserLocalStore userLocalStore;

    String username;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MessageFragment()).commit();

        //userLocalStore = new UserLocalStore(this);

            getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MessageFragment()).commit();

            userLocalStore = new UserLocalStore(this);

            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    username = null;
                } else {
                    username = extras.getString("USERNAME");
                    userLocalStore.getLoggedinUser();

                }
            } else {
                username = (String) savedInstanceState.getSerializable("USERNAME");
            }
    }
    /**
     *
     */
    @Override
    public void onBackPressed() {
        this.setTitle("Inbox");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MessageFragment()).commit();
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView textView = (TextView) findViewById(R.id.menuUsernameText);
        textView.setText(username.toUpperCase());
        textView.setTextSize(14);
        return true;
    }

    /**
     *
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            userLocalStore.clearUserData();
            finish();
        }

        if (id == R.id.drop_pin) {
            setFragment("Drop pin");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param item
     * @return onNavigationItemSelected
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inbox) {
            setFragment("Inbox");

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);

        } else if (id == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MessageFragment()).commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostHistoryFragment()).addToBackStack(null).commit();
            setTitle("Post History");

        } else if (id == R.id.nav_pin) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DropPinFragment()).addToBackStack(null).commit();
            setTitle("Drop Pin");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     * @param title
     */
    private void setFragment(String title)
    {
        if (title == "Inbox") {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MessageFragment()).addToBackStack(null).commit();

            this.setTitle("Inbox");
        }
        else if (title == "Drop pin") {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DropPinFragment()).addToBackStack(null).commit();

            // change mainbar title
            this.setTitle("Drop pin");
        }
    }

    @Override
    public void onListFragmentInteraction(MessageContent.MessageItem item) {

    }
}
