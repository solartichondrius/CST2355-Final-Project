package com.example.recipes;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RecipeDatabase extends SQLiteOpenHelper {
    public static final int VERSION_NUMBER = 7;
    public static final String DATABASE_NAME = "RecipesDB";
    public static final String TABLE_NAME = "RecipesTable";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_URL = "URL";
    public static final String COL_IMAGE = "IMAGE_URL";

    public RecipeDatabase(Activity ctx){super(ctx, DATABASE_NAME, null, VERSION_NUMBER);}

    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE " + TABLE_NAME +
                "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " + COL_URL + " TEXT, " +
                COL_IMAGE + " TEXT " + ");";
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertData(String title, String url, String image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_URL, url);
        values.put(COL_IMAGE, image);
        long results = db.insert(TABLE_NAME, null, values);
        return results;
    }

    public boolean dataDuplicate(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cs = db.query(false, RecipeDatabase.TABLE_NAME, new String[]{RecipeDatabase.COL_ID, RecipeDatabase.COL_TITLE, RecipeDatabase.COL_URL, RecipeDatabase.COL_IMAGE}, null, null, null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        if(cs.moveToFirst()) {
            do {
                arrayList.add(cs.getString(cs.getColumnIndex(COL_TITLE)));
            } while (cs.moveToNext());
        }
        for(int i = 0; i < arrayList.size(); i++) {
            if (title.equals(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void deleteMessage(long id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL_ID + " = " + id + ";";
        db.execSQL(query);
    }

}
