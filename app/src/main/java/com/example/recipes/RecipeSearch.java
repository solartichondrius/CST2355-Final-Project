package com.example.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.newsheadlines.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecipeSearch extends AppCompatActivity {
    ListView listView;
    Button searchBtn;
    EditText searchBox;
    ProgressBar progressBar;
    ArrayList<Recipe> arrayList;
    ArrayList<String> chickenTitle;
    ArrayList<String> chickenUrl;
    ArrayList<String> chickenImage;
    ArrayList<String> lasagnaTitle;
    ArrayList<String> lasagnaUrl;
    ArrayList<String> lasagnaImage;
    BaseAdapter listAdapter;
    RecipeDatabase dbOpener;
    SQLiteDatabase db;
    Cursor cs;
    Toolbar toolbar;
    Bundle dataToPass;
    boolean isTablet;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);
        listView = findViewById(R.id.listViewRecipe);
        searchBtn = findViewById(R.id.buttonSearch);
        searchBox = findViewById(R.id.editSearch);
        progressBar = findViewById(R.id.progressbarRecipe);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        arrayList = new ArrayList<>();
        dbOpener = new RecipeDatabase(this);
        db = dbOpener.getWritableDatabase();
        cs = db.query(false, RecipeDatabase.TABLE_NAME, new String[]{RecipeDatabase.COL_ID, RecipeDatabase.COL_TITLE, RecipeDatabase.COL_URL, RecipeDatabase.COL_IMAGE},null, null, null, null, null, null);
        isTablet = findViewById(R.id.fragmentLocation) != null;
        this.pref = getApplicationContext().getSharedPreferences("recipe_data", MODE_PRIVATE);
        this.searchBox.setText(pref.getString("keyword",""));

        RecipeSearch.ChickenQuery chickenQuery = new RecipeSearch.ChickenQuery();
        chickenQuery.execute();

        RecipeSearch.LasagnaQuery lasagnaQuery = new RecipeSearch.LasagnaQuery();
        lasagnaQuery.execute();

        listAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return arrayList.size();
            }

            @Override
            public Recipe getItem(int position) {
                return arrayList.get(position);
            }

            @Override
            public long getItemId(int position) { return position; }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Recipe rcp = getItem(position);
                    convertView = getLayoutInflater().inflate(R.layout.activity_recipe, parent, false);
                    TextView title = convertView.findViewById(R.id.textTitle);
                    title.setText(rcp.getTitle());
                return convertView;
            }
        };
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataToPass = new Bundle();
                dataToPass.putInt("position", position);
                dataToPass.putString("title", arrayList.get(position).getTitle());
                dataToPass.putString("url", arrayList.get(position).getUrl());
                dataToPass.putString("image", arrayList.get(position).getImage());
                if(isTablet)
                { RecipeFragment rFragment = new RecipeFragment();;
                    rFragment.setArguments( dataToPass ); //pass it a bundle for information
                    rFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, rFragment).addToBackStack(null)
                            .commit(); //actually load the fragment.
                } else {
                    Intent intent = new Intent(RecipeSearch.this, EmptyRecipe.class);
                    intent.putExtras(dataToPass);
                    startActivityForResult(intent, 610);
                }
            }
        });

        searchBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.recipe_dialog,null);
                builder.setView(v)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                return true;
            case R.id.action2:
                if(isTablet)
                {   FavouriteRecipe rFragment = new FavouriteRecipe();//pass it a bundle for information
                    rFragment.setArguments(dataToPass);
                    rFragment.setTablet(true);//tell the fragment if it's running on a tablet or not
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, rFragment).commit(); //actually load the fragment.
                } else {
                    Intent intentF = new Intent(RecipeSearch.this, EmptyFavorite.class);
                    startActivityForResult(intentF, 2912);
                }
                return true;
                default:
        }
        return super.onOptionsItemSelected(item);
    }

    class ChickenQuery extends AsyncTask<String, Integer, String>{
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            String chickenRecipe = "http://torunski.ca/FinalProjectChickenBreast.json";
            try{
                HttpURLConnection chickenConnection = (HttpURLConnection) new URL(chickenRecipe).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(chickenConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; stringBuilder.append(line));
                String result = stringBuilder.toString();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    titles.add(object.getString("title"));
                    publishProgress(15);
                    url.add(object.getString("source_url"));
                    publishProgress(30);
                    image.add(object.getString("image_url"));
                    publishProgress(45);

                }

            } catch(MalformedURLException mfe){
                mfe.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            } catch (JSONException je){
                je.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sentFromDoInBackground){
            super.onPostExecute(sentFromDoInBackground);
            chickenTitle = titles;
            chickenUrl = url;
            chickenImage = image;
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Integer...values){
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);


        }
    }

    class LasagnaQuery extends AsyncTask<String,Integer,String>{
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            String lasagnaRecipe = "http://torunski.ca/FinalProjectLasagna.json";
            try{
                HttpURLConnection lasagnaConnection = (HttpURLConnection) new URL(lasagnaRecipe).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(lasagnaConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; stringBuilder.append(line));
                String result = stringBuilder.toString();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    titles.add(object.getString("title"));
                    publishProgress(60);
                    url.add(object.getString("source_url"));
                    publishProgress(75);
                    image.add(object.getString("image_url"));
                    publishProgress(100);
                }

            } catch(MalformedURLException mfe){
                mfe.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            } catch (JSONException je){
                je.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sentFromDoInBackground){
            super.onPostExecute(sentFromDoInBackground);
            lasagnaTitle = titles;
            lasagnaUrl = url;
            lasagnaImage = image;
            progressBar.setVisibility(View.INVISIBLE);

            if (!RecipeSearch.this.searchBox.getText().toString().isEmpty()) {
                search();
            }
        }

        @Override
        protected void onProgressUpdate(Integer...values){
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }
    }

    private void search() {
        arrayList.clear();
        for (int i = 0; i < chickenTitle.size(); i++) {
            String title = chickenTitle.get(i);
            String url = chickenUrl.get(i);
            String image = chickenImage.get(i);
            if(title.toLowerCase().contains(searchBox.getText().toString())) {
                Recipe rcp = new Recipe(title, url, image);
                arrayList.add(rcp);
            }
        }
        for (int i = 0; i < lasagnaTitle.size(); i++) {
            String title = lasagnaTitle.get(i);
            String url = lasagnaUrl.get(i);
            String image = lasagnaImage.get(i);
            if(title.toLowerCase().contains(searchBox.getText().toString())) {
                Recipe rcp = new Recipe(title, url, image);
                arrayList.add(rcp);
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("keyword", searchBox.getText().toString());
        editor.commit();
    }
}


