package com.example.chargingstation;

import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.*;

import android.os.AsyncTask;
import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public class SearchQuery extends AsyncTask <String, Integer, String> {

        @Override
        protected String doInBackground(String... args){

            return null;
        }

        public void onProgressUpdate(Integer... value){

        }

        public void onPostExecute(String value){

        }
    }
}
