package com.example.chargingstation;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class holds a database which contains a table of all charging stations favorited
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "ChargingStationDatabase";
    public static final int VERSION = 1;
    public static final String TABLE = "FavoriteStations";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "Title";
    public static final String COL_LONGITUDE = "Longitude";
    public static final String COL_LATITUDE ="Latitude";
    public static final String COL_PHONE = "Phone";

    /**
     * Initial constructor
     * @param context
     */
    public DatabaseOpenHelper(Activity context){
        super(context, DATABASE, null, VERSION);
    }

    /**
     * Creates a new database
     * @param db the database to create
     */
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TITLE + " TEXT, " + COL_LONGITUDE + " TEXT, " + COL_LATITUDE + " TEXT, " + COL_PHONE +" TEXT)");
    }

    /**
     * If version number is higher than initial version
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /**
     * If version number is lower than initial version
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

}
