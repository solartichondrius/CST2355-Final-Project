package com.example.newsheadlines;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Copypasta from a previous lab, used to manage a database to store all of the saved News Headlines in
 */
public class SavedNewsHeadlinesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SavedDB";
    public static final int VERSION_NUM = 3;
    public static final String TABLE_NAME = "SavedDB";
    public static final String COL_ID = "_id";
    public static final String COL_SOURCE = "source";
    public static final String COL_AUTHOR = "author";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_URL = "url";
    public static final String COL_URLTOIMAGE = "urlToImage";
    public static final String COL_PUBLISHEDAT = "publishedAt";
    public static final String COL_CONTENT = "content";

    public SavedNewsHeadlinesDBHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    public void onCreate(SQLiteDatabase db)
    {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_SOURCE + " TEXT, "
                + COL_AUTHOR + " TEXT, "
                + COL_TITLE + " TEXT, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_URL + " TEXT, "
                + COL_URLTOIMAGE + " TEXT, "
                + COL_PUBLISHEDAT + " TEXT, "
                + COL_CONTENT + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}