package edu.fsu.cs.PokeBox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardView extends MainActivity{
    private String id;
    private String url;
    private String name;
    private String number;
    private String rarity;
    private String normalprice;
    private String holoprice;
    private String reverseholoprice;
    private String firsteditionholoprice;
    public static Bitmap image;

    private final String currency = "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            //setting the back button on action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        //setting the loading animation for card image
        ImageView pokecardimage = findViewById(R.id.pokeimage);

        pokecardimage.setOnClickListener(v -> {
            Intent i = new Intent(getApplication(), ViewImage.class);
            ViewImage.image = image;
            startActivity(i);
        });

        //showing the card image and pokemon name
        id = intent.getStringExtra("id");
        url = intent.getStringExtra("imageurl");
        name = intent.getStringExtra("name");
        TextView tname = findViewById(R.id.displayname);
        tname.setText(name);
        pokecardimage.setImageBitmap(image);

        //setting Card Number and Rarity
        TextView tnumberandrarity = findViewById(R.id.numberandrarity);
        number = intent.getStringExtra("number");
        rarity = intent.getStringExtra("rarity");
        String result = number + " / " + rarity;
        tnumberandrarity.setText(result);

        //setting Card type, HP, and stage
        TextView ttypehpstage = findViewById(R.id.typehpstage);
        String type = intent.getStringExtra("types");
        String hp = intent.getStringExtra("hp");
        String stage = intent.getStringExtra("stages");
        result = type + " / " + hp + " / " + stage;
        ttypehpstage.setText(result);

        //setting attacks
        TextView tattacks = findViewById(R.id.attacks);
        String attacks = intent.getStringExtra("attacks");
        tattacks.setText(attacks);

        //setting weakness and resistance
        TextView tweaknessandresistance = findViewById(R.id.weaknessandresistance);
        String weakness = intent.getStringExtra("weakness");
        String resistance = intent.getStringExtra("resistance");
        result = weakness + " / " + resistance;
        tweaknessandresistance.setText(result);

        //setting prices
        normalprice = intent.getStringExtra("nprice");
        holoprice = intent.getStringExtra("hprice");
        reverseholoprice = intent.getStringExtra("rhprice");
        firsteditionholoprice = intent.getStringExtra("fedhprice");

        TextView tprice;
        String price;
        if(normalprice != null){
            tprice = findViewById(R.id.nprice);
            price = currency + normalprice;
            tprice.setText(price);
        }

        if(holoprice != null){
            tprice = findViewById(R.id.hprice);
            price = currency + holoprice;
            tprice.setText(price);
        }

        if(reverseholoprice != null){
            tprice = findViewById(R.id.rhprice);
            price = currency + reverseholoprice;
            tprice.setText(price);
        }

        if(firsteditionholoprice != null){
            tprice = findViewById(R.id.fedhprice);
            price = currency + firsteditionholoprice;
            tprice.setText(price);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseDatabase database;
        DatabaseReference reference;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.addwatchlist:
                List<Object> nil = new ArrayList<>();
                Map<String, Object> prices = new HashMap<>();
                prices.put("normal", normalprice);
                prices.put("holofoil", holoprice);
                prices.put("reverseHolofoil", reverseholoprice);
                prices.put("1stEditionHolofoil", firsteditionholoprice);

                PokeCard cardToWatch = new PokeCard(id, name, "nil", rarity, number,
                        nil, nil, nil, nil, nil, nil, prices);

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER).child(id);
                reference.setValue(cardToWatch);

                reference.setValue(cardToWatch);
                Toast.makeText(this, "Card added to watchlist!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.deleteCard:
                database = FirebaseDatabase.getInstance();
                //remove from user's collection
                reference = database.getReference(MainActivity.CURRENT_USER).child(id);
                reference.removeValue();
                // remove from user's watchlist
                reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER).child(id);
                reference.removeValue();
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    //menu option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
