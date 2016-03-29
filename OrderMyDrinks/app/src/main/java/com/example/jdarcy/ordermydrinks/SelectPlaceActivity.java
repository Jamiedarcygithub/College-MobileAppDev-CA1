package com.example.jdarcy.ordermydrinks;

/**
 * Places Activity
 * Loads nearby places from google places of types bar
 * return a list of bars for user to select
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectPlaceActivity extends AppCompatActivity {
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //Progress Dialog Manager
    ProgressDialogManager pDialog = new ProgressDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Places List
    PlaceList nearPlaces;

    // GPS Location
    GPSTracker gps;

    // Places Listview
    ListView lv;

    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<>();


    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name


    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);

        setUpDrawer();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(SelectPlaceActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // creating GPS Class object
        gps = new GPSTracker(this);

        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            alert.showAlertDialog(SelectPlaceActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }

        // Getting listview
        lv = (ListView) findViewById(R.id.list);

        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        new LoadPlaces().execute();


        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                String userName = SelectPlaceActivity.this.getIntent().getStringExtra("userName");
                String userEmail = SelectPlaceActivity.this.getIntent().getStringExtra("userEmail");
                String guests = SelectPlaceActivity.this.getIntent().getStringExtra("guests");

                // Starting new intent
                Intent i = new Intent(getApplicationContext(),
                        SelectedPlaceActivity.class);


                i.putExtra("userName", userName);
                i.putExtra("userEmail", userEmail);
                i.putExtra("guests", guests);

                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                i.putExtra(KEY_REFERENCE, reference);

                try {
                    //Launch the Selected Place Activity
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                    alert.showAlertDialog(SelectPlaceActivity.this, "Activity Launch Error",
                            "Could not find Activity: SelectedPlaceActivity",
                            false);
                }


            }
        });


    }

    /**
     * Background Async Task to Load Google places
     */
    class LoadPlaces extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.showProcessDialog(SelectPlaceActivity.this, "", "Loading Places...");
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();

            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                //
                String types = "bar|night_club"; // Listing places bars or nightclubs

                // Radius to search in meters
                double radius = 1500; // 1000 meters

                // get nearest places
                nearPlaces = googlePlaces.search(gps.getLatitude(),
                        gps.getLongitude(), radius, types);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismissProcessDialog();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;


                    // Check for all possible status
                    switch (status) {
                        case "OK":
                            // Successfully got places details
                            if (nearPlaces.results != null) {
                                // loop through each place
                                for (Place p : nearPlaces.results) {
                                    HashMap<String, String> map = new HashMap<>();
                                    // Place reference won't display in listview - it will be hidden
                                    // Place reference is used to get "place full details"
                                    map.put(KEY_REFERENCE, p.reference);
                                    // Bar name
                                    map.put(KEY_NAME, p.name);
                                    // Bar address
                                    map.put(KEY_VICINITY, p.vicinity);
                                    // adding HashMap to ArrayList
                                    placesListItems.add(map);
                                }
                                // list adapter
                                ListAdapter adapter = new SimpleAdapter(SelectPlaceActivity.this, placesListItems,
                                        R.layout.list_item,
                                        new String[]{KEY_REFERENCE, KEY_NAME, KEY_VICINITY}, new int[]{
                                        R.id.reference, R.id.name, R.id.vicinity});
                                // Adding data into listview
                                lv.setAdapter(adapter);
                            }
                            break;
                        // no results found
                        case "ZERO_RESULTS":
                            alert.showAlertDialog(SelectPlaceActivity.this, "Near Places",
                                    "Sorry no bars found in area",
                                    false);
                            break;
                        case "UNKNOWN_ERROR":
                            alert.showAlertDialog(SelectPlaceActivity.this, "Places Error",
                                    "Sorry unknown error occurred.",
                                    false);
                            break;

                        case "OVER_QUERY_LIMIT":
                            alert.showAlertDialog(SelectPlaceActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                            break;
                        case "REQUEST_DENIED":
                            alert.showAlertDialog(SelectPlaceActivity.this, "Places Error",
                                    "Sorry error occurred. Request is denied",
                                    false);
                            break;
                        case "INVALID_REQUEST":

                            alert.showAlertDialog(SelectPlaceActivity.this, "Places Error",
                                    "Sorry error occurred. Invalid Request",
                                    false);
                            break;
                        default:
                            alert.showAlertDialog(SelectPlaceActivity.this, "Places Error",
                                    "Sorry error occurred.",
                                    false);
                            break;

                    }

                }
            });

        }
    }


    /**
     * Method to load menu on Activity create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        return true;
    }

    /**
     * Method to handle options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg = "";

        switch (item.getItemId()) {

            case R.id.search:
                Intent i = new Intent(this, SelectPlaceActivity.class);
                i.putExtra("userName", this.getIntent().getStringExtra("userName"));
                i.putExtra("userEmail", this.getIntent().getStringExtra("userEmail"));
                i.putExtra("guests", this.getIntent().getStringExtra("guests"));
                startActivity(i);
                break;
            case R.id.refresh:
                new LoadPlaces().execute();
                break;
            case R.id.settings:
                Toast.makeText(this, "Setting not implemented yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Exit:
                System.exit(1);
                break;
        }

        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setUpDrawer() {

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);


        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");

                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
                toolbar.setNavigationIcon(R.drawable.ic_menu);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(mDrawerToggle);

    }

}

