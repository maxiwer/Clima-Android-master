package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherController extends AppCompatActivity {
    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "ae9b1a399fbaa801e51768d5e04ce0d7";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // Setting location provider: whether it is network or GPS.
//    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    ImageButton changeCityButton;

    // LocationManager starts and stops updating requesting location updates
    // LocationListener will be notified if location is actually changed
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);

        // TODO: Add an OnClickListener to the changeCityButton here:

    }

    // onResume() - OS runs the activity in foreground, before user's interacting.
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "onResume() called");
        Log.d("Clima", "Calling method: getWeatherForCurrentLocation()");
        getWeatherForCurrentLocation();
    }

    // TODO: Add getWeatherForNewCity(String city) here:

    // getWeatherForCurrentLocation()
    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Implementing LocationListener interface
        locationListener = new LocationListener() {
            @Override
            //Callback when location has changed.
            public void onLocationChanged(Location location) {
                Log.d("Clima", "onLocationChanged() callback");

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("Clima", "Latitude is: " + latitude);
                Log.d("Clima", "Longitude is: " + longitude);

                // a class from "loopj async http library", where we put params to request GET to server
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                // the requesting method
                someNetworking(params);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            // Callback when internet or GPS are disabled.
            public void onProviderDisabled(String s) {
                Log.d("Clima", "onProviderDisabled() callback");
            }
        };

        //starting getting updates for location via locationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        // if location change, this method will call onLocationChanged().
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    //Grant user's permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "Permission granted");
                getWeatherForCurrentLocation();
            } else {
                Log.d("Clima", "Permission denied");
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void someNetworking(RequestParams params) {
        // the class below is used for requesting asynchronous HTTP requests
        AsyncHttpClient httpClient = new AsyncHttpClient();
        // implementing GET request
        httpClient.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            // if request was successful
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Clima", "onSuccess() method is called: " + response.toString());
                // Getting Java object that parsed from JSON
                WeatherDataModel weatherDataModel = WeatherDataModel.fromJSON(response);
            }

            // if request was unsuccessful
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Clima", "Fail: " + e.toString());
                Log.d("Clima", "Status code: " + statusCode);
                Toast.makeText(WeatherController.this, "Request falied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // TODO: Add updateUI() here:


    // TODO: Add onPause() here:
}
