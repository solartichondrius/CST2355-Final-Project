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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
public class DetailedActivity extends Fragment {

    SQLiteDatabase savedDB;
    Button saveButton = null;
    Button deleteButton = null;
    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        SavedNewsHeadlinesDBHelper dbOpener = new SavedNewsHeadlinesDBHelper(getActivity());
        savedDB = dbOpener.getWritableDatabase();

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.detailed_layout, container, false);


        TextView source = result.findViewById(R.id.source);
        TextView author = result.findViewById(R.id.author);
        TextView title = result.findViewById(R.id.title);
        TextView description = result.findViewById(R.id.description);
        TextView url = result.findViewById(R.id.url);
        ImageView image = result.findViewById(R.id.image);
        TextView publishedAt = result.findViewById(R.id.publishedAt);
        TextView content = result.findViewById(R.id.content);
        Button webButton = result.findViewById(R.id.webButton);

        source.setText(dataFromActivity.getString("source", null));
        author.setText(dataFromActivity.getString("author", null));
        title.setText(dataFromActivity.getString("title", null));
        description.setText(dataFromActivity.getString("description", null));
        url.setText(dataFromActivity.getString("url", null));
        try {
            image.setImageBitmap(image(dataFromActivity.getString("title", null), dataFromActivity.getString("urlToImage", null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishedAt.setText(dataFromActivity.getString("publishedAt", null));
        content.setText(dataFromActivity.getString("content", null));

        if(isAlreadyInDB(dataFromActivity.getString("url", null))) {
            deleteButton = result.findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
        }else{
            saveButton = result.findViewById(R.id.saveButton);
            saveButton.setVisibility(View.VISIBLE);
        }

        if(webButton != null)
            webButton.setOnClickListener( v -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(dataFromActivity.getString("url", null)));
                startActivity(i);
            });

        if(saveButton != null)
            saveButton.setOnClickListener( v -> {
                ContentValues newValues = new ContentValues();
                newValues.put(SavedNewsHeadlinesDBHelper.COL_SOURCE, dataFromActivity.getString("source", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_AUTHOR, dataFromActivity.getString("author", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_TITLE, dataFromActivity.getString("title", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_DESCRIPTION, dataFromActivity.getString("description", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_URL, dataFromActivity.getString("url", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_URLTOIMAGE, dataFromActivity.getString("urlToImage", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_PUBLISHEDAT, dataFromActivity.getString("publishedAt", null));
                newValues.put(SavedNewsHeadlinesDBHelper.COL_CONTENT, dataFromActivity.getString("content", null));
                savedDB.insert(SavedNewsHeadlinesDBHelper.TABLE_NAME, null, newValues);
                Snackbar.make(saveButton, getString(R.string.saved), Snackbar.LENGTH_LONG).show();
                deleteButton = result.findViewById(R.id.deleteButton);
                saveButton.setVisibility(View.INVISIBLE);
                saveButton = null;
                deleteButton.setVisibility(View.VISIBLE);
            });

        if(deleteButton != null)
            deleteButton.setOnClickListener( v -> {
                savedDB.delete(SavedNewsHeadlinesDBHelper.TABLE_NAME, SavedNewsHeadlinesDBHelper.COL_URL + " = ?", new String[]{dataFromActivity.getString("url", null)});
                Snackbar.make(deleteButton, getString(R.string.deleted), Snackbar.LENGTH_LONG).show();
                saveButton = result.findViewById(R.id.saveButton);
                deleteButton.setVisibility(View.INVISIBLE);
                deleteButton = null;
                saveButton.setVisibility(View.VISIBLE);
            });

        return result;
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
     * @param title
     * @param urlToImage
     * @return the image
     * @throws IOException
     */
    public Bitmap image(String title, String urlToImage) throws IOException {
        Bitmap image;
        if (fileExistence(title + ".png")) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(getActivity().getFileStreamPath(title + ".png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            image = BitmapFactory.decodeStream(inputStream);
        } else {
            image = getBitmapFromURL(urlToImage);
            FileOutputStream outputStream = getActivity().openFileOutput( title + ".png", Context.MODE_PRIVATE);
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
        File file = getActivity().getFileStreamPath(fileName);
        return file.exists();
    }

    /**
     * Check whether or not a news article is stored in the local database.
     * Used url as the unique identifier for the news article since all url's must be unique.
     * @param url
     * @return True if the url is in the database, False if the url is NOT in the database
     */
    public boolean isAlreadyInDB(String url){

        String selection = SavedNewsHeadlinesDBHelper.COL_URL + " LIKE ? ";
        String[] selectionArgs = {url};

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
    public void onStop() {
        super.onStop();
        //View result =  inflater.inflate(R.layout.detailed_layout, container, false);

        //ImageView image = result.findViewById(R.id.image);
        //image.setImageResource(0);
    }
}
