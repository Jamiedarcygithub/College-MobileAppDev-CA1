package com.example.jdarcy.ordermydrinks;

/**
 * Travel details activity
 * loads map with current location and destination location
 * user select method of travel (Walk, Drive Cycle) and the
 * estimate travel time is calculated using the google maps api
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TravelDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //Progress Dialog Manager
    ProgressDialogManager pDialog = new ProgressDialogManager();

    // Google map
    GoogleMap googleMap;

    // GPS Location
    GPSTracker gps;

    //Button to next Activity intent
    FloatingActionButton fabButton;

    RadioButton rbDriving;
    RadioButton rbBiCycling;
    RadioButton rbWalking;
    RadioGroup rgModes;
    ArrayList<LatLng> markerPoints;
    int mMode = 0;
    final int MODE_DRIVING = 0;
    final int MODE_BICYCLING = 1;
    final int MODE_WALKING = 2;
    TextView tvDistanceDuration;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);
        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(TravelDetailsActivity.this, "Internet Connection Error",
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
            alert.showAlertDialog(TravelDetailsActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }

        setUpDrawer();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fabButtonClick();
            }
        });

        // Getting reference to rb_driving
        rbDriving = (RadioButton) findViewById(R.id.rb_driving);

        // Getting reference to rb_bicylcing
        rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);

        // Getting reference to rb_walking
        rbWalking = (RadioButton) findViewById(R.id.rb_walking);

        // Getting Reference to rg_modes
        rgModes = (RadioGroup) findViewById(R.id.rg_modes);
        rgModes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });

        // Initializing
        markerPoints = new ArrayList<>();
        // Getting reference to SupportMapFragment of the travel activiity
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);

    }

    /**
     * Make Reservation button to  Load Reservation details Activity
     */
    public void fabButtonClick() {

        //define a new Intent for the Reservation details
        Intent i = new Intent(this, SelectDrinksActivity.class);

        i.putExtra("userName", this.getIntent().getStringExtra("userName"));
        i.putExtra("userEmail", this.getIntent().getStringExtra("userEmail"));
        i.putExtra("guests", this.getIntent().getStringExtra("guests"));
        i.putExtra("barName", this.getIntent().getStringExtra("barName"));
        i.putExtra("barAddress", this.getIntent().getStringExtra("barAddress"));
        i.putExtra("barPhoneNumber", this.getIntent().getStringExtra("barPhoneNumber"));
        i.putExtra("barLocatonLat", this.getIntent().getStringExtra("barLocatonLat"));
        i.putExtra("barLocationLng", this.getIntent().getStringExtra("barLocationLng"));
        i.putExtra("barWebsite", this.getIntent().getStringExtra("barWebsite"));
        i.putExtra("travelTime", tvDistanceDuration.getText().toString());

        startActivity(i);
        try {
            //Launch the Reservation details Activity
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            alert.showAlertDialog(TravelDetailsActivity.this, "Activity Launch Error",
                    "Could not find Activity: Travel details activity",
                    false);
        }
    }

    // Drawing Start and Stop locations
    private void drawStartStopMarkers() {

        for (int i = 0; i < markerPoints.size(); i++) {

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(markerPoints.get(i));

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (i == 0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (i == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

// Add new marker to the Google Map Android API V2
            googleMap.addMarker(options);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";

        if (rbDriving.isChecked()) {
            mode = "mode=driving";
            mMode = 0;
        } else if (rbBiCycling.isChecked()) {
            mode = "mode=bicycling";
            mMode = 1;
        } else if (rbWalking.isChecked()) {
            mode = "mode=walking";
            mMode = 2;
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Retutn the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception getting url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * Background Async Task to Fetches data from url passed
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.showProcessDialog(TravelDetailsActivity.this, "", "Loading Map...");
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         **/
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // dismiss the dialog after getting all products
            pDialog.dismissProcessDialog();

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) { // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));


                    LatLng position = new LatLng(lat, lng);
                    points.add(position);


                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);

                // Changing the color polyline according to the mode
                if (mMode == MODE_DRIVING)
                    lineOptions.color(Color.RED);
                else if (mMode == MODE_BICYCLING)
                    lineOptions.color(Color.GREEN);
                else if (mMode == MODE_WALKING)
                    lineOptions.color(Color.BLUE);
            }

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);

        }

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        googleMap = map;

        setUpMap();

    }


    public void setUpMap() {
        // Enable MyLocation Button in the Map
//        googleMap.setMyLocationEnabled(true);


        LatLng currentLocation = new LatLng(gps.getLatitude(), gps.getLongitude());
        LatLng barLocation = new LatLng(Double.parseDouble(this.getIntent().getStringExtra("barLocatonLat")), Double.parseDouble(this.getIntent().getStringExtra("barLocationLng")));

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLocation, 13);
        googleMap.animateCamera(yourLocation);
        // Adding new item to the ArrayList
        markerPoints.add(currentLocation);
        markerPoints.add(barLocation);

        // Draws Start and Stop markers on the Google Map
        drawStartStopMarkers();

// Setting onclick event listener for the map
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });
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
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
