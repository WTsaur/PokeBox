package edu.fsu.cs.PokeBox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class WatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PokeCard> watchlist;
    Context context;

    public WatchListAdapter(Context context, List<PokeCard> watchlist) {
        this.watchlist = watchlist;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_watch_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PokeCard card = watchlist.get(position);
        Map<String, Object> prices = card.getPrices();
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).id.setText(card.getId());
            ((MyViewHolder) holder).name.setText(card.getName());
            String currency = "$ ";
            String price;
            if (prices.get("normal") == null) {
                ((MyViewHolder) holder).normPrice.setText("n/a");
            } else {
                price = currency + prices.get("normal").toString();
                ((MyViewHolder) holder).normPrice.setText(price);
            }

            if (prices.get("holofoil") == null) {
                ((MyViewHolder) holder).holoPrice.setText("n/a");
            } else {
                price = currency + prices.get("holofoil").toString();
                ((MyViewHolder) holder).holoPrice.setText(price);
            }

            if (prices.get("reverseHolofoil") == null) {
                ((MyViewHolder) holder).revHoloPrice.setText("n/a");
            } else {
                price = currency + prices.get("reverseHolofoil").toString();
                ((MyViewHolder) holder).revHoloPrice.setText(price);
            }

            if (prices.get("1stEditionHolofoil") == null) {
                ((MyViewHolder) holder).firstEdPrice.setText("n/a");
            } else {
                price = currency + prices.get("1stEditionHolofoil").toString();
                ((MyViewHolder) holder).firstEdPrice.setText(price);
            }
        }
    }

    @Override
    public int getItemCount() {
        return watchlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        TextView normPrice;
        TextView holoPrice;
        TextView revHoloPrice;
        TextView firstEdPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.watch_id);
            name = itemView.findViewById(R.id.watch_name);
            normPrice = itemView.findViewById(R.id.watch_normalPrice);
            holoPrice = itemView.findViewById(R.id.watch_holoPrice);
            revHoloPrice = itemView.findViewById(R.id.watch_revHoloPrice);
            firstEdPrice = itemView.findViewById(R.id.watch_1stEdRevHoloPrice);
        }
    }
}
