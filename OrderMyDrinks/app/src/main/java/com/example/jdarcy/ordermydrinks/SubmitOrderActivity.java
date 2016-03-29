package com.example.jdarcy.ordermydrinks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;

/**
 * Submit Order Activity
 * Class to submit order details to Azure tables
 */
public class SubmitOrderActivity extends AppCompatActivity {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Bookings> mBookingsTable;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    Toolbar toolbar;

    //Button to save details to azure
    FloatingActionButton fabButton;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_order_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://drinksorder.azure-mobile.net/",
                    "rWgrsUsZRLBjNawpbnlpcJNmpKDpOn96",
                    this);


        } catch (MalformedURLException e) {
            alert.showAlertDialog(SubmitOrderActivity.this, "Azure Connection Error: Bad URL ", e.toString(), false);
        } catch (Exception e) {
            alert.showAlertDialog(SubmitOrderActivity.this, "Azure Connection Error", e.toString(), false);
        }

        //Add details to summary screen

        ((TextView) findViewById(R.id.user_name)).setText(this.getIntent().getStringExtra("userName"));
        ((TextView) findViewById(R.id.guests)).setText(this.getIntent().getStringExtra("guests"));
        ((TextView) findViewById(R.id.time)).setText(this.getIntent().getStringExtra("travelTime"));
        ((TextView) findViewById(R.id.barname)).setText(this.getIntent().getStringExtra("barName"));
        ((TextView) findViewById(R.id.address)).setText(this.getIntent().getStringExtra("barAddress"));
        ((TextView) findViewById(R.id.order)).setText(this.getIntent().getStringExtra("order"));

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItemToAzure();
            }
        });

    }


    /**
     * Add a new item to azure tables
     */
    public void addItemToAzure() {
        if (mClient == null) {
            return;
        }
        Bookings itemBooking = new Bookings();
        itemBooking.setName(this.getIntent().getStringExtra("userName"));
        itemBooking.setTime(10);
        itemBooking.setOrder(this.getIntent().getStringExtra("order"));
        itemBooking.setGuests(Integer.parseInt(this.getIntent().getStringExtra("guests")));
        itemBooking.setBarAddress(this.getIntent().getStringExtra("barName"));
        itemBooking.setBarName(this.getIntent().getStringExtra("barEmail"));
        itemBooking.setEmail(this.getIntent().getStringExtra("userEmail"));

        mClient.getTable(Bookings.class).insert(itemBooking, new TableOperationCallback<Bookings>() {
            public void onCompleted(Bookings entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubmitOrderActivity.this);
                    builder.setMessage("Items submitted to Azure. Do you want to test effect of alcohol?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                    //alert.showAlertDialog(SubmitOrderActivity.this, "Azure Insert", " Passed", true);
                } else {
                    // Insert failed
                    alert.showAlertDialog(SubmitOrderActivity.this, "Azure Insert", "Failed" + exception.toString(), false);
                }
            }
        });
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent i;
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    i = new Intent(getApplicationContext(), EffectOfAlcoholActivity.class);
                    startActivity(i);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };


    /**
     * Method to load menu on Activity create
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * Method to handle options menu
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg = "";

        switch (item.getItemId()) {

            case R.id.search:
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                break;

            case R.id.edit:
                msg = getString(R.string.edit);
                break;

            case R.id.settings:
                msg = getString(R.string.settings);
                break;

            case R.id.Exit:
                finish();
                break;
        }

        Toast.makeText(this, msg + " clicked !!!!!", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
    private void setUpDrawer() {

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }
}
