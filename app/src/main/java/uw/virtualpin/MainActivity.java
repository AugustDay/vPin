package uw.virtualpin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PinListFragment.OnListFragmentInteractionListener {

    //EditText etUserName, etPassword, etFirstName, etLastName, etEmail;
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

        userLocalStore = new UserLocalStore(this);


//        setFragment("Inbox");
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        setFragment("Inbox");
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

        Intent intent = new Intent(this, this.getClass());
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }

    /**
     *
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

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

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
            Intent intent = new Intent(this, this.getClass());
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
            Intent intent = new Intent(this, ProfilePage.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PinListFragment()).commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostHistoryFragment()).commit();
            setTitle("Post History");

        } else if (id == R.id.nav_pin) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DropPinFragment()).commit();
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
            PinListFragment pinListFragment = new PinListFragment();
            Bundle args = new Bundle();

            pinListFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, pinListFragment);

            // Commit the transaction
            transaction.commit();

            this.setTitle("Inbox");
        }
        else if (title == "Drop pin") {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DropPinFragment()).commit();

            // change mainbar title
            this.setTitle("Drop pin");
        }
    }


    @Override
    public void onListFragmentInteraction(Pin item) {

        PinDetailFragment pinDetailFragment = new PinDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(PinDetailFragment.PIN_ITEM_SELECTED, item);
        pinDetailFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, pinDetailFragment)
                .addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}
