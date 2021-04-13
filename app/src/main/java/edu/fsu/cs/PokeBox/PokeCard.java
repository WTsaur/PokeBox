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
    private final String hp;
    private final String rarity;
    private final String number;
    private final List<Object> attacks;
    private final List<Object> types;
    private final List<Object> evolvesTo;
    private final List<Object> subtypes;
    private final List<Object> weaknesses;
    private final List<Object> resistances;
    private String imageUrl;
    private Map<String, Object> prices;

    @Exclude
    private Bitmap imageBitmap;

    public PokeCard() {
        this.id = "";
        this.name = "";
        this.hp = "";
        this.rarity = "";
        this.number = "";
        this.attacks = new ArrayList<>();
        this.types = new ArrayList<>();
        this.evolvesTo = new ArrayList<>();
        this.subtypes = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.resistances = new ArrayList<>();
        this.imageUrl = "";
        this.prices = new HashMap<>();
        this.imageBitmap = null;
    }

    public PokeCard(String id, String name, String hp, String rarity, String number,
                    List<Object> attacks, List<Object> subtypes, List<Object> weaknesses,
                    List<Object> resistances, List<Object> types, List<Object> evolvesTo,
                    Map<String, Object> prices) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.number = number;
        this.rarity = rarity;
        this.attacks = attacks;
        this.subtypes = subtypes;
        this.weaknesses = weaknesses;
        this.resistances = resistances;
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

    public String getHp() {
        return hp;
    }

    public String getRarity() {
        return rarity;
    }

    public String getNumber() { return number; }

    public List<Object> getAttacks() {
        return attacks;
    }

    public List<Object> getSubtypes() { return subtypes; }

    public List<Object> getWeaknesses() { return weaknesses; }

    public List<Object> getResistances() { return resistances; }

    public List<Object> getTypes() {
        return types;
    }

    public List<Object> getEvolvesTo() { return evolvesTo; }

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

    public void setPrices(Map<String, Object> prices) { this.prices = prices; }
}
