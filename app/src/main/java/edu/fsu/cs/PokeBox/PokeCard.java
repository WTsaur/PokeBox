package edu.fsu.cs.PokeBox;

import android.graphics.Bitmap;
import org.json.JSONObject;

import java.util.List;

public class PokeCard {
    private final String id;
    private final String name;
    private final List<String> types;
    private final List<String> evolvesTo;
    private Bitmap image;
    private JSONObject prices;

    public PokeCard(String id, String name, List<String> types, List<String> evolvesTo, Bitmap image, JSONObject prices) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.evolvesTo = evolvesTo;
        this.image = image;
        this.prices = prices;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTypes() {
        return types;
    }

    public List<String> getEvolvesTo() {
        return evolvesTo;
    }

    public void setImage(Bitmap img) {
        this.image = img;
    }

    public Bitmap getImage() {
        return image;
    }

    public JSONObject getPrices() { return prices; }

}
