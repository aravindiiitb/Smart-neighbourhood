package com.example.sunshine.sunshine;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final static String EXTRA_MESSAGE = "Aravind Is good boy";

    private static final String TAG = "MyActivity";
    public String mood="";
    static InputStream in=null;
    public Double lat=0.0,lon=0.0;

    private static String url_get_data = "http://api.openweathermap.org/data/2.5/weather?lat=";

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    RelativeLayout relativeLayout;
    int images[]  = {R.drawable.cloudy1, R.drawable.rainy, R.drawable.sunny,R.drawable.snow1,R.drawable.clear,R.drawable.misty};
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);

        relativeLayout = (RelativeLayout) findViewById(R.id.mylayout);
        buildGoogleApiClient();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat=mLastLocation.getLatitude();
            lon= mLastLocation.getLongitude();
            //relativeLayout.setBackgroundResource(images[new Random().nextInt(3)]);
            Toast.makeText(getApplicationContext(), lat.toString() + " : " + lon.toString(),
                    Toast.LENGTH_LONG).show();
            //Data d = new Data();
            //d.doInBackground();
            new Data().execute(url_get_data);



        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class Data extends AsyncTask<String,String,String> {
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), mood,
                    Toast.LENGTH_LONG).show();
            if (mood.equals("Sunny")) {
                relativeLayout.setBackgroundResource(images[2]);
            }
            else if (mood.equals("Clear"))
                relativeLayout.setBackgroundResource(images[4]);
            else if (mood.equals("Rain") || mood.equals("Drizzle"))
                relativeLayout.setBackgroundResource(images[1]);
            else if (mood.equals("Snow"))
                relativeLayout.setBackgroundResource(images[3]);
            else if (mood.equals("Clouds"))
                relativeLayout.setBackgroundResource(images[0]);
            else if (mood.equals("Mist"))
                relativeLayout.setBackgroundResource(images[5]);
            else {
                relativeLayout.setBackgroundResource(images[0]);
            }
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            JSONParser jsonParser = new JSONParser();
            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_get_data + lat.toString() + "&lon=" + lon.toString() + "&APPID=dacdb3ad675b26c6234ae012030677b3", "POST", params);
                //JSONObject json = jsonParser.makeHttpRequest("http://api.openweathermap.org/data/2.5/weather?lat=64.5000&lon=-97.5000&APPID=dacdb3ad675b26c6234ae012030677b3","POST",params);
                // check log cat fro response
                Log.d("Create Response1", json.toString());
                JSONArray weather = json.getJSONArray("weather");
                Log.d("Create Response4", weather.toString());
                mood = weather.getJSONObject(0).get("main").toString();
                Log.d("Create Response4", mood);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void login (View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
    }

    public void signUp (View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
