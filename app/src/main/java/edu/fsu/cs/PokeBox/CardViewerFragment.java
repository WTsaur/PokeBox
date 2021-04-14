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
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

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
    private TableLayout filterTable;

    private Set<String> activeFilters;

    private ImageButton bugBtn;
    private ImageButton darkBtn;
    private ImageButton dragonBtn;
    private ImageButton electricBtn;
    private ImageButton fairyBtn;
    private ImageButton fightBtn;
    private ImageButton fireBtn;
    private ImageButton flyingBtn;
    private ImageButton ghostBtn;
    private ImageButton grassBtn;
    private ImageButton groundBtn;
    private ImageButton iceBtn;
    private ImageButton normalBtn;
    private ImageButton poisonBtn;
    private ImageButton psychicBtn;
    private ImageButton rockBtn;
    private ImageButton steelBtn;
    private ImageButton waterBtn;
    private ImageButton colorlessBtn;

    private final List<PokeCard> collection = new ArrayList<>();
    private final List<PokeCard> filteredCards = new ArrayList<>();
    private final List<Integer> collectionIdxQueue = new ArrayList<>();
    private final Set<String> cardIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_viewer, container, false);

        // instantiate page elements
        searchText = view.findViewById(R.id.searchEditText);
        searchBtn = view.findViewById(R.id.searchBtn);
        filterTable = view.findViewById(R.id.filter_table);
        loadingText = view.findViewById(R.id.loading_text);
        rv = view.findViewById(R.id.card_viewer_recycler);
        activeFilters = new HashSet<>();

        loadingText.setTextColor(getResources().getColor(R.color.white));

        // Set up filter buttons
        ImageButton filterBtn = view.findViewById(R.id.filterBtn);
        //filter image buttons
        Button submitFilters = view.findViewById(R.id.submitFilter);
        Button clearFilters = view.findViewById(R.id.clearFilters);
        bugBtn = view.findViewById(R.id.tag_bug);
        darkBtn = view.findViewById(R.id.tag_dark);
        dragonBtn = view.findViewById(R.id.tag_dragon);
        electricBtn = view.findViewById(R.id.tag_electric);
        fairyBtn = view.findViewById(R.id.tag_fairy);
        fightBtn = view.findViewById(R.id.tag_fight);
        fireBtn = view.findViewById(R.id.tag_fire);
        flyingBtn = view.findViewById(R.id.tag_flying);
        ghostBtn = view.findViewById(R.id.tag_ghost);
        grassBtn = view.findViewById(R.id.tag_grass);
        groundBtn = view.findViewById(R.id.tag_ground);
        iceBtn = view.findViewById(R.id.tag_ice);
        normalBtn = view.findViewById(R.id.tag_normal);
        poisonBtn = view.findViewById(R.id.tag_poison);
        psychicBtn = view.findViewById(R.id.tag_psychic);
        rockBtn = view.findViewById(R.id.tag_rock);
        steelBtn = view.findViewById(R.id.tag_steel);
        waterBtn = view.findViewById(R.id.tag_water);
        colorlessBtn = view.findViewById(R.id.tag_colorless);

        // set up listeners
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(MainActivity.CURRENT_USER);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadDataFromDatabase();
                updateRecyclerView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadDataFromDatabase();
                updateRecyclerView();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadDataFromDatabase();
                updateRecyclerView();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("ChildEventListener", "onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ChildEventListener", "onCancelled", error.toException());
            }
        });

        searchText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchBtn.callOnClick();
                    return true;
                }
            }
            return false;
        });

        //search by name, searching an empty string returns all cards
        searchBtn.setOnClickListener(v -> {
            hideKeyboard(v);
            String search = searchText.getText().toString().toLowerCase(Locale.ROOT);
            if(!search.isEmpty()) {

                // filterCatcher "catches" wanted cards
                Set<PokeCard> filterCatcher = new HashSet<>();
                String name;

                for (PokeCard card : collection) {
                    name = card.getName().toLowerCase(Locale.ROOT);
                    if (!name.contains(search)) {
                        filterCatcher.add(card);
                    }
                }
                collection.removeAll(filterCatcher);

                for (PokeCard card : filteredCards) {
                    name = card.getName().toLowerCase(Locale.ROOT);
                    if (!name.contains(search)) {
                        filterCatcher.add(card);
                    }
                }
                filteredCards.removeAll(filterCatcher);

                collection.addAll(filteredCards);
                filteredCards.clear();
                filteredCards.addAll(filterCatcher);

            } else {
                submitFilters.callOnClick();
            }

            if (collection.size() == 0) {
                Toast.makeText(getContext(), "No cards found!", Toast.LENGTH_SHORT).show();
            }

            updateRecyclerView();
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterTable.getVisibility() == View.VISIBLE) {
                    filterTable.setVisibility(View.INVISIBLE);
                } else {
                    filterTable.setVisibility(View.VISIBLE);
                }
            }
        });

        submitFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterTable.setVisibility(View.INVISIBLE);

                if(!activeFilters.isEmpty()) {
                    // filterCatcher "catches" wanted cards
                    Set<PokeCard> filterCatcher = new HashSet<>();
                    List<Object> types;
                    String type;
                    boolean hasType;

                    for (PokeCard card : collection) {
                        types = card.getTypes();
                        hasType = false;
                        for (Object typeObj : types) {
                            type = typeObj.toString();
                            if (activeFilters.contains(type)) {
                                hasType = true;
                                break;
                            }
                        }
                        if (!hasType) {
                            filterCatcher.add(card);
                        }
                    }

                    collection.removeAll(filterCatcher);

                    for (PokeCard card : filteredCards) {
                        types = card.getTypes();
                        hasType = false;
                        for (Object typeObj : types) {
                            type = typeObj.toString();
                            if (activeFilters.contains(type)) {
                                hasType = true;
                                break;
                            }
                        }
                        if (!hasType) {
                            filterCatcher.add(card);
                        }
                    }

                    filteredCards.removeAll(filterCatcher);
                    collection.addAll(filteredCards);
                    filteredCards.clear();
                    filteredCards.addAll(filterCatcher);

                } else{
                    collection.addAll(filteredCards);
                    filteredCards.clear();
                }

                if (collection.size() == 0) {
                    Toast.makeText(getContext(), "No cards found!", Toast.LENGTH_SHORT).show();
                }

                updateRecyclerView();
            }
        });

        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bugBtn.setAlpha(1f);
                darkBtn.setAlpha(1f);
                dragonBtn.setAlpha(1f);
                electricBtn.setAlpha(1f);
                fairyBtn.setAlpha(1f);
                fightBtn.setAlpha(1f);
                fireBtn.setAlpha(1f);
                flyingBtn.setAlpha(1f);
                ghostBtn.setAlpha(1f);
                grassBtn.setAlpha(1f);
                groundBtn.setAlpha(1f);
                iceBtn.setAlpha(1f);
                normalBtn.setAlpha(1f);
                poisonBtn.setAlpha(1f);
                psychicBtn.setAlpha(1f);
                rockBtn.setAlpha(1f);
                steelBtn.setAlpha(1f);
                waterBtn.setAlpha(1f);
                colorlessBtn.setAlpha(1f);
                activeFilters.clear();
            }
        });

        colorlessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Colorless");
                } else {
                    activeFilters.remove("Colorless");
                }
            }
        });

        bugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Bug");
                } else {
                    activeFilters.remove("Bug");
                }
            }
        });

        darkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Dark");
                } else {
                    activeFilters.remove("Dark");
                }
            }
        });

        dragonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Dragon");
                } else {
                    activeFilters.remove("Dragon");
                }
            }
        });

        electricBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Electric");
                } else {
                    activeFilters.remove("Electric");
                }
            }
        });

        fairyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Fairy");
                } else {
                    activeFilters.remove("Fairy");
                }
            }
        });

        fightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Fighting");
                } else {
                    activeFilters.remove("Fighting");
                }
            }
        });

        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Fire");
                } else {
                    activeFilters.remove("Fire");
                }
            }
        });

        flyingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Flying");
                } else {
                    activeFilters.remove("Flying");
                }
            }
        });

        ghostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Ghost");
                } else {
                    activeFilters.remove("Ghost");
                }
            }
        });

        grassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Grass");
                } else {
                    activeFilters.remove("Grass");
                }
            }
        });

        groundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Ground");
                } else {
                    activeFilters.remove("Ground");
                }
            }
        });

        iceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Ice");
                } else {
                    activeFilters.remove("Ice");
                }
            }
        });

        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Normal");
                } else {
                    activeFilters.remove("Normal");
                }
            }
        });

        poisonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Poison");
                } else {
                    activeFilters.remove("Poison");
                }
            }
        });

        psychicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Psychic");
                } else {
                    activeFilters.remove("Psychic");
                }
            }
        });

        rockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Rock");
                } else {
                    activeFilters.remove("Rock");
                }
            }
        });

        steelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Steel");
                } else {
                    activeFilters.remove("Steel");
                }
            }
        });

        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click(v)) {
                    activeFilters.add("Water");
                } else {
                    activeFilters.remove("Water");
                }
            }
        });

        return view;
    }

    public boolean click(View v) {
        float n = 0.5f;
        if (v.getAlpha() == n) {
            v.setAlpha(1f);
            return false;
        } else {
            v.setAlpha(n);
            return true;
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void loadDataFromDatabase() {
        cardIds.clear();
        collectionIdxQueue.clear();
        collection.clear();
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
        b.putString("id", collection.get(position).getId());
        b.putString("name", collection.get(position).getName());
        b.putString("imageurl", collection.get(position).getImageUrl());
        b.putString("hp", collection.get(position).getHp());
        b.putString("rarity", collection.get(position).getRarity());
        b.putString("number", collection.get(position).getNumber());
        b.putString("id", collection.get(position).getId());

        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        //get card type strings
        List<Object> types = collection.get(position).getTypes();
        for (Object type : types) {
            sb.append((String) type);

        }
        b.putString("types", sb.toString());

        sb.setLength(0);
        //get card stage strings
        List<Object> stages = collection.get(position).getSubtypes();
        for (Object stage : stages) {
            sb.append((String) stage);

        }
        b.putString("stages", sb.toString());
        sb.setLength(0);

        //get card attack strings
        List<Object> attacks = collection.get(position).getAttacks();
        int count = 0;
        for (Object attack : attacks) {
            sb.append((String) attack);
            if (count++ != attacks.size() - 1) {
                sb.append("\n");
            }
        }
        b.putString("attacks", sb.toString());
        sb.setLength(0);

        //get card weakness
        List<Object> weakness = collection.get(position).getWeaknesses();
        for (Object o : weakness) {
            sb.append((String) o);
        }
        b.putString("weakness", sb.toString());
        sb.setLength(0);

        //get card resistance
        List<Object> resistance = collection.get(position).getResistances();
        for (Object o : resistance) {
            sb.append((String) o);
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
            if(result != null) {
                if (!collectionIdxQueue.isEmpty()) {
                    int idx = collectionIdxQueue.get(0);
                    collectionIdxQueue.remove(0);
                    String txt = "Loading... " + (collection.size() - collectionIdxQueue.size()) + "/" + collection.size();
                    loadingText.setText(txt);
                    collection.get(idx).setImageBitmap(result);
                    if (collectionIdxQueue.size() == 0) {
                        loadingText.setVisibility(View.INVISIBLE);
                        updateRecyclerView();
                    }
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
