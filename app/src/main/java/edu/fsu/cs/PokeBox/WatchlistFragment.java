package edu.fsu.cs.PokeBox;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.*;

public class WatchlistFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView watchRV;
    private WatchListAdapter watchListAdapter;

    private final List<PokeCard> watchlist = new ArrayList<>();
    private final List<String> cardIds = new ArrayList<>();

    private static final String BASE_URL = "https://api.pokemontcg.io/v2/cards/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_watchlist, container, false);

        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);
        watchRV = view.findViewById(R.id.watchlist_rv);

        watchListAdapter = new WatchListAdapter(getContext(), watchlist);

        // set up watch list recycler view
        watchRV.setLayoutManager(new LinearLayoutManager(getContext()));
        watchRV.setAdapter(watchListAdapter);

        // set up item touch helper
        ItemTouchHelper.SimpleCallback touchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.custom_red));
            private final Drawable deleteIcon = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_baseline_delete_24);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getAdapterPosition();

                    // remove from user's watchlist
                    String id = watchlist.get(pos).getId();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER).child(id);
                    reference.removeValue();

                    watchlist.remove(pos);
                    updateRecyclerView();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }


                background.draw(c);

                // Calculate position of Trash Icon
                assert deleteIcon != null;
                int intrinsicHeight = deleteIcon.getIntrinsicHeight();
                int intrinsicWidth = deleteIcon.getIntrinsicWidth();

                int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth + 55;
                int deleteIconRight = itemView.getRight() - deleteIconMargin + 55;
                int deleteIconBottom = deleteIconTop + intrinsicHeight;

                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(watchRV);

        // set up listeners
        swipeRefreshLayout.setOnRefreshListener(this::refreshPrices);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadDataFromDatabase();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadDataFromDatabase();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadDataFromDatabase();
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

        return view;
    }

    public void refreshPrices() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        for (PokeCard card : watchlist) {
            String id = card.getId();
            // Send JSON request
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            String url = BASE_URL + id;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    // Retrieve data from response
                    JSONObject data = response.getJSONObject("data");

                    JSONObject tcgPlayer;
                    JSONObject pricesJSON;
                    Map<String, Object> prices = new HashMap<>();
                    if (data.has("tcgplayer")) {
                        tcgPlayer = data.getJSONObject("tcgplayer");
                        pricesJSON = tcgPlayer.getJSONObject("prices");
                        if (pricesJSON.has("normal")) {
                            try {
                                JSONObject normalPrices = pricesJSON.getJSONObject("normal");
                                double marketPrice = normalPrices.getDouble("market");
                                prices.put("normal", marketPrice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (pricesJSON.has("holofoil")) {
                            try {
                                JSONObject holoPrices = pricesJSON.getJSONObject("holofoil");
                                double marketPrice = holoPrices.getDouble("market");
                                prices.put("holofoil", marketPrice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (pricesJSON.has("reverseHolofoil")) {
                            try {
                                JSONObject holoPrices = pricesJSON.getJSONObject("reverseHolofoil");
                                double marketPrice = holoPrices.getDouble("market");
                                prices.put("reverseHolofoil", marketPrice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (pricesJSON.has("1stEditionHolofoil")) {
                            try {
                                JSONObject holoPrices = pricesJSON.getJSONObject("1stEditionHolofoil");
                                double marketPrice = holoPrices.getDouble("market");
                                prices.put("1stEditionHolofoil", marketPrice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (prices.isEmpty()) {
                        prices.put("none", 0);
                    }

                    // Update watchlist list
                    card.setPrices(prices);

                    // Update data in database
                    DatabaseReference reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER)
                            .child(card.getId()).child("prices");
                    reference.updateChildren(prices);

                    reference = database.getReference(MainActivity.CURRENT_USER)
                            .child(card.getId()).child("prices");
                    reference.updateChildren(prices);

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.e("REQUEST ERROR: ", error.toString()));
            queue.add(request);
        }
        swipeRefreshLayout.setRefreshing(false);
        updateRecyclerView();
    }

    public void updateRecyclerView() {
        watchListAdapter = new WatchListAdapter(getContext(), watchlist);
        watchRV.setAdapter(watchListAdapter);
    }

    public void loadDataFromDatabase() {
        cardIds.clear();
        watchlist.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("watchlists").child(MainActivity.CURRENT_USER);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot child : snapshot.getChildren()) {
                    PokeCard card = child.getValue(PokeCard.class);
                    if (card != null) {
                        String id = card.getId();
                        if (!cardIds.contains(id)) {
                            watchlist.add(card);
                            cardIds.add(id);
                        }
                    }
                }
                updateRecyclerView();
            } else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        });
    }
}
