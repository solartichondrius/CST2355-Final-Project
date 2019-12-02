package com.example.newsheadlines;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Toolbar tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

        Button carButton = findViewById(R.id.carButton);
        if(carButton != null)
            carButton.setOnClickListener( v -> {
                Intent goToCarActivity = new Intent(MainActivity.this, com.example.chargingstation.ChargingActivity.class);
                startActivity(goToCarActivity);
            });

        Button newsButton = findViewById(R.id.newsButton);
        if(newsButton != null)
            newsButton.setOnClickListener( v -> {
                Intent goToNewsActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(goToNewsActivity);
            });

    }
    /**
     *  Creates the toolbar menu
     * @param menu the menu to create
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    /**
     * Holds the conditional statement that handles which icon the user clicks on.
     * Each item goes to a different activity
     * @param item The item clicked
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.chargingItem:
                Intent goToChargingStation = new Intent(MainActivity.this, com.example.chargingstation.ChargingActivity.class);
                startActivity(goToChargingStation);
                break;

            case R.id.newsItem:
                Intent goToNewsActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(goToNewsActivity);
                break;
        }

        return true;
    }

}
