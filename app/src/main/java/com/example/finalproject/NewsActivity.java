package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * News Activity application for the CST2335 Final Group Project (Fall 2019)
 * @author Thomas Curtis
 */
public class NewsActivity extends AppCompatActivity {

    BaseAdapter myAdapter; //Adapter for the list of News Headlines
    ArrayList<NewsHeadline> newsHeadlines = new ArrayList<>(); //array list for storing all of the news headlines

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_headlines);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //the following is just placeholder code to show that the listview works
        for(int i=0; i<100; i++) newsHeadlines.add(new NewsHeadline("News Headline " + i));
        //the above code will be replaced after I implement the News API

        ListView theList = findViewById(R.id.theList);
        theList.setAdapter( myAdapter = new MyListAdapter() );
        theList.setOnItemClickListener( ( parent,  view,  position,  id) ->{
            editor.putString("title", newsHeadlines.get(position).getTitle());
            editor.commit();
            Intent goToDetailedActivity = new Intent(getApplicationContext(), DetailedActivity.class);
            startActivity(goToDetailedActivity);
        });

        EditText searchBar = findViewById(R.id.searchBar);
        Button searchButton = findViewById(R.id.searchButton);

        if(searchButton != null)
            searchButton.setOnClickListener( v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Searching for \"" + searchBar.getText() + "\"");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Toast toast = Toast.makeText(getApplicationContext(), "Searching for \"" + searchBar.getText() + "\"", Toast.LENGTH_LONG);
                toast.show();
                Snackbar.make(searchButton,"Searching for \"" + searchBar.getText() + "\"", Snackbar.LENGTH_LONG).show();
                try {
                    progressBarTest(progressBar);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
    }

    /**
     * Adapter for the list of News Headlines
     */
    class MyListAdapter extends BaseAdapter {

        public int getCount() {  return newsHeadlines.size();  } //This function tells how many messages to show
        public NewsHeadline getItem(int position) { return newsHeadlines.get(position); }
        public long getItemId(int i) { return 0; }
        public String getTitle(int position) { return newsHeadlines.get(position).getTitle();  }

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
     * tests that the progress bar works by using the Thread.sleep() method to slow it down enough that we can actually see it loading
     * @param progressBar te progressBar to test
     */
    public void progressBarTest(ProgressBar progressBar) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        Thread.sleep(100);
        for(int i=0; i<=100; i++){
                progressBar.setProgress(i);
                Thread.sleep(10);
        }
    }
}
