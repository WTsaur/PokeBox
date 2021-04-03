package edu.fsu.cs.PokeBox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class CardViewerFragment extends Fragment implements CardsAdapter.OnCardClickListener {

    private EditText searchText;
    private TextView loadingText;
    private ImageButton searchBtn;
    private RecyclerView rv;

    private List<PokeCard> collection = new ArrayList<>();
    private List<Integer> collectionIdxQueue = new ArrayList<>();
    private Set<String> cardIds = new HashSet<>();

 /*   private List<Object> attacks;
    private List<Object> types;
    private List<Object> subtypes;
    private List<Object> weaknesses;
    private List<Object> resistances;
*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_viewer, container, false);

        // instantiate page elements
        searchText = view.findViewById(R.id.searchEditText);
        searchBtn = view.findViewById(R.id.searchBtn);
        ImageButton filterBtn = view.findViewById(R.id.filterBtn);
        loadingText = view.findViewById(R.id.loading_text);
        rv = view.findViewById(R.id.card_viewer_recycler);

        // set up listeners
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(MainActivity.CURRENT_USER);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("onDataChange", "data change detected!");
                loadDataFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ValueEventListener", "loadPost:onCancelled", error.toException());
            }
        };
        reference.addValueEventListener(valueEventListener);

        searchText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchBtn.callOnClick();
                    return true;
                }
            }
            return false;
        });

        searchBtn.setOnClickListener(v -> {
            hideKeyboard(v);
            Toast.makeText(getContext(), searchText.getText().toString(), Toast.LENGTH_LONG).show();
        });

        filterBtn.setOnClickListener(v -> Toast.makeText(getContext(), "Filter button clicked!", Toast.LENGTH_LONG).show());

        return view;
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void loadDataFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(MainActivity.CURRENT_USER);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot child : snapshot.getChildren()) {
                    PokeCard card = child.getValue(PokeCard.class);
                    if (card != null) {
                        String id = card.getId();
                        if (!cardIds.contains(id)) {
                            collection.add(card);
                            cardIds.add(id);
                        }
                    }
                }
                try {
                    if (collection.size() > 0) {
                        setCollectionBitMaps();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        });
    }

    public void setCollectionBitMaps() throws FileNotFoundException {
        loadingText.setVisibility(View.VISIBLE);
        collectionIdxQueue.clear();
        int i = 0;
        String txt = "Loading... 0/" + collection.size();
        for (PokeCard card : collection) {
            loadingText.setText(txt);
            // Download Bitmap
            collectionIdxQueue.add(i++);
            new DownloadTask().execute(stringToURL(card.getImageUrl()));
        }
    }

    public void updateRecyclerView() {
        CardsAdapter cardsAdapter = new CardsAdapter(collection, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(cardsAdapter);
    }

    @Override
    public void OnCardClick(int position) {
        //load the cardview activity
        Intent intent = new Intent(getActivity(), CardView.class);
        Bundle b = new Bundle();
        b.putString("name", collection.get(position).getName());
        b.putString("imageurl", collection.get(position).getImageUrl());
        b.putString("hp", collection.get(position).getHp());
        b.putString("rarity", collection.get(position).getRarity());
        b.putString("number", collection.get(position).getNumber());

        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        //get card type strings
        List<Object> types = collection.get(position).getTypes();
        for (int i=0; i<types.size(); i++){
            sb.append((String) types.get(i));

        }
        b.putString("types", sb.toString());

        sb.setLength(0);
        //get card stage strings
        List<Object> stages = collection.get(position).getSubtypes();
        for (int i=0; i<stages.size(); i++){
            sb.append((String) stages.get(i));

        }
        b.putString("stages", sb.toString());
        sb.setLength(0);

        //get card attack strings
        List<Object> attacks = collection.get(position).getAttacks();
        for (int i=0; i<attacks.size(); i++){
            sb.append((String) attacks.get(i));
            sb.append("\n");
        }
        b.putString("attacks", sb.toString());
        sb.setLength(0);

        //get card weakness
        List<Object> weakness = collection.get(position).getWeaknesses();
        for (int i=0; i<weakness.size(); i++){
            sb.append((String) weakness.get(i));
        }
        b.putString("weakness", sb.toString());
        sb.setLength(0);

        //get card resistance
        List<Object> resistance = collection.get(position).getResistances();
        for (int i=0; i<resistance.size(); i++){
            sb.append((String) resistance.get(i));
        }
        b.putString("resistance", sb.toString());
        sb.setLength(0);

        //get card price
        Map<String, Object> prices = collection.get(position).getPrices();
        if(!prices.containsKey("none")) {
            if (prices.containsKey("normal")) {
                String nprice = prices.get("normal").toString();
                b.putString("nprice", nprice);
            }
            else b.putString("nprice", null);

            if(prices.containsKey("holofoil")){
                String hprice = prices.get("holofoil").toString();
                b.putString("hprice",hprice);
            }
            else b.putString("hprice", null);

            if(prices.containsKey("reverseHolofoil")){
                String rhprice = prices.get("reverseHolofoil").toString();
                b.putString("rhprice", rhprice);
            }
            else b.putString("rhprice", null);

            if(prices.containsKey("1stEditionHolofoil")){
                String fedhprice = prices.get("1stEditionHolofoil").toString();
                b.putString("fedhprice", fedhprice);
            }
            else b.putString("fedhprice", null);

        }


        intent.putExtras(b);
        startActivity(intent);

    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {

        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection;
            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        // When all async task done
        protected void onPostExecute(Bitmap result){
            if(result!=null){
                int idx = collectionIdxQueue.get(0);
                collectionIdxQueue.remove(0);
                String txt = "Loading... " + (collection.size() - collectionIdxQueue.size()) + "/" + collection.size();
                loadingText.setText(txt);
                collection.get(idx).setImageBitmap(result);
                if (collectionIdxQueue.size() == 0) {
                    loadingText.setVisibility(View.INVISIBLE);
                    updateRecyclerView();
                }
            } else {
                // Notify user that an error occurred while downloading image
                Toast.makeText(getContext(), "Error Downloading Image", Toast.LENGTH_SHORT).show();
            }
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
