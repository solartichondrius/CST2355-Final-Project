package com.example.recipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsheadlines.R;

public class EmptyRecipe extends AppCompatActivity {
    Bundle dataToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_recipe);
        dataToPass = getIntent().getExtras();

        Bundle dataToPass = getIntent().getExtras();
        RecipeFragment dFragment = new RecipeFragment();
        dFragment.setArguments( dataToPass );
        dFragment.setTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, dFragment)
                .commit();
    }

}
