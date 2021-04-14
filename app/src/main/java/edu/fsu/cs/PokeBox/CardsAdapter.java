package edu.fsu.cs.PokeBox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private final List<PokeCard> cards;
    private final OnCardClickListener onCardClickListener;

    public CardsAdapter(List<PokeCard> cards, OnCardClickListener onCardClickListener) {
        this.cards = cards;
        this.onCardClickListener = onCardClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cardView = inflater.inflate(R.layout.item_card, parent, false);

        return new ViewHolder(cardView, onCardClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokeCard card = cards.get(position);

        ImageView imgView = holder.imageView;
        imgView.setImageBitmap(card.getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        OnCardClickListener onCardClickListener;

        public ViewHolder(View itemView, OnCardClickListener onCardClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardImageView);
            this.onCardClickListener = onCardClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardClickListener.OnCardClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnCardClickListener {
        void OnCardClick(int position);
    }
}
