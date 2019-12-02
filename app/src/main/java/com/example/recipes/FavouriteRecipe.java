package com.example.recipes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newsheadlines.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FavouriteRecipe extends Fragment {
    private boolean isTablet;
    ListView listView;
    Button delete;
    BaseAdapter listAdapter;
    ArrayList<Recipe> arrayList;
    RecipeDatabase dbOpener;
    SQLiteDatabase db;
    Cursor cs;
    Bundle dataToPass;
    TextView title;
    TextView url;
    ImageView imageView;
    String imageUrl;
    String image;
    String file;


    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.activity_favourite_recipe, container, false);
        listView = result.findViewById(R.id.favoriteList);
        arrayList = new ArrayList<>();
        dbOpener = new RecipeDatabase(getActivity());
        db = dbOpener.getWritableDatabase();
        cs = db.query(false, RecipeDatabase.TABLE_NAME, new String[]{RecipeDatabase.COL_ID, RecipeDatabase.COL_TITLE, RecipeDatabase.COL_URL, RecipeDatabase.COL_IMAGE}, null, null, null, null, null, null);
        dataToPass = getArguments();

        if(cs.moveToFirst()) {
            do {
                String title = cs.getString(cs.getColumnIndex(RecipeDatabase.COL_TITLE));
                String url = cs.getString(cs.getColumnIndex(RecipeDatabase.COL_URL));
                String image = cs.getString(cs.getColumnIndex(RecipeDatabase.COL_IMAGE));
                long id = cs.getLong(cs.getColumnIndex(RecipeDatabase.COL_ID));
                arrayList.add(new Recipe(title, url, image, id));
            } while(cs.moveToNext());
        }
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
            public long getItemId(int position) {
                return getItem(position).getId();
            }

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
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.favorite_dialog,null);
                title = v.findViewById(R.id.titleFavorite);
                url = v.findViewById(R.id.urlFavorite);
                imageView = v.findViewById(R.id.imageFavorite);
                title.setText(arrayList.get(position).getTitle());
                url.setText(arrayList.get(position).getUrl());
                imageUrl = arrayList.get(position).getImage();
                image = imageUrl.substring(0, 4) + "s" + imageUrl.substring(4);
                file = imageUrl.substring(28);

                ImageUrlF imageUrlF = new ImageUrlF();
                imageUrlF.execute();

                builder.setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isTablet) {
                            RecipeSearch parent = (RecipeSearch) getActivity();
                            if(arrayList.size()>0) {
                                dbOpener.deleteMessage(id);
                                arrayList.remove(position);
                                listAdapter.notifyDataSetChanged();
                            } else{
                                Toast.makeText(getContext(),"No Recipe available",Toast.LENGTH_LONG).show();
                            }
                            parent.getSupportFragmentManager().beginTransaction().commit();
                        } else {
                            EmptyFavorite parent =(EmptyFavorite)getActivity();
                            if(arrayList.size()>0) {
                                dbOpener.deleteMessage(id);
                                arrayList.remove(position);
                                Toast.makeText(getContext(),"Deleted from favorite list", Toast.LENGTH_LONG).show();
                                listAdapter.notifyDataSetChanged();
                            } else{
                                Toast.makeText(getContext(),"No Recipe available",Toast.LENGTH_LONG).show();
                            }
                            parent.finish();
                        }
                    }
                });
                builder.create().show();


            }
        });

        listAdapter.notifyDataSetChanged();

        return result;

    }

    public class ImageUrlF extends AsyncTask<String, Integer, String> {
        Bitmap bitmap;

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpURLConnection imageConnection = (HttpURLConnection) new URL(image).openConnection();
                imageConnection.connect();
                int responseCode = imageConnection.getResponseCode();
                if (responseCode == 200) {
                    bitmap = BitmapFactory.decodeStream(imageConnection.getInputStream());
                    FileOutputStream outputStream = getActivity().openFileOutput(file, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (MalformedURLException mfe){
                mfe.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            imageView.setImageBitmap(bitmap);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


    }
}
