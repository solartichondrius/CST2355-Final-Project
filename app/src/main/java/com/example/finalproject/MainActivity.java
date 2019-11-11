package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.chargingstation.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity  extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Button carButton = findViewById(R.id.carButton);
        if(carButton != null)
            carButton.setOnClickListener( v -> {
                Intent goToCarActivity = new Intent(MainActivity.this, ChargingActivity.class);
                startActivity(goToCarActivity);
            });
        /*
        Button recipeButton = findViewById(R.id.recipeButton);
        if(recipeButton != null)
            recipeButton.setOnClickListener( v -> {
                Intent goToRecipeActivity = new Intent(getApplicationContext(), RecipeActivity.class);
                startActivity(goToRecipeActivity);
            });

        Button currencyButton = findViewById(R.id.currencyButton);
        if(currencyButton != null)
            currencyButton.setOnClickListener( v -> {
                Intent goToCurrencyActivity = new Intent(getApplicationContext(), CurrencyActivity.class);
                startActivity(goToCurrencyActivity);
            });
        */
        Button newsButton = findViewById(R.id.newsButton);
        if(newsButton != null)
            newsButton.setOnClickListener( v -> {
                Intent goToNewsActivity = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(goToNewsActivity);
            });

    }
}
