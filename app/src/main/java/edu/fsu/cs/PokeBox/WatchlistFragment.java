package edu.fsu.cs.PokeBox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatchlistFragment extends Fragment {
    private EditText searchText;
    private TextView loadingText;
    private ImageButton searchBtn;
    private RecyclerView rv;

    private List<PokeCard> collection = new ArrayList<>();
    private List<Integer> collectionIdxQueue = new ArrayList<>();
    private Set<String> cardIds = new HashSet<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        return view;
    }
}
