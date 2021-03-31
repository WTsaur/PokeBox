package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CardView extends MainActivity{
    private ImageView pokecardimage;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        Intent intent = getIntent();
        pokecardimage = (ImageView) findViewById(R.id.pokeimage);
        Glide.with(this).load(R.drawable.loading).into(pokecardimage);

        url = intent.getStringExtra("imageurl");

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

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
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
