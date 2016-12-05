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

import uw.virtualpin.pin.Pin;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PinListFragment.OnListFragmentInteractionListener {

    /**
     *  Initialize the main activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PinListFragment()).commit();
    }

    /**
     *  Determines what happens when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        this.setTitle("Inbox");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PinListFragment()).commit();
    }

    /**
     * Initialize menu options
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     *  Handle action bar item clicks
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
        }

        if (id == R.id.drop_pin) {
            setFragment("Drop pin");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  Handle navigation item clicks here
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
        } else if (id == R.id.nav_bookmarks) {

        } else if (id == R.id.nav_filter) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_send) {

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
                    .replace(R.id.fragment_container, new PinListFragment()).commit();

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
    public void onListFragmentInteraction(Pin item) {

/*        CourseDetailFragment courseDetailFragment = new CourseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(CourseDetailFragment.COURSE_ITEM_SELECTED, item);
        courseDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, courseDetailFragment)
                .addToBackStack(null)
                .commit();*/

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
