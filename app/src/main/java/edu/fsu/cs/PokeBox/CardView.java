package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;

public class CardView extends MainActivity{
    private ImageView pokecardimage, pokecardimageclick;
    private TextView Tname, Tnumberandrarity, Ttypehpstage, Tattacks, Tweaknessandresistance, Tprice, text;
    private String url, name, hp, number, rarity, type, stage, attacks, weakness, resistance, normalprice, holoprice, reverseholoprice, firsteditionholoprice;
    private String result;

    //TODO: Add to watchlist menu, delete option in the menu...?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);

        //setting the back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
