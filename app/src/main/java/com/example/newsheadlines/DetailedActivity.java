package com.example.newsheadlines;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Activity to display a detailed view of a NewsHeadline
 */
public class DetailedActivity extends AppCompatActivity {

    SQLiteDatabase savedDB;
    Button saveButton = null;
    Button deleteButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_layout);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        SavedNewsHeadlinesDBHelper dbOpener = new SavedNewsHeadlinesDBHelper(this);
        savedDB = dbOpener.getWritableDatabase();

        TextView source = findViewById(R.id.source);
        TextView author = findViewById(R.id.author);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView url = findViewById(R.id.url);
        ImageView image = findViewById(R.id.image);
        TextView publishedAt = findViewById(R.id.publishedAt);
        TextView content = findViewById(R.id.content);
        Button webButton = findViewById(R.id.webButton);

        source.setText(pref.getString("source", null));
        author.setText(pref.getString("author", null));
        title.setText(pref.getString("title", null));
        description.setText(pref.getString("description", null));
        url.setText(pref.getString("url", null));
        try {
            image.setImageBitmap(image(pref));
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishedAt.setText(pref.getString("publishedAt", null));
        content.setText(pref.getString("content", null));

        if(isAlreadyInDB(pref)) {
            deleteButton = findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
        }else{
            saveButton = findViewById(R.id.saveButton);
            saveButton.setVisibility(View.VISIBLE);
        }

        if(webButton != null)
            webButton.setOnClickListener( v -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(pref.getString("url", null)));
                startActivity(i);
            });

        if(saveButton != null)
            saveButton.setOnClickListener( v -> {
                ContentValues newValues = new ContentValues();
                newValues.put(SavedNewsHeadlinesDBHelper.COL_SOURCE, pref.getString("source", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_AUTHOR, pref.getString("author", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_TITLE, pref.getString("title", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_DESCRIPTION, pref.getString("description", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_URL, pref.getString("url", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_URLTOIMAGE, pref.getString("urlToImage", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_PUBLISHEDAT, pref.getString("publishedAt", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_CONTENT, pref.getString("content", null));
                savedDB.insert(SavedNewsHeadlinesDBHelper.TABLE_NAME, null, newValues);
                Snackbar.make(saveButton, getString(R.string.saved), Snackbar.LENGTH_LONG).show();
                deleteButton = findViewById(R.id.deleteButton);
                saveButton.setVisibility(View.INVISIBLE);
                saveButton = null;
                deleteButton.setVisibility(View.VISIBLE);
            });

        if(deleteButton != null)
            deleteButton.setOnClickListener( v -> {
                savedDB.delete(SavedNewsHeadlinesDBHelper.TABLE_NAME, SavedNewsHeadlinesDBHelper.COL_URL + " = ?", new String[]{pref.getString("url", null)});
                Snackbar.make(deleteButton, getString(R.string.deleted), Snackbar.LENGTH_LONG).show();
                saveButton = findViewById(R.id.saveButton);
                deleteButton.setVisibility(View.INVISIBLE);
                deleteButton = null;
                saveButton.setVisibility(View.VISIBLE);
            });


    }
    /**
     * gets an image from a url
     * @param src the url of the image
     * @return the image
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
    /**
     * Load an image either from url or file depending on whether it is already saved as a file or not.
     * @param pref SharedPreferences where thr image's url is stored
     * @return the image
     * @throws IOException
     */
    public Bitmap image(SharedPreferences pref) throws IOException {
        String title = pref.getString("title", null);
        Bitmap image;
        if (fileExistence(title + ".png")) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(getBaseContext().getFileStreamPath(title + ".png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            image = BitmapFactory.decodeStream(inputStream);
        } else {
            String urlToImage = pref.getString("urlToImage", null);
            image = getBitmapFromURL(urlToImage);
            FileOutputStream outputStream = openFileOutput( title + ".png", Context.MODE_PRIVATE);
            if(image!=null) image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        }
        return image;
    }

    /**
     * Check whether or not a file exists
     * @param fileName file to check for existence
     * @return True if the file exists, false if it does not exist
     */
    public boolean fileExistence(String fileName) {
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    /**
     * Check whether or not a news article is stored in the local database.
     * Used url as the unique identifier for the news article since all url's must be unique.
     * @param pref SharedPreferences to get the stored url from
     * @return True if the url is in the database, False if the url is NOT in the database
     */
    public boolean isAlreadyInDB(SharedPreferences pref){

        String selection = SavedNewsHeadlinesDBHelper.COL_URL + " LIKE ? ";
        String[] selectionArgs = {pref.getString("url", null)};

        Cursor cursor = savedDB.query(
                SavedNewsHeadlinesDBHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.getCount() > 0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageView image = findViewById(R.id.image);
        image.setImageResource(0);
    }
}
