package com.example.chargingstation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finalproject.*;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class holds and displays all charging stations added to the user's favorites database
 */
public class FavoriteActivity extends AppCompatActivity {

    private ArrayList<ChargingStation> stations = new ArrayList<>();
    private BaseAdapter myAdapter;
    View thisRow;
    SQLiteDatabase db;
    ListView theList;

    /**
     * This method is called when the activity is created.
     * It creates the toolbar to be used later.
     * It creates a list adapter for the list view.
     * It adds all items in the database to an array list.
     * Creates a listener for when an item is clicked in the list view.
     * The listening lambda calls on another method.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar tbar = findViewById(R.id.favoriteToolbar);
        setSupportActionBar(tbar);

        theList = findViewById(R.id.favoriteList);
        myAdapter = new MyListAdapter();
        theList.setAdapter(myAdapter);

        DatabaseOpenHelper dbOpener = new DatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {DatabaseOpenHelper.COL_ID, DatabaseOpenHelper.COL_TITLE, DatabaseOpenHelper.COL_LONGITUDE, DatabaseOpenHelper.COL_LATITUDE, DatabaseOpenHelper.COL_PHONE};
        Cursor results = db.query(false, DatabaseOpenHelper.TABLE, columns, null, null, null, null, null, null);
        int idColIndex = results.getColumnIndex(DatabaseOpenHelper.COL_ID);
        int titleColIndex = results.getColumnIndex(DatabaseOpenHelper.COL_TITLE);
        int longColIndex = results.getColumnIndex(DatabaseOpenHelper.COL_LONGITUDE);
        int latColIndex = results.getColumnIndex(DatabaseOpenHelper.COL_LATITUDE);
        int phoneColIndex = results.getColumnIndex(DatabaseOpenHelper.COL_PHONE);

        while(results.moveToNext()){
            long id = results.getLong(idColIndex);
            String title = results.getString(titleColIndex);
            double longitude = Double.valueOf(results.getString(longColIndex));
            double latitude = Double.valueOf(results.getString(latColIndex));
            String phone = results.getString(phoneColIndex);

            ChargingStation station = new ChargingStation(title, longitude, latitude, phone);
            station.setId(id);
            stations.add(station);
        }

        theList.setOnItemClickListener((lv, vw, pos, id) -> {
            ChargingStation station = stations.get(pos);
            resultClicked(station, pos);
        });
    }

    /**
     * This method handles when an item in the listview is clicked.
     * An alert dialog appears displaying all the charging station's attributes.
     * @See ChargingStation.class
     * The positive button deletes the item from the listview and the database.
     * The neutral button opens the google maps app and places a pin a the location of the selected charging station.
     * The negative button closes the dialog.
     *
     * @param station The station selected
     * @param position The position of the selected station in the list.
     */
    public void resultClicked(ChargingStation station, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Name: " + station.getTitle() + "\n" + "Latitude: " + station.getLatitude() + "\n" + "Longitude: " + station.getLongitude() + "\n" + "Phone: " + station.getPhone());
        builder.setPositiveButton("Delete from Favorites", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete
                db.execSQL("DELETE FROM " + DatabaseOpenHelper.TABLE + " WHERE " + DatabaseOpenHelper.COL_ID + " = " + station.getId());
                stations.remove(stations.get(position));
                myAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog
            }
        });
        builder.setNeutralButton("View Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri mapIntentUri = Uri.parse("geo:" + station.getLatitude() + "," + station.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if(mapIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(mapIntent);
                }
            }
        });
        builder.create().show();
    }

    /**
     * Creates the toolbar menu
     * @param menu The menu to be created
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_charging, menu);
        return true;
    }

    /**
     * This method handles when an item in the menu is clicked.
     * Each icon redirects to a different activity.
     * @param item The item selected
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.aboutItem:
                Intent goToAbout = new Intent(FavoriteActivity.this, AboutActivity.class);
                startActivity(goToAbout);
                break;
            case R.id.favoriteItem:
                Intent goToFavorite = new Intent(FavoriteActivity.this, FavoriteActivity.class);
                startActivity(goToFavorite);
                break;
            case R.id.searchItem:
                Intent goToSearch = new Intent(FavoriteActivity.this, SearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.homeItem:
                Intent goHome = new Intent(FavoriteActivity.this, ChargingActivity.class);
                startActivity(goHome);
                break;
        }

        return true;
    }

    /**
     * This internal class creates and maintains the listview.
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * Gets the number of station objects in the listview
         * @return the number of station objects
         */
        @Override
        public int getCount() {
            return stations.size();
        }

        /**
         * Gets the station object at a specified position in the list
         * @param position The position of the object in the list
         * @return the specified object
         */
        public ChargingStation getItem(int position){
            return stations.get(position);
        }

        /**
         * Gets the ID of the station item at a certain position
         * @param position The position of the station object in the listview
         * @return The ID of the station object at the specified position
         */
        public long getItemId(int position){
            return position;
        }

        /**
         * Sets the content of an individual view in the listview
         * @param position The posiiton the item is being inserted
         * @param recycled The old view
         * @param parent The parent view
         * @return The newly updated row
         */
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
