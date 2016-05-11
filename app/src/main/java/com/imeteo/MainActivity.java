package com.imeteo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    private static final String API_KEY = "6c55f38f9cba0a753a2ecf7f5fb56788";
    private static final String CELSIUS = "metric";

    EditText city;
    TextView info;
    Button goBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (EditText) findViewById(R.id.city_edt);
        info = (TextView) findViewById(R.id.info_tv);
        goBtn = (Button) findViewById(R.id.go_btn);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiDataTask task =  new GetApiDataTask();
                //TODO pass parameters
                task.execute("hello");
            }
        });

    }

    private class GetApiDataTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... params) {

            String temp = "";
            String info = "";
            try {
//                api.openweathermap.org/data/2.5/weather?q={city name},{country code}
                URL urlByCity = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + "London" + "," + "uk" + "&APPID=" + API_KEY + "&units=" + CELSIUS);

//                api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}
                URL urlByCoordinates = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + "42.69" + "&lon=" + "23.32" + "&APPID=" + API_KEY + "&units=" + CELSIUS);

//                TODO
                HttpURLConnection connection = (HttpURLConnection) urlByCoordinates.openConnection();
                connection.connect();

                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder body = new StringBuilder();
                while(sc.hasNextLine()){
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
        }
    }


}
