package com.example.jdarcy.ordermydrinks;

/**
 * Selected Place Activity
 * Displays details about the selected bar from google places
 * with call and email bar functionality
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SelectedPlaceActivity extends AppCompatActivity {
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

    // Place Details
    PlaceDetails placeDetails;

    // Button to make call activity
    FloatingActionButton fabCallButton;

    //Button to browse to next activity
    FloatingActionButton fabAddButton;

    //Button to open Email intent
    FloatingActionButton fabEmailButton;


    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place

    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);

        setUpDrawer();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabEmailButton = (FloatingActionButton) findViewById(R.id.fab_email);
        fabEmailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fabEmailButtonClick();
            }
        });

        fabCallButton = (FloatingActionButton) findViewById(R.id.fab_call);
        fabCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fabCallButtonClick();
            }
        });

        fabAddButton = (FloatingActionButton) findViewById(R.id.fab);
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fabAddButtonClick();
            }
        });
        Intent i = getIntent();

        // Place referece id
        String reference = i.getStringExtra(KEY_REFERENCE);

        // Calling a Async Background thread
        new LoadSelectedPlace().execute(reference);
    }


    /**
     * Call Bar button to Load phones dialer
     */
    private void fabCallButtonClick() {
        //define a new Intent for the Make Call Activity
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("0000000000"));
        i.setPackage("com.android.server.telecom");
        i.setData(Uri.parse("tel:" + placeDetails.result.formatted_phone_number));
        try {
            //Launch the Phone bar Activity
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            alert.showAlertDialog(SelectedPlaceActivity.this, "Activity Launch Error",
                    "Could not find Activity: Action_Call",
                    false);
        }
    }

    /**
     * Email button to load Email intent
     */
    public void fabEmailButtonClick() {

        //define a new Intent to send an email
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setType("vnd.android.cursor.item/email");
        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"abc@xyz.com"});
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Order Drinks");
        i.putExtra(android.content.Intent.EXTRA_TEXT, "Order drinks");
        startActivity(Intent.createChooser(i, "Send mail using..."));
        startActivity(i);
        try {
            //Launch the Reservation details Activity
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            alert.showAlertDialog(SelectedPlaceActivity.this, "Intent Launch Error",
                    "Could not find Intent: Send email",
                    false);
        }
    }

    /**
     * Make Reservation button to  Load Reservation details Activity
     */
    public void fabAddButtonClick() {

        //define a new Intent for the travel details
        Intent i = new Intent(this, TravelDetailsActivity.class);
        i.putExtra("userName", this.getIntent().getStringExtra("userName"));
        i.putExtra("userEmail", this.getIntent().getStringExtra("userEmail"));
        i.putExtra("guests", this.getIntent().getStringExtra("guests"));
        i.putExtra("barName", placeDetails.result.name);
        i.putExtra("barAddress", placeDetails.result.formatted_address);
        i.putExtra("barPhoneNumber", placeDetails.result.formatted_phone_number);
        i.putExtra("barLocatonLat", Double.toString(placeDetails.result.geometry.location.lat));
        i.putExtra("barLocationLng", Double.toString(placeDetails.result.geometry.location.lng));
        i.putExtra("barWebsite", placeDetails.result.website);

        Toast.makeText(this, placeDetails.result.geometry.location.lat+" "+ placeDetails.result.geometry.location.lng, Toast.LENGTH_SHORT).show();

        startActivity(i);
        try {
            //Launch the Travel details Activity
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            alert.showAlertDialog(SelectedPlaceActivity.this, "Activity Launch Error",
                    "Could not find Activity: Travel details activity",
                    false);
        }
    }


    /**
     * Background Async Task to Load Google places
     */
    class LoadSelectedPlace extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.showProcessDialog(SelectedPlaceActivity.this, "", "Loading profile ...");
        }

        /**
         * getting Profile JSON
         */
        protected String doInBackground(String... args) {
            String reference = args[0];

            // creating Places class object
            googlePlaces = new GooglePlaces();

            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getPlaceDetails(reference);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismissProcessDialog();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (placeDetails != null) {
                        String status = placeDetails.status;
                        // check place details status
                        // Check for all possible status
                        if (status.equals("OK")) {
                            if (placeDetails.result != null) {

                                String name = placeDetails.result.name;
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String website = placeDetails.result.website;
                                String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                                String longitude = Double.toString(placeDetails.result.geometry.location.lng);

                                Log.d("Place ", name + address + phone + latitude + longitude);

                                // Displaying all the details in the view
                                // activity_selected_place.xml
                                TextView lbl_name = (TextView) findViewById(R.id.name);
                                TextView lbl_address = (TextView) findViewById(R.id.address);
                                TextView lbl_phone = (TextView) findViewById(R.id.phone);
                                TextView lbl_location = (TextView) findViewById(R.id.location);
                                TextView lbl_website = (TextView) findViewById(R.id.website);
                                // Check for null data from google
                                // Sometimes place details might missing
                                name = name == null ? "Not present" : name; // if name is null display as "Not present"
                                address = address == null ? "Not present" : address;
                                //if (phone == null) {
                                //    fabCallButton.setEnabled(false);
                                //    phone = "Not present";
                                //}
                                phone = phone == null ? "Not present" : phone;
                                website = website == null ? "Not present" : website;
                                latitude = latitude == null ? "Not present" : latitude;
                                longitude = longitude == null ? "Not present" : longitude;

                                lbl_name.setText(name);
                                lbl_address.setText(address);
                                lbl_website.setText(website);
                                lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
                                lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));
                            }
                        } else if (status.equals("ZERO_RESULTS")) {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Near Places",
                                    "Error no place found.",
                                    false);
                        } else if (status.equals("OVER_QUERY_LIMIT")) {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                    "Error Occurred - query limit to google places is reached",
                                    false);
                        } else if (status.equals("REQUEST_DENIED")) {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                    "Error Occurred. Request is denied",
                                    false);
                        } else if (status.equals("INVALID_REQUEST")) {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                    "Error Occurred. Invalid Request",
                                    false);
                        } else if (status.equals("UNKNOWN_ERROR")) {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                    "Sorry unknown error Occurred.",
                                    false);
                        } else {
                            alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                    "Error Occurred.",
                                    false);
                        }
                    } else {
                        alert.showAlertDialog(SelectedPlaceActivity.this, "Places Error",
                                "Error Occurred.",
                                false);
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
                break;
            case R.id.settings:
                Toast.makeText(this, "Setting not implemented yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Exit:
                System.exit(1);
                break;
        }

        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item)|| super.onOptionsItemSelected(item);
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

