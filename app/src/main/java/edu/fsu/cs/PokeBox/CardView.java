package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardView extends MainActivity{
    private ImageView pokecardimage;
    private TextView Tname;
    private String url;
    private String name;
    private static final String API_KEY = "b87cf1ba-0a63-4073-9135-7573becb8002";
    private static final String BASE_URL = "https://api.pokemontcg.io/v2/cards?X-Api-Key:" + API_KEY + "&q=name:\"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        Intent intent = getIntent();
        //setting the loading animation for card image
        pokecardimage = (ImageView) findViewById(R.id.pokeimage);
        Glide.with(this).load(R.drawable.loading).into(pokecardimage);

        //showing the card image and pokemon name
        url = intent.getStringExtra("imageurl");
        name = intent.getStringExtra("name");
        Tname = (TextView) findViewById(R.id.displayname);
        Tname.setText(name);
        new DownloadImageTask((ImageView) findViewById(R.id.pokeimage)).execute(url);



    }

    //getting the image from url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }




    protected URL stringToURL(String src) {
        try {
            return new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}