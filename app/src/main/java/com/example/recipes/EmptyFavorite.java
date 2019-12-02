package com.example.recipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsheadlines.R;


public class EmptyFavorite extends AppCompatActivity {
    Bundle dataToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_favorite);
        dataToPass = getIntent().getExtras();

        FavouriteRecipe fFragment = new FavouriteRecipe();
        fFragment.setArguments(dataToPass);
        fFragment.setTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, fFragment)
                .commit();

    }
}
