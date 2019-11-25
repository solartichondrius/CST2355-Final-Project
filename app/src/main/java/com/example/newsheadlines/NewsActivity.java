package com.example.newsheadlines;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.StrictMode;

import static com.example.newsheadlines.JsonReader.readJsonFromUrl;

/**
 * News Activity application for the CST2335 Final Group Project (Fall 2019)
 * @author Thomas Curtis
 */
public class NewsActivity extends AppCompatActivity {

    ProgressBar progressBar; //progress bar to show the progress of the search for news headlines
    ListView theList; //list view to display the list of news headlines
    String VERSION_NUMBER = "2019-11-25"; //when this program was last updated
    String ACTIVITY_NAME = "News Headline"; //name of this activity
    String AUTHOR = "Thomas Curtis"; //the person who wrote this program
    BaseAdapter myAdapter; //Adapter for the list of News Headlines
    ArrayList<NewsHeadline> newsHeadlines = new ArrayList<>(); //array list for storing all of the news headlines that the user searched for
    ArrayList<NewsHeadline> savedArticles = new ArrayList<>(); //array list for storing all of the news headlines that the user saved in the database for offline viewing
    SharedPreferences pref; //used to save values to use in other parts of the program
    SharedPreferences.Editor editor; //used to edit the values to use in other parts of the program
    SQLiteDatabase searchedDB; //the database to store all the news articles that the user searched for
    SQLiteDatabase savedDB; //the database to store all the news articles that the user chooses to save
    public static final int EMPTY_ACTIVITY = 345;

    @Override
    protected void onStop(){ //when the program is stopped
        super.onStop(); //call the super method
        searchedDB.execSQL("DELETE FROM " + SearchedNewsHeadlinesDBHelper.TABLE_NAME); //delete the previously stored news headlines first
        for(int i = 0; i < newsHeadlines.size(); i++) { //for every news headline stored in the array
            ContentValues newValues = new ContentValues();
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_SOURCE, newsHeadlines.get(i).getSource());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_AUTHOR, newsHeadlines.get(i).getAuthor());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_TITLE, newsHeadlines.get(i).getTitle());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_DESCRIPTION, newsHeadlines.get(i).getDescription());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_URL, newsHeadlines.get(i).getUrl());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_URLTOIMAGE, newsHeadlines.get(i).getUrlToImage());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_PUBLISHEDAT, newsHeadlines.get(i).getPublishedAt());
            newValues.put(SearchedNewsHeadlinesDBHelper.COL_CONTENT, newsHeadlines.get(i).getContent());
            searchedDB.insert(SearchedNewsHeadlinesDBHelper.TABLE_NAME, null, newValues); //store every value of the News Headline in the array
        }
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_headlines);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setText(pref.getString("searchBar", ""));
        Button searchButton = findViewById(R.id.searchButton);
        Button helpButton = findViewById(R.id.helpButton);
        Button savedButton = findViewById(R.id.savedButton);
        SavedNewsHeadlinesDBHelper savedDBOpener = new SavedNewsHeadlinesDBHelper(this);
        savedDB = savedDBOpener.getWritableDatabase();
        SearchedNewsHeadlinesDBHelper searchedDBOpener = new SearchedNewsHeadlinesDBHelper(this);
        searchedDB = searchedDBOpener.getWritableDatabase();
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        boolean empty = true;
        Cursor cur = searchedDB.rawQuery("SELECT COUNT(*) FROM " + SearchedNewsHeadlinesDBHelper.TABLE_NAME, null);
        if (cur != null && cur.moveToFirst()) empty = (cur.getInt (0) == 0); //check if the database is empty
        cur.close();
        if(!empty){ //if the database is not empty, then load all of the news headlines in the database into the list view
            theList = findViewById(R.id.theList);
            theList.setAdapter( myAdapter = new newsHeadlinesAdapter() );
            myAdapter.notifyDataSetChanged();
            String [] columns = {
                    SearchedNewsHeadlinesDBHelper.COL_ID,
                    SearchedNewsHeadlinesDBHelper.COL_SOURCE,
                    SearchedNewsHeadlinesDBHelper.COL_AUTHOR,
                    SearchedNewsHeadlinesDBHelper.COL_TITLE,
                    SearchedNewsHeadlinesDBHelper.COL_DESCRIPTION,
                    SearchedNewsHeadlinesDBHelper.COL_URL,
                    SearchedNewsHeadlinesDBHelper.COL_URLTOIMAGE,
                    SearchedNewsHeadlinesDBHelper.COL_PUBLISHEDAT,
                    SearchedNewsHeadlinesDBHelper.COL_CONTENT
            };
            Cursor results = searchedDB.query(false, SearchedNewsHeadlinesDBHelper.TABLE_NAME, columns, null, null, null, null, null, null);
            printCursor(results, newsHeadlines);
            theList.setOnItemClickListener( ( parent,  view,  position,  id) ->{
                Bundle dataToPass = new Bundle();
                dataToPass.putString("source", newsHeadlines.get(position).getSource());
                dataToPass.putString("author", newsHeadlines.get(position).getAuthor());
                dataToPass.putString("title", newsHeadlines.get(position).getTitle());
                dataToPass.putString("description", newsHeadlines.get(position).getDescription());
                dataToPass.putString("url", newsHeadlines.get(position).getUrl());
                dataToPass.putString("urlToImage", newsHeadlines.get(position).getUrlToImage());
                dataToPass.putString("publishedAt", newsHeadlines.get(position).getPublishedAt());
                dataToPass.putString("content", newsHeadlines.get(position).getContent());

                if(isTablet)
                {
                    DetailedActivity dFragment = new DetailedActivity(); //add a DetailFragment
                    dFragment.setArguments( dataToPass ); //pass it a bundle for information
                    dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .addToBackStack("AnyName") //make the back button undo the transaction
                            .commit(); //actually load the fragment.
                }
                else //isPhone
                {
                    Intent nextActivity = new Intent(NewsActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
                }
            });
        }

        if(helpButton != null) //when this button is pressed, show a custom dialog box displaying information about this activity
            helpButton.setOnClickListener( v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(ACTIVITY_NAME + ", " + getString(R.string.versionNumber) + ": " + VERSION_NUMBER + "\n\n" + getString(R.string.authorText) + " " + AUTHOR + "\n\n" + getString(R.string.newsExplanation))
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Doesn't do anything special, just clicking on it closes the dialog box
                            }
                        }).create().show();
            });

        if(searchButton != null) //when this button is pressed, search the url for news headlines matching the criteria entered by the user in the search bar
            searchButton.setOnClickListener( v -> {
                editor.putString("searchBar", searchBar.getText().toString());
                newsHeadlines.clear();
                NewsQuery news = new NewsQuery();
                news.execute("https://newsapi.org/v2/everything?apiKey=1c016c8c50464e50b58ad14667f05ea1&q=" + searchBar.getText());
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.searchCompleteDisplaying) + " " + pref.getString("arraySize", null) + " " + getString(R.string.outOf) + " " + pref.getString("totalResults", null) + " " + getString(R.string.totalResultsFound), Toast.LENGTH_LONG);
                toast.show();
            });

        if(savedButton != null) // when the this button is pressed, load all of the saved news headlines from the database into the listview
            savedButton.setOnClickListener( v -> {
                theList = findViewById(R.id.theList);
                theList.setAdapter( myAdapter = new savedArticlesAdapter() );
                myAdapter.notifyDataSetChanged();
                String [] columns = {
                        SavedNewsHeadlinesDBHelper.COL_ID,
                        SavedNewsHeadlinesDBHelper.COL_SOURCE,
                        SavedNewsHeadlinesDBHelper.COL_AUTHOR,
                        SavedNewsHeadlinesDBHelper.COL_TITLE,
                        SavedNewsHeadlinesDBHelper.COL_DESCRIPTION,
                        SavedNewsHeadlinesDBHelper.COL_URL,
                        SavedNewsHeadlinesDBHelper.COL_URLTOIMAGE,
                        SavedNewsHeadlinesDBHelper.COL_PUBLISHEDAT,
                        SavedNewsHeadlinesDBHelper.COL_CONTENT
                };
                Cursor results = savedDB.query(false, SavedNewsHeadlinesDBHelper.TABLE_NAME, columns, null, null, null, null, null, null);
                printCursor(results, savedArticles);
                theList.setOnItemClickListener( ( parent,  view,  position,  id) ->{
                    Bundle dataToPass = new Bundle();
                    dataToPass.putString("source", savedArticles.get(position).getSource());
                    dataToPass.putString("author", savedArticles.get(position).getAuthor());
                    dataToPass.putString("title", savedArticles.get(position).getTitle());
                    dataToPass.putString("description", savedArticles.get(position).getDescription());
                    dataToPass.putString("url", savedArticles.get(position).getUrl());
                    dataToPass.putString("urlToImage", savedArticles.get(position).getUrlToImage());
                    dataToPass.putString("publishedAt", savedArticles.get(position).getPublishedAt());
                    dataToPass.putString("content", savedArticles.get(position).getContent());

                    if(isTablet)
                    {
                        DetailedActivity dFragment = new DetailedActivity(); //add a DetailFragment
                        dFragment.setArguments( dataToPass ); //pass it a bundle for information
                        dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                                .addToBackStack("AnyName") //make the back button undo the transaction
                                .commit(); //actually load the fragment.
                    }
                    else //isPhone
                    {
                        Intent nextActivity = new Intent(NewsActivity.this, EmptyActivity.class);
                        nextActivity.putExtras(dataToPass); //send data to next activity
                        startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
                    }
                });
            });
    }

    /**
     * gets all of the values for every column in the database the creates a new NewsHeadline with those values and adds it to the savedArticles array, repeat for every row in the database
     * @param c cursor, which is a database query
     */
    protected void printCursor(Cursor c, ArrayList aList){

        aList.clear(); //clear anything already in the savedArticles array

        while(c.moveToNext()) { //while there's data in the row, get the values from every column and create a new NewsHeadline Object to add to the savedArticles array
            String source = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_SOURCE));
            String author = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_AUTHOR));
            String title = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_TITLE));
            String description = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_URL));
            String urlToImage = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_URLTOIMAGE));
            String publishedAt = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_PUBLISHEDAT));
            String content = c.getString(c.getColumnIndex(SavedNewsHeadlinesDBHelper.COL_CONTENT));
            aList.add(new NewsHeadline(source, author, title, description, url, urlToImage, publishedAt, content));
        }
    }

    /**
     * Adapter for the list of News Headlines that were a result of the search by the user
     */
    class newsHeadlinesAdapter extends BaseAdapter {
        /**
         * get the size of the newsHeadlines array
         * @return the size of the newsHeadlines array
         */
        public int getCount() {  return newsHeadlines.size();  }
        /**
         * get a NewsHeadline object from the newsHeadlines array at the specified position
         * @param position the position of the NewsHeadline object in the newsHeadline array
         * @return the NewsHeadline object at the specified position in the newsHeadlines array
         */
        public NewsHeadline getItem(int position) { return newsHeadlines.get(position); }
        /**
         * didn't use this method but had to make it anyways because it is in the abstract parent class
         * @param i
         * @return
         */
        public long getItemId(int i) { return 0; }
        /**
         * Creates a row for each NewsHeadline object in the newsHeadlines array
         * @param p position of the NewsHeadline object in the newsHeadlines array
         * @param recycled don't know what this is for and don't think it's even needed because it doesn't look like it's even used, but I dare not remove it for fear of breaking my program
         * @param parent the parent view (the view that this view is inside of)
         * @return the newly created view
         */
        public View getView(int p, View recycled, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.table_row_layout, parent, false);
            NewsHeadline thisRow = getItem(p);
            TextView title = newView.findViewById(R.id.title);
            title.setText(thisRow.getTitle());
            return newView;
        }
    }
    /**
     * Adapter for the list of News Headlines that were saved by the user
     */
    class savedArticlesAdapter extends BaseAdapter {
        /**
         * get the size of the savedHeadlines array
         * @return the size of the savedHeadlines array
         */
        public int getCount() {  return savedArticles.size();  }
        /**
         * get a NewsHeadline object from the savedHeadlines array at the specified position
         * @param position the position of the NewsHeadline object in the savedHeadline array
         * @return the NewsHeadline object at the specified position in the savedHeadlines array
         */
        public NewsHeadline getItem(int position) { return savedArticles.get(position); }
        /**
         * didn't use this method but had to make it anyways because it is in the abstract parent class
         * @param i
         * @return
         */
        public long getItemId(int i) { return 0; }
        /**
         * Creates a row for each NewsHeadline object in the savedHeadlines array
         * @param p position of the NewsHeadline object in the savedHeadlines array
         * @param recycled don't know what this is for and don't think it's even needed because it doesn't look like it's even used, but I dare not remove it for fear of breaking my program
         * @param parent the parent view (the view that this view is inside of)
         * @return the newly created view
         */
        public View getView(int p, View recycled, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.table_row_layout, parent, false);
            NewsHeadline thisRow = getItem(p);
            TextView title = newView.findViewById(R.id.title);
            title.setText(thisRow.getTitle());
            return newView;
        }
    }
    /**
     * Query that uses AsyncTask to get news headlines from a url
     */
    public class NewsQuery extends AsyncTask<String, Integer, String> {

        JSONObject jObject;

        @Override
        protected String doInBackground(String... params) {

            try{
                URL URL = new URL(params[0]); //use the provided url
                HttpURLConnection conn = (HttpURLConnection) URL.openConnection(); //to establish a connection
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect(); //connect to the url

                jObject = readJsonFromUrl(params[0]); //read the JSON data from the url
                editor.putString("totalResults", jObject.getString("totalResults")); //store the total number of results from the search
                JSONArray articles = jObject.getJSONArray("articles"); //get the articles from the url and put them into a JSON array
                editor.putString("arraySize", Integer.toString(articles.length())); //store the size of the array

                for(int i = 0; i < articles.length(); i++) { //loop through every object in the array
                    JSONObject j = articles.getJSONObject(i); //get the JSON object from the array at the iterator's position
                    String source = j.getString("source"); //get the "source" string from the JSONObject
                    for(int k=0; k<2; k++) source = source.substring(source.indexOf("\"")+1); //remove the leading characters before the 2 quote to get to the id of the source
                    if(!source.substring(1, 5).equals("null")) for(int k=0; k<2; k++) source = source.substring(source.indexOf("\"")+1); //if the id isn't null then remove the characters before the next 2 quotes
                    for(int k=0; k<3; k++) source = source.substring(source.indexOf("\"")+1); //loop through the rest of "source" string, cutting off all the information we don't need (before the quotes)
                    source.trim(); //trim off the spaces left behind after removing the other characters from the string
                    source = source.split("\"")[0]; //remove the characters that we don't need (after the final quote)
                    String author = j.getString("author"); //get the "author" string from the JSONObject
                    String title = j.getString("title"); //get the "title" string from the JSONObject
                    String description = j.getString("description"); //get the "description" string from the JSONObject
                    String url = j.getString("url"); //get the "url" string from the JSONObject
                    String urlToImage = j.getString("urlToImage"); //get the "urlToImage" string from the JSONObject
                    String publishedAt = j.getString("publishedAt"); //get the "publishedAt" string from the JSONObject
                    String content = j.getString("content"); //get the "content" string from the JSONObject
                    newsHeadlines.add(new NewsHeadline(source, author, title, description, url, urlToImage, publishedAt, content)); //create a new NewsHeadline object in the newsHeadline array with all of the values we just set
                    publishProgress(100/articles.length()*(i+1)); //publish the progress to the loading bar. Full would be 100, so divide that by the length of the array, then multiply it by the iterator plus 1
                } //(since it starts at 0 and ends at 1 less than the actual length of the array)
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value){
            progressBar.setVisibility(View.VISIBLE); //make the progress bar visible
            progressBar.setProgress(value[0]); //the update it with the current progress
        }

        @Override
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.INVISIBLE); //hide the progress bar since we're done loading now
            theList = findViewById(R.id.theList); //get the list view
            theList.setAdapter( myAdapter = new newsHeadlinesAdapter() ); //set the adapter which is responsible for updating the list view
            myAdapter.notifyDataSetChanged();
            theList.setOnItemClickListener( ( parent,  view,  position,  id) ->{ //when any object in the list view is clicked run the following code
                Bundle dataToPass = new Bundle();
                dataToPass.putString("source", newsHeadlines.get(position).getSource());
                dataToPass.putString("author", newsHeadlines.get(position).getAuthor());
                dataToPass.putString("title", newsHeadlines.get(position).getTitle());
                dataToPass.putString("description", newsHeadlines.get(position).getDescription());
                dataToPass.putString("url", newsHeadlines.get(position).getUrl());
                dataToPass.putString("urlToImage", newsHeadlines.get(position).getUrlToImage());
                dataToPass.putString("publishedAt", newsHeadlines.get(position).getPublishedAt());
                dataToPass.putString("content", newsHeadlines.get(position).getContent());
                boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded
                if(isTablet)
                {
                    DetailedActivity dFragment = new DetailedActivity(); //add a DetailFragment
                    dFragment.setArguments( dataToPass ); //pass it a bundle for information
                    dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .addToBackStack("AnyName") //make the back button undo the transaction
                            .commit(); //actually load the fragment.
                }
                else //isPhone
                {
                    Intent nextActivity = new Intent(NewsActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
                }
            });
        }
    }
}
