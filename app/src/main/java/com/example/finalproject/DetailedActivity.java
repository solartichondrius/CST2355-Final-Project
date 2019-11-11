package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_layout);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        TextView source = findViewById(R.id.source);
        TextView author = findViewById(R.id.author);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView url = findViewById(R.id.url);

        title.setText(pref.getString("title", null));
    }
}
