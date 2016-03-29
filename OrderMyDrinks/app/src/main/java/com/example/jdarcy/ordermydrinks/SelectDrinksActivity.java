package com.example.jdarcy.ordermydrinks;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class SelectDrinksActivity extends AppCompatActivity {

    //Button to browse to next activity
    Button reviewReservationButton;

    //Button to next Activity intent
    FloatingActionButton fabButton;

    // Mobile Service Client reference
    private MobileServiceClient mClient;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //Progress Dialog Manager
    ProgressDialogManager pDialog = new ProgressDialogManager();

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;
    //Mobile Service Table used to access data
    private MobileServiceTable<Drinks> mDrinksTable;

    //Adapter to sync the items list with the view
    private DrinksListAdapter mAdapter;

    ListView listViewDrinks;
    TextView numDrinksListItem;

    int numDrinks = 0;
    private String order;

    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;


    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drinks);

        setUpDrawer();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the Mobile Service Client instance, using the provided
        try {

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://drinksorder.azure-mobile.net/",
                    "rWgrsUsZRLBjNawpbnlpcJNmpKDpOn96",
                    this).withFilter(new ProgressFilter());

            // Get the Mobile Service Table instance to use
            mDrinksTable = mClient.getTable(Drinks.class);

            // Create an adapter to bind the items with the view
            mAdapter = new DrinksListAdapter(this, R.layout.drinks_list_item);
            listViewDrinks = (ListView) findViewById(R.id.listViewDrinks);
            listViewDrinks.setAdapter(mAdapter);

            //Floating Action button click to next activity
            fabButton = (FloatingActionButton) findViewById(R.id.fab);
            fabButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fabButtonClick(listViewDrinks);
                }
            });

            // Load the items from the Mobile Service
            refreshItemsFromTable();
        } catch (MalformedURLException e) {
            alert.showAlertDialog(SelectDrinksActivity.this, "Azure Connection Error: Bad URL ", e.toString(), false);
        } catch (Exception e) {
            alert.showAlertDialog(SelectDrinksActivity.this, "Azure Connection Error", e.toString(), false);
        }

        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        listViewDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem

                String drink = ((TextView) view.findViewById(R.id.checkDrinksItem)).getText().toString();

                String numOfDrinks = ((TextView) view.findViewById(R.id.numDrinks)).getText().toString();

                try {
                    numDrinks = Integer.parseInt(numOfDrinks);
                    ((TextView) view.findViewById(R.id.numDrinks)).setText(Integer.toString(numDrinks + 1));

                } catch (NumberFormatException e) {
                    System.out.println("Could not parse " + e);
                }
            }
        });


    }

    public void fabButtonClick(ListView view) {

        /** get all values of the TextView-Field in the drinks list and add them to the order*/
        String order = "";
        for (int i = 0; i <= view.getLastVisiblePosition() - view.getFirstVisiblePosition(); i++) {
            if (!((TextView) view.getChildAt(i).findViewById(R.id.numDrinks)).getText().toString().equals("0")) {
                order = order + ((TextView) view.getChildAt(i).findViewById(R.id.checkDrinksItem)).getText() + ": " +
                        ((TextView) view.getChildAt(i).findViewById(R.id.numDrinks)).getText() + ", ";
            }
        }

        Toast.makeText(SelectDrinksActivity.this, order, Toast.LENGTH_SHORT).show();


        //define a new Intent for the Reservation details
        Intent i = new Intent(getApplicationContext(), SubmitOrderActivity.class);


        i.putExtra("order", order);
        i.putExtra("userName", this.getIntent().getStringExtra("userName"));
        i.putExtra("userEmail", this.getIntent().getStringExtra("userEmail"));
        i.putExtra("guests", this.getIntent().getStringExtra("guests"));
        i.putExtra("barName", this.getIntent().getStringExtra("barName"));
        i.putExtra("barAddress", this.getIntent().getStringExtra("barAddress"));
        i.putExtra("barPhoneNumber", this.getIntent().getStringExtra("barPhoneNumber"));
        i.putExtra("barLocatonLat", this.getIntent().getStringExtra("barLocatonLat"));
        i.putExtra("barLocationLng", this.getIntent().getStringExtra("barLocationLng"));
        i.putExtra("barWebsite", this.getIntent().getStringExtra("barWebsite"));
        i.putExtra("travelTime", this.getIntent().getStringExtra("travelTime"));

        Toast.makeText(this, this.getIntent().getStringExtra("travelTime") + " clicked !!!!!", Toast.LENGTH_SHORT).show();

        startActivity(i);
        try {
            //Launch the Submit Order Activity
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            alert.showAlertDialog(SelectDrinksActivity.this, "Activity Launch Error",
                    "Could not find Activity: SubmitOrderActivity",
                    false);
        }
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Drinks> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Drinks item : results) {
                                mAdapter.add(item);
                                order = item.getText();

                            }
                        }
                    });
                } catch (final Exception e) {
                    alert.showAlertDialog(SelectDrinksActivity.this, "ERROR",
                            "Error Loading drinks list: " + e, false);
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<Drinks> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mDrinksTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    /**
     * Run an ASync task on the corresponding executor
     *
     * @param task task to execute
     * @return task.execute
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {


        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    pDialog.showProcessDialog(SelectDrinksActivity.this, "", "Loading Drinks List from Azure...");
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pDialog.dismissProcessDialog();
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
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
                refreshItemsFromTable();
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