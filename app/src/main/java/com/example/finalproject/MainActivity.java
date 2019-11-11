package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity  extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Button newsButton = findViewById(R.id.newsButton);
        if(newsButton != null)
            newsButton.setOnClickListener( v -> {
                Intent goToNewsActivity = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(goToNewsActivity);
            });

    }
}
