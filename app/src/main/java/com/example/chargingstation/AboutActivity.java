package com.example.chargingstation;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalproject.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This class is the about activity. It contains information about the app and how it is used
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * Creates the view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar tbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(tbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, new AboutFragment());
        ft.commit();
    }

    /**
     * Creates the toolbar menu
     * @param menu The menu to create
     * @return true;
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_charging, menu);
        return true;
    }

    /**
     * Holds the conditional statement that handles when an icon is chosen.
     * Each icon goes to a different activity
     * @param item The item chosen
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.aboutItem:
                Intent goToAbout = new Intent(AboutActivity.this, AboutActivity.class);
                startActivity(goToAbout);
                break;
            case R.id.favoriteItem:
                Intent goToFavorite = new Intent(AboutActivity.this, FavoriteActivity.class);
                startActivity(goToFavorite);
                break;
            case R.id.searchItem:
                Intent goToSearch = new Intent(AboutActivity.this, SearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.homeItem:
                Intent goHome = new Intent(AboutActivity.this, ChargingActivity.class);
                startActivity(goHome);
                break;
        }
        return true;
    }
}
