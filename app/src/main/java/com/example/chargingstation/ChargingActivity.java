package com.example.chargingstation;
import com.example.newsheadlines.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This class is the main activity for the car charging station portion of the app.
 */
public class ChargingActivity extends AppCompatActivity {


    /**
     * Sets the view from activity_charging
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        Toolbar tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

    }

    /**
     *  Creates the toolbar menu
     * @param menu the menu to create
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_charging, menu);
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
            case R.id.aboutItem:
               // Intent goToAbout = new Intent(ChargingActivity.this, AboutActivity.class);
                //startActivity(goToAbout);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder, new AboutFragment());
                ft.commit();

                break;
            case R.id.favoriteItem:
                Intent goToFavorite = new Intent(ChargingActivity.this, FavoriteActivity.class);
                startActivity(goToFavorite);
                break;
            case R.id.searchItem:
                Intent goToSearch = new Intent(ChargingActivity.this, SearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.homeItem:
                Intent goHome = new Intent(ChargingActivity.this, ChargingActivity.class);
                startActivity(goHome);
                break;
        }

        return true;
    }
}
