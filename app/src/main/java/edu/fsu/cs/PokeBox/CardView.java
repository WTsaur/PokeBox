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
    private ImageView pokecardimage, pokecardimageclick;
    private TextView Tname, Tnumberandrarity, Ttypehpstage, Tattacks, Tweaknessandresistance, Tprice, text;
    private String url, name, hp, number, rarity, type, stage, attacks, weakness, resistance, normalprice, holoprice, reverseholoprice, firsteditionholoprice;
    private String result;

    //TODO: Add to watchlist menu, delete option in the menu...?
    //click on image expands the image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        Intent intent = getIntent();
        //setting the loading animation for card image
        pokecardimage = (ImageView) findViewById(R.id.pokeimage);
        Glide.with(this).load(R.drawable.loading).into(pokecardimage);


        pokecardimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), ViewImage.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                i.putExtras(bundle);
                startActivity(i);
            }});

        //showing the card image and pokemon name
        url = intent.getStringExtra("imageurl");
        name = intent.getStringExtra("name");
        Tname = (TextView) findViewById(R.id.displayname);
        Tname.setText(name);
        new DownloadImageTask((ImageView) findViewById(R.id.pokeimage)).execute(url);

        //setting Card Number and Rarity
        Tnumberandrarity = (TextView) findViewById(R.id.numberandrarity);
        number = intent.getStringExtra("number");
        rarity = intent.getStringExtra("rarity");
        result = number + " / " + rarity;
        Tnumberandrarity.setText(result);

        //setting Card type, HP, and stage
        Ttypehpstage = (TextView) findViewById(R.id.typehpstage);
        type = intent.getStringExtra("types");
        hp = intent.getStringExtra("hp");
        stage = intent.getStringExtra("stages");
        result = type + " / " + hp + " / " + stage;
        Ttypehpstage.setText(result);

        //setting attacks
        Tattacks = (TextView) findViewById(R.id.attacks);
        attacks = intent.getStringExtra("attacks");
        Tattacks.setText(attacks);

        //setting weakness and resistance
        Tweaknessandresistance = (TextView) findViewById(R.id.weaknessandresistance);
        weakness = intent.getStringExtra("weakness");
        resistance = intent.getStringExtra("resistance");
        result = weakness + " / " + resistance;
        Tweaknessandresistance.setText(result);

        //setting prices
        normalprice = intent.getStringExtra("nprice");
        holoprice = intent.getStringExtra("hprice");
        reverseholoprice = intent.getStringExtra("rhprice");
        firsteditionholoprice = intent.getStringExtra("fedhprice");

        if(normalprice != null){
            Tprice = (TextView) findViewById(R.id.nprice);
            Tprice.setText(normalprice);
        }

        if(holoprice != null){
            Tprice = (TextView) findViewById(R.id.hprice);
            Tprice.setText(holoprice);
        }

        if(reverseholoprice != null){
            Tprice = (TextView) findViewById(R.id.rhprice);
            Tprice.setText(reverseholoprice);
        }

        if(firsteditionholoprice != null){
            Tprice = (TextView) findViewById(R.id.fedhprice);
            Tprice.setText(firsteditionholoprice);
        }



    }

    //getting the image from url
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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


}
