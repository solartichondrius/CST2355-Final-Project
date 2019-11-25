package com.example.chargingstation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import com.example.newsheadlines.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * This class allows the user to search for a charging station based on longitude and latitude.
 * The user can then select a result and either add to favorites, or view the location in google maps.
 */

public class SearchActivity extends AppCompatActivity {

    String longitude;
    String latitude;
    EditText longitudeText;
    EditText latitudeText;
    SharedPreferences sharedPrefs;
    ArrayList<ChargingStation> stations = new ArrayList<>();
    BaseAdapter myAdapter;
    View thisRow;
    SQLiteDatabase db;
    ProgressBar progressBar;

    /**
     * This method is called when the activity is created.
     * It creates a toolbar to be used later.
     * It creates a search button, which has an on click listener.
     * It creates a database helper and retrieves an existing database.
     * It creates a listview which will populate when the search results are retrieved.
     * It looks for previously searched coordinates and prefills the edit texts.
     * A listener for the listview contains a lambda which calls another method.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar tbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(tbar);

        Button searchButton = findViewById(R.id.searchActivityButton);

        DatabaseOpenHelper dbOpener = new DatabaseOpenHelper(this);
        db = dbOpener.getReadableDatabase();


        longitudeText = findViewById(R.id.searchLongitude);
        latitudeText = findViewById(R.id.searchLatitude);


        sharedPrefs = getSharedPreferences("SavedCoordinates", Context.MODE_PRIVATE);
        String previousLong = sharedPrefs.getString("searchLongitude", "Longitude");
        String previousLat = sharedPrefs.getString("searchLatitude", "Latitude");
        latitudeText.setText(previousLat);
        longitudeText.setText(previousLong);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);



        ListView list = findViewById(R.id.searchResults);
        myAdapter = new MyListAdapter();
        list.setAdapter(myAdapter);
        list.setOnItemClickListener((lv, vw, pos, id) -> {
            ChargingStation station = stations.get(pos);
                resultClicked(station);
        });

        searchButton.setOnClickListener(e -> {
            longitude = longitudeText.getText().toString();
            latitude = latitudeText.getText().toString();
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.execute();
        });


    }

    /**
     * When the app or activity is left (but not killed), the search is saved for the next time the activity is used.
     * The longitude and latitude are saved.
     */
    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("searchLongitude", longitude);
        editor.putString("searchLatitude", latitude);
        editor.commit();
    }

    /**
     * Creates a toolbar menu
     * @param menu The menu to be created
     * @return true;
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_charging, menu);
        return true;
    }

    /**
     * Contains a conditional statement which handles where to go when an item is clicked.
     * Each icon goes to a different activity.
     * @param item The item selected
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.aboutItem:
                Intent goToAbout = new Intent(SearchActivity.this, AboutActivity.class);
                startActivity(goToAbout);
                break;
            case R.id.favoriteItem:
                Intent goToFavorite = new Intent(SearchActivity.this, FavoriteActivity.class);
                startActivity(goToFavorite);
                break;
            case R.id.searchItem:
                Intent goToSearch = new Intent(SearchActivity.this, SearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.homeItem:
                Intent goHome = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(goHome);
                break;
        }

        return true;
    }

    /**
     * Handles when an item is pressed in the listview.
     * An alert dialog pops up with the information about the charging station.
     * @See ChargingStation.class.
     * The positive button adds the selected station to the favorites database.
     * The neutral button opens the google maps app and drops a pin at the location of the station.
     * The negative button closes the dialog.
     * @param station The station in the list view that was selected.
     */
    public void resultClicked(ChargingStation station){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Name: " + station.getTitle() + "\n" + "Latitude: " + station.getLatitude() + "\n" + "Longitude: " + station.getLongitude() + "\n" + "Phone: " + station.getPhone());
        builder.setPositiveButton("Add to Favorites", new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add to favorites
                ContentValues newRowValues = new ContentValues();
                newRowValues.put(DatabaseOpenHelper.COL_TITLE, station.getTitle());
                newRowValues.put(DatabaseOpenHelper.COL_LONGITUDE, String.valueOf(station.getLongitude()));
                newRowValues.put(DatabaseOpenHelper.COL_LATITUDE, String.valueOf(station.getLatitude()));
                newRowValues.put(DatabaseOpenHelper.COL_PHONE, station.getPhone());

                long id = db.insert(DatabaseOpenHelper.TABLE, null, newRowValues);
                station.setId(id);
                Toast.makeText(SearchActivity.this,"Saved to favorites", Toast.LENGTH_LONG).show();

            }
        });
        builder.setNeutralButton("View Map", new DialogInterface.OnClickListener() {
                @Override
                 public void onClick(DialogInterface dialog, int which) {
                    Uri mapIntentUri = Uri.parse("geo:0,0?q=" + station.getLatitude() + "," + station.getLongitude() + "(" + station.getTitle() + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if(mapIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(mapIntent);
                    }
                }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog
            }
        });
        builder.create().show();
    }

    /**
     * This internal class contains all the logic behind searching for the charging stations.
     */
    public class SearchQuery extends AsyncTask <String, Integer, String> {

        String title;
        String phone;
        double resultLong;
        double resultLat;

        /**
         * This method does all the search heavy lifting.
         * It takes the input longitude and latitude and concatenates it into a URL which returns a JSON page with the results.
         * The result is an array of charging station objects. The array is iterated through, looking for the needed attributes.
         * @See ChargingStation.class
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args){
            String ret = null;
            String queryURL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=CA&latitude=" + latitude + "&longitude=" + longitude + "&maxresults=20";
            Log.e("LOOK HERE", queryURL);

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
                        if (!operatorObject.isNull("Title")) {
                            title = titleTemp;
                        } else {
                            title = "No name";
                        }
                        String phoneTemp = operatorObject.getString("PhonePrimaryContact");
                        if(!operatorObject.isNull("PhonePrimaryContact")){
                            phone = phoneTemp;
                        }else {
                            phone = "None";
                        }

                    }else {
                        title = "No name";
                        phone = "None";
                    }
                    if(!result.isNull("AddressInfo")){
                        JSONObject addressObject = result.getJSONObject("AddressInfo");
                        resultLong = addressObject.getDouble("Longitude");
                        resultLat = addressObject.getDouble("Latitude");
                        phone = addressObject.getString("ContactTelephone1");
                    }else {
                        resultLong = 0;
                        resultLat = 0;
                        phone = "None";
                    }

                    stations.add(new ChargingStation(title, resultLong, resultLat, phone));
                    publishProgress(i*100/resultArray.length());
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

        /**
         * This method is called during the search, while doInBackground is searching.
         * It makes the progress bar visible and updates it as the search is being conducted.
         * @param value
         */
        public void onProgressUpdate(Integer... value){
            super.onProgressUpdate();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        /**
         * After the search is successful, the progress bar is hidden again, and the listview of results is updated.
         * @param value
         */
        public void onPostExecute(String value){
            super.onPostExecute(value);
            progressBar.setVisibility(View.INVISIBLE);
            myAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This private class handles everything related to the listview.
     */
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
