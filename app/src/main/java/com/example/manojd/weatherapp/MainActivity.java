package com.example.manojd.weatherapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manojd.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView darkSky;
    ImageView iconImageView;
    private static String TAG = "MainActivity";
    private Weather weather;
    double latitude; //= 18.5204;//37.8267;
    double longitude; //= 73.8567; //-122.4233;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"oncreate called");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        getNewLocation();
    }

    //=============================================Getting current location================================================================
    private void getNewLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*3600,5,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        Log.v("latitude",""+latitude);
        longitude = location.getLongitude();
        Log.v("longitude",""+longitude);

        getForecast(latitude,longitude);
        // lat long
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //========================================getting current location end=================================================================

    private void getForecast(double latitude, double longitude) {

        Log.v(TAG,"Longitude"+longitude);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        darkSky =findViewById(R.id.darkSkyAttribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        iconImageView = findViewById(R.id.iconImageView);

        String apiKey = "61fba02cd448ca591d5b77cab2b5113b";
        String forecastURL = "https://api.darksky.net/forecast/"+apiKey+"/"+latitude+","+longitude;

        if(isNetworkAvailable()) {
            Log.v(TAG,"isNetworkAvailable called");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v(TAG,"onFailure called");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v("MainActivity", jsonData);
                        if (response.isSuccessful()) {
                            weather = getCurrentDetails(jsonData);
                            final Weather displayWeather = new Weather(
                                    weather.getLocationLabel(),
                                    weather.getIcon(),
                                    weather.getSummary(),
                                    weather.getTime(),
                                    weather.getTemperature(),
                                    weather.getHumidity(),
                                    weather.getPrecipChance(),
                                    weather.getTimeZone()
                            );
                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }

                    } catch (IOException ex) {
                        Log.e(TAG,"IO Exception caught",ex);
                    } catch (JSONException ex){
                        Log.e(TAG,"JSONException caught",ex);
                    }

                }
            });
        }
        else {
            Log.v(TAG,"isNetWorkAvailable is not called");
        }
    }

    private Weather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG,"From JSON "+timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        Weather weather = new Weather();
        weather.setIcon(currently.getString("icon"));
        weather.setLocationLabel("Alcatraz Island, CA");
        weather.setSummary(currently.getString("summary"));
        weather.setHumidity(currently.getDouble("humidity"));
        weather.setPrecipChance(currently.getDouble("precipProbability"));
        weather.setTemperature(currently.getDouble("temperature"));
        weather.setTime(currently.getLong("time"));
        weather.setTimeZone(timezone);

        Log.d(TAG,weather.getFormattedTime());
        return weather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(info != null && info.isConnected())
            isAvailable = true;
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network")
                    .setMessage("Network is not available,Please try later!!")
                    .setPositiveButton("OK",null);
            builder.create().show();
            //Toast.makeText(this,"Network is not available,Please try later!!",Toast.LENGTH_SHORT).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AndroidDialogFragment dialogFragment = new AndroidDialogFragment();
        dialogFragment.show(getFragmentManager(),"ErrorDialog");
    }

    public void refreshForecast(View view){
        Toast.makeText(this,"Refreshing data",Toast.LENGTH_SHORT).show();
        getForecast(latitude,longitude);
    }

}
