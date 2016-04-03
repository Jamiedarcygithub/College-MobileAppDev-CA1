package com.example.jdarcy.ordermydrinks;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    //Button to browse to next activity
    FloatingActionButton floatyButton;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    private EditText editTextUsername, editTextEmail;
    private TextInputLayout inputLayoutName, inputLayoutEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle("OrderMyDrinks");

        floatyButton = (FloatingActionButton) findViewById(R.id.floaty);
        floatyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validateUserDetails();
                //floatyClick();
            }
        });

    }

    /** Next Button Click Event */
    public void validateUserDetails() {

        if (validateName() && validateEmail()) {

            final EditText guestsField = (EditText) findViewById(R.id.EditNumberOfGuests);
            String guests = guestsField.getText().toString();

            //define a new Intent for the Places Activity
            Intent i = new Intent(getApplicationContext(), SelectPlaceActivity.class);

            i.putExtra("userName", editTextUsername.getText().toString());
            i.putExtra("userEmail", editTextEmail.getText().toString());
            i.putExtra("guests", guests);
            startActivity(i);
            try {
                //Launch the Reservation details Activity
                startActivity(i);
            } catch (android.content.ActivityNotFoundException ex) {
                alert.showAlertDialog(MainActivity.this, "Activity Launch Error",
                        "Could not find Activity: Select Place Activity",
                        false);
            }


        }
    }

    private boolean validateName() {
        if (editTextUsername.getText().toString().isEmpty()) {
            inputLayoutName.setError(getString(R.string.username_validation_msg));
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String pwd = editTextEmail.getText().toString().trim();
        if (pwd.length() < 8 ) {
            inputLayoutEmail.setError(getString(R.string.email_validation_msg));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }

    /**
     * Method to load menu on Activity create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        return super.onOptionsItemSelected(item);
    }

}

