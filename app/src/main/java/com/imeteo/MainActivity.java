package com.imeteo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.imeteo.model.CItyDao;
import com.imeteo.model.DAO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    public static final int SAVE_TO_FILE = 0;
    public static final int SAVE_TO_DB = 1;
    private TextView txtCityName;

    private GoogleMap map;
    private LocationManager locationManager;

    private HashMap<String, String> cities;

    private static final String API_KEY = "6c55f38f9cba0a753a2ecf7f5fb56788";
    private static final String CELSIUS = "metric";

    EditText city;
    TextView info;
    Button goBtn;
    private TextView mapTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCityName = (TextView) findViewById(R.id.city_name);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        city = (EditText) findViewById(R.id.city_edt);
        info = (TextView) findViewById(R.id.info_tv);
        goBtn = (Button) findViewById(R.id.go_btn);
        mapTemp = (TextView) findViewById(R.id.map_temp);


        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiDataTask task = new GetApiDataTask();
                //TODO pass parameters
                task.execute(city.getText().toString().trim());
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        We should explicitly check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map = googleMap;
//        This sets find my location icon in upper right if gps is turned on
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

//                Check if gps is turned on
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                } else {//if gps is not turned on

//                    Goes to location setting to turn on gps
                    Intent callGPSSettingIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                }

                return false;
            }
        });

        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0) {
                    txtCityName.setText(addresses.get(0).getLocality());
                    new GetApiDataTask().execute(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                }
            }
        });
    }

    private void saveConfig() {
        SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = config.edit();
        editor.putInt("save_to_file", SAVE_TO_FILE);
        editor.putInt("save_to_db", SAVE_TO_DB);
        editor.commit();
    }


    private class GetApiDataTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String temp = "";
            String info = "";

            String numberReg = "\\d+.\\d+";
            String lat;
            String lon;

            try {

                URL url;
                if (!params[0].matches(numberReg)) {

//                api.openweathermap.org/data/2.5/weather?q={city name},{country code}
                    url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + params[0] + "&APPID=" + API_KEY + "&units=" + CELSIUS);

                } else {
                    lat = params[0];
                    lon = params[1];
//                api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}
                    url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=" + API_KEY + "&units=" + CELSIUS);
                }

//                TODO
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder body = new StringBuilder();
                while (sc.hasNextLine()) {
                    body.append(sc.nextLine());
                }
                info = body.toString();

                JSONObject jsonData = new JSONObject(info);
                JSONObject observation = (JSONObject) jsonData.get("main");
                temp = observation.getString("temp");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return temp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            info.setText(s);
            mapTemp.setText(s);
            if (txtCityName.getText()!=null && s!=null && txtCityName.length()>0) {
                new SaveChosenCityFromMap().execute(txtCityName.getText().toString(), s);
            }
        }
    }


    class SaveChosenCityFromMap extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {

            String cityName = params[0];
            String cityTemp = params[1];

            DAO saveCity = new CItyDao(getApplicationContext());
            ((CItyDao)saveCity).open();
            saveCity.saveCityInfo(cityName,Double.valueOf(cityTemp));

            return null;
        }
    }

    private class LoadCitiesTask extends AsyncTask<Void, Void, HashMap<String, String>>{

        private HashMap<String, String> cities;

        @Override
        protected HashMap<String, String> doInBackground(Void... params) {

            String info;
            try{
                URL urlByCoordinates = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + "42.69" + "&lon=" + "23.32" + "&APPID=" + API_KEY + "&units=" + CELSIUS);

                HttpURLConnection connection = (HttpURLConnection) urlByCoordinates.openConnection();
                connection.connect();

                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder body = new StringBuilder();
                while(sc.hasNextLine()){
                    body.append(sc.nextLine());
                }
                info = body.toString();

                JSONArray array = new JSONArray(info);

                for (int i = 0; i < info.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String city = object.getString("name");
                    String country = object.getString("country");

                    cities.put(country, city);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
            super.onPostExecute(stringStringHashMap);
            MainActivity.this.cities = stringStringHashMap;
        }
    }

}
