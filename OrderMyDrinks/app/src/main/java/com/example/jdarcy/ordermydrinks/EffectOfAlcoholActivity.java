package com.example.jdarcy.ordermydrinks;
/**
 * EffectOfAlcohol Activity
 * Activity to compare Accelerometer readings to and from destination
 * calculates time to and from destination
 */

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class EffectOfAlcoholActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastTime = 0;
    private float lastX, lastY, lastZ;
    private float effectOfAlcohol;
    private static final int THRESHOLD = 100; //used to see whether a shake gesture has been detected or not.
    TextView accelerometerFromBar;
    TextView accelerometerToBar;
    TextView journeyTimeToBar;
    TextView journeyTimeFromBar;

    private int stoppedClicked = 0;
    private int startClicked = 0;
    private TextView tempTextView; //Temporary TextView
    private Button tempBtn; //Temporary Button
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours, minutes, seconds, milliseconds;
    private long secs, mins, hrs, msecs;
    private boolean stopped = false;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_of_alcohol);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton start = (ImageButton) findViewById(R.id.btn_accelerometer_start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startAccelerometer();
            }

        });


        ImageButton stop = (ImageButton) findViewById(R.id.btn_accelerometer_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopAccelerometer();
            }

        });
        accelerometerToBar = (TextView) findViewById(R.id.accelerometer_to_bar);
        accelerometerFromBar = (TextView) findViewById(R.id.accelerometer_from_bar);
        journeyTimeToBar = ((TextView) findViewById(R.id.time_to_bar));
        journeyTimeFromBar = ((TextView) findViewById(R.id.time_from_bar));


    }

    public void startAccelerometer() {
        showStopButton();


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //reset text boxes after trip to and from bar
        if (startClicked == 0) {
            //Clear all text boxes
            accelerometerToBar.setText("");
            accelerometerToBar.setVisibility(View.VISIBLE);
            accelerometerFromBar.setText("");
            accelerometerFromBar.setVisibility(View.VISIBLE);

            journeyTimeFromBar.setText("");
            journeyTimeFromBar.setVisibility(View.VISIBLE);
            journeyTimeToBar.setText("");
            journeyTimeToBar.setVisibility(View.VISIBLE);
        }

        startClicked++;

        if (stopped) {
            startTime = System.currentTimeMillis() - elapsedTime;
        } else {
            startTime = System.currentTimeMillis();
        }

        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    public void stopAccelerometer() {
        hideStopButton();
        stoppedClicked++;

        if (stoppedClicked == 1) {
            accelerometerToBar.setText(String.valueOf(effectOfAlcohol));
            accelerometerToBar.setVisibility(View.VISIBLE);
            journeyTimeToBar.setText(hours + ":" + minutes + ":" + seconds);

        } else if (stoppedClicked == 2) {
            accelerometerFromBar.setText(String.valueOf(effectOfAlcohol));
            accelerometerFromBar.setVisibility(View.VISIBLE);
            journeyTimeFromBar.setText(hours + ":" + minutes + ":" + seconds);
            stoppedClicked = 0;
            startClicked = 0;
        }
        stopped = false;
        effectOfAlcohol = 0;
        mSensorManager.unregisterListener(this);

    }
    /*
        The system’s sensors are sensitive, when holding a device, it is constantly in motion, no matter
        how steady your hand is. We don’t need all this data. We store the system’s current time
        (in milliseconds) and check if more than 100 milliseconds have passed since the last time
        onSensorChanged was invoked.
     */

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastTime) > 100) {
                long diffTime = (currentTime - lastTime);
                lastTime = currentTime;
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                TextView text = (TextView) findViewById(R.id.number);
                text.setText(String.valueOf(speed));
                if (speed > THRESHOLD) {
                    RelativeLayout ball = (RelativeLayout) findViewById(R.id.ball);
                    Animation a = AnimationUtils.loadAnimation(this, R.anim.move_down_ball_first);
                    ball.setVisibility(View.INVISIBLE);
                    ball.setVisibility(View.VISIBLE);
                    ball.clearAnimation();
                    ball.startAnimation(a);

                }
                lastX = x;
                lastY = y;
                lastZ = z;
                effectOfAlcohol = speed + effectOfAlcohol;

                if (startClicked == 1) {
                    accelerometerToBar.setText(Float.toString(effectOfAlcohol));
                    accelerometerToBar.setVisibility(View.VISIBLE);
                } else if (startClicked == 2) {
                    accelerometerFromBar.setText(Float.toString(effectOfAlcohol));
                    accelerometerFromBar.setVisibility(View.VISIBLE);
                }


            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void showStopButton() {

        ((ImageButton) findViewById(R.id.btn_accelerometer_start)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.btn_accelerometer_stop)).setVisibility(View.VISIBLE);
    }

    private void hideStopButton() {
        ((ImageButton) findViewById(R.id.btn_accelerometer_start)).setVisibility(View.VISIBLE);
        ((ImageButton) findViewById(R.id.btn_accelerometer_stop)).setVisibility(View.GONE);
    }

    private void updateTimer(float time) {
        secs = (long) (time / 1000);
        mins = (long) ((time / 1000) / 60);
        hrs = (long) (((time / 1000) / 60) / 60);
/* Convert the seconds to String * and format to ensure it has * a leading zero when required */
        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

/* Convert the minutes to String and format the String */
        mins = mins % 60;
        minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        } /* Convert the hours to String and format the String */
        hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }


        if (startClicked == 1) {
            journeyTimeToBar.setText(hours + ":" + minutes + ":" + seconds);
        } else if (startClicked == 2) {
            journeyTimeFromBar.setText(hours + ":" + minutes + ":" + seconds);
        }

    }

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };
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
                startClicked = 0;
                startAccelerometer();
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
