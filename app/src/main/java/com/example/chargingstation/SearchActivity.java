package com.example.chargingstation;

import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    String longitude;
    String latitude;
    EditText longitudeText;
    EditText latitudeText;
    SharedPreferences sharedPrefs;
    ArrayList<ChargingStation> stations = new ArrayList<>();
    BaseAdapter myAdapter;
    View thisRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = findViewById(R.id.searchActivityButton);

        longitudeText = findViewById(R.id.searchLongitude);
        longitude = longitudeText.getText().toString();
        latitudeText = findViewById(R.id.searchLatitude);
        latitude = latitudeText.getText().toString();

        sharedPrefs = getSharedPreferences("SavedCoordinates", Context.MODE_PRIVATE);
        String previousLong = sharedPrefs.getString("searchLongitude", longitude);
        longitudeText.setText(previousLong);

        View progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ListView list = findViewById(R.id.searchResults);
        myAdapter = new MyListAdapter();
        list.setAdapter(myAdapter);
        list.setOnItemClickListener((lv, vw, pos, id) -> {
            ChargingStation station = stations.get(pos);
            
        });

        searchButton.setOnClickListener(e -> {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.execute();
        });


    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("longitudeSearch", longitude);
        editor.commit();
    }

    public class SearchQuery extends AsyncTask <String, Integer, String> {

        String title;
        String phone;
        double resultLong;
        double resultLat;

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
                    if(!result.isNull("OperatorInfo")) {
                        JSONObject operatorObject = result.getJSONObject("OperatorInfo");
                        String titleTemp = operatorObject.getString("Title");
                        if (titleTemp != null) {
                            title = titleTemp;
                        } else {
                            title = "No name";
                        }
                        String phoneTemp = operatorObject.getString("PhonePrimaryContact");
                        if(phoneTemp != null){
                            phone = phoneTemp;
                        }else {
                            phone = "No phone number";
                        }

                    }else {
                        title = "No name";
                        phone = "No phone number";
                    }
                    if(!result.isNull("AddressInfo")){
                        JSONObject addressObject = result.getJSONObject("AddressInfo");
                        resultLong = addressObject.getDouble("Longitude");
                        resultLat = addressObject.getDouble("Latitude");
                    }else {
                        resultLong = 0;
                        resultLat = 0;
                    }

                    stations.add(new ChargingStation(title, resultLong, resultLat, phone));
                }

            }catch(MalformedURLException mfe){
                ret = "Malformed URL exception";
                Log.e(ret, mfe.getMessage());
            }catch(IOException ioe){
                ret = "IO Exception. Is the wifi connected?";
                Log.e(ret, ioe.getMessage());
            } catch (JSONException e) {
                ret = "JSON issues";
                Log.e(ret, e.getMessage());
            }

            return ret;
        }

        public void onProgressUpdate(Integer... value){
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String value){
            myAdapter.notifyDataSetChanged();
        }
    }

    private class MyListAdapter extends BaseAdapter{
        @Override
        public int getCount(){return stations.size();}
        @Override
        public ChargingStation getItem(int position){return stations.get(position);}
        @Override
        public long getItemId(int position){return position;}
        @Override
        public View getView(int position, View recycled, ViewGroup parent){
            thisRow = recycled;

            ChargingStation station = getItem(position);

            if(recycled == null){
                thisRow = getLayoutInflater().inflate(R.layout.list_chargingsearch, null);
            }
            TextView titleResult = thisRow.findViewById(R.id.chargingTitle);
            titleResult.setText(station.getTitle());

            return thisRow;
        }
    }
}
