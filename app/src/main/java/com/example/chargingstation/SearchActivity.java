package com.example.chargingstation;

import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.*;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    String longitude;
    String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = findViewById(R.id.searchActivityButton);
        searchButton.setOnClickListener(e -> {
            EditText longitudeText = findViewById(R.id.searchLongitude);
            longitude = longitudeText.getText().toString();
            EditText latitudeText = findViewById(R.id.searchLatitude);
            latitude = latitudeText.getText().toString();
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.execute();
        });
    }

    public class SearchQuery extends AsyncTask <String, Integer, String> {

        String title;
        String phone;
        String resultLong;
        String resultLat;

        @Override
        protected String doInBackground(String... args){
            String ret = null;
            String queryURL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=CA&latitude=" + latitude + "&longitude=" + longitude + "&maxresults=10";

            try{
                //connect to server
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();
                //turn everything into the worlds longest string
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null){
                    builder.append(line + "\n");
                }
                String results = builder.toString();
                JSONArray resultArray = new JSONArray(results);

                for(int i = 0; i < resultArray.length(); i++){
                    JSONObject result = resultArray.getJSONObject(i);
                    JSONObject operatorObject = result.getJSONObject("OperatorInfo");
                    if(operatorObject != null){
                        String titleTemp = operatorObject.getString("Title");
                        if(titleTemp != null){
                            title = titleTemp;
                        }else {
                            title = "No name";
                        }
                    }else {
                        title = "No name";
                    }
                        Log.e("LOOK HERE", title);

                }

            }catch(MalformedURLException mfe){
                ret = "Malformed URL exception";
            }catch(IOException ioe){
                ret = "IO Exception. Is the wifi connected?";
            } catch (JSONException e) {
                ret = "JSON issues";
            }

            return ret;
        }

        public void onProgressUpdate(Integer... value){

        }

        public void onPostExecute(String value){

        }
    }
}
