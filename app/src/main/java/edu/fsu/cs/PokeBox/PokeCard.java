package edu.fsu.cs.PokeBox;

import android.graphics.Bitmap;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class PokeCard {
    private final String id;
    private final String name;
    private final List<Object> types;
    private final List<Object> evolvesTo;
    private String imageUrl;
    private Map<String, Object> prices;
    @Exclude
    private Bitmap imageBitmap;

    public PokeCard() {
        this.id = "";
        this.name = "";
        this.types = new ArrayList<>();
        this.evolvesTo = new ArrayList<>();
        this.imageUrl = "";
        this.prices = new HashMap<>();
        this.imageBitmap = null;
    }

    public PokeCard(String id, String name, List<Object> types, List<Object> evolvesTo, Map<String, Object> prices) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.evolvesTo = evolvesTo;
        this.imageUrl = "";
        this.imageBitmap = null;
        this.prices = prices;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Object> getTypes() {
        return types;
    }

    public List<Object> getEvolvesTo() {
        return evolvesTo;
    }

    @Exclude
    public Bitmap getImageBitmap() { return imageBitmap; }

    @Exclude
    public void setImageBitmap(Bitmap bitmap) { this.imageBitmap = bitmap; }

    @Exclude
    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Map<String, Object> getPrices() { return prices; }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("types", types);
        result.put("evolvesTo", evolvesTo);
        result.put("imageUrl", imageUrl);
        result.put("prices", prices);
        return result;
    }

}
