package com.example.recipes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class RecipeFragment extends Fragment {
    Button add;
    TextView title;
    TextView url;
    ImageView imageView;
    String imageUrl;
    String image;
    String file;
    RecipeDatabase dbOpener;
    SQLiteDatabase db;
    Cursor cs;
    private boolean isTablet;
    private Bundle dataFromActivity;
    ArrayList<String> arrayList;

    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataFromActivity = getArguments();
        dbOpener = new RecipeDatabase(getActivity());
        db = dbOpener.getWritableDatabase();
        cs = db.query(false, RecipeDatabase.TABLE_NAME, new String[]{RecipeDatabase.COL_ID, RecipeDatabase.COL_TITLE, RecipeDatabase.COL_URL, RecipeDatabase.COL_IMAGE}, null, null, null, null, null, null);
        arrayList = new ArrayList<>();
        View result = inflater.inflate(R.layout.activity_recipe_fragment,container,false);
        title = result.findViewById(R.id.titleView);
        url = result.findViewById(R.id.urlView);
        imageView = result.findViewById(R.id.imageRecipe);
        add = result.findViewById(R.id.favorite);
            title.setText(dataFromActivity.getString("title"));
            url.setText(dataFromActivity.getString("url"));
            imageUrl = dataFromActivity.get("image").toString();
            image = imageUrl.substring(0, 4) + "s" + imageUrl.substring(4);
            file = imageUrl.substring(28);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dbOpener.dataDuplicate(title.getText().toString())){
                        Toast.makeText(getActivity(),"Recipe already exist in favorite list",Toast.LENGTH_LONG).show();
                    }else {
                        Recipe rcp = new Recipe(title.getText().toString(), url.getText().toString()
                                , imageUrl, dbOpener.insertData(title.getText().toString(),
                                url.getText().toString(), imageUrl));
                        Toast.makeText(getActivity(),"Recipe added to favorite list", Toast.LENGTH_LONG).show();
                    }
                }
            });

        ImageUrl query = new ImageUrl();
        query.execute();

        return result;
    }

    public class ImageUrl extends AsyncTask<String, Integer, String>{
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
