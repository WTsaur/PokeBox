package edu.fsu.cs.PokeBox;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ScannerFragment extends Fragment implements CardsAdapter.OnCardClickListener {

    // Storage/Camera Permissions
    private static final int REQUEST_PERMS = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int CAMERA_LAUNCH_REQUEST = 1348;

    private PokeCard selectedCard;

    // variables related to Download task
    private static int DOWNLOAD_TASKS = 0;
    private static int TASKS_COMPLETED = 0;
    private final List<PokeCard> cards = new ArrayList<>();

    // elements
    private Button startCamera;
    private Uri image_uri;
    private TextView load_text;
    private RecyclerView recyclerView;
    private Button btnSelect;
    private ImageView selectedCardView;
    private Button btnCancel;

    private static final String API_KEY = "b87cf1ba-0a63-4073-9135-7573becb8002";
    private static final String BASE_URL = "https://api.pokemontcg.io/v2/cards?X-Api-Key:" + API_KEY + "&q=name:\"";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_scanner, container, false);

        int pCheck = 0;
        for (String permission : PERMISSIONS) {
            int permStatus = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permission);
            pCheck += permStatus;
        }
        if (pCheck != 0) {
            requestPermissions();
        }

        //set up elements
        selectedCardView = view.findViewById(R.id.selected_card_view);
        load_text = view.findViewById(R.id.loading_text);

        recyclerView = view.findViewById(R.id.scanner_card_recycler);

        btnCancel = view.findViewById(R.id.btnCancelSelection);
        btnCancel.setOnClickListener(v -> {
            btnSelect.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            selectedCardView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            startCamera.setVisibility(View.VISIBLE);
        });

        btnSelect = view.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(v -> {
            btnSelect.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            selectedCardView.setVisibility(View.INVISIBLE);

            // Save card to database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(MainActivity.CURRENT_USER).child(selectedCard.getId());
            reference.setValue(selectedCard);

            Toast.makeText(getContext(), "Card Added to Collection!", Toast.LENGTH_LONG).show();
            recyclerView.setVisibility(View.INVISIBLE);
            startCamera.setVisibility(View.VISIBLE);
        });


        //set up camera launcher button
        startCamera = view.findViewById(R.id.capture);
        startCamera.setOnClickListener(v -> {
            recyclerView.setVisibility(View.INVISIBLE);
            int permCheck = 0;
            for (String permission : PERMISSIONS) {
                int permStatus = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permission);
                permCheck += permStatus;
            }
            if (permCheck != 0) {
                requestPermissions();
            } else {
                launchCamera();
            }
        });
        return view;
    }

    private void requestPermissions() {
        Vector<String> v = new Vector<>();
        for (String permission : PERMISSIONS) {
            int status = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permission);
            if (status != PackageManager.PERMISSION_GRANTED) {
                v.add(permission);
            }
        }
        String[] permission_list = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            permission_list[i] = v.get(i);
        }
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), permission_list, REQUEST_PERMS);
    }

    private void launchCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "PokeBox Card Scan");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera Fragment");
        image_uri = Objects.requireNonNull(getContext()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAMERA_LAUNCH_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMS) {
            int check = 0;
            for (int i = 0; i < permissions.length; i++) {
                check += grantResults[i];
            }
            if (check == 0) {
                launchCamera();
            } else {
                Toast.makeText(getContext(), "Some permissions were denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_LAUNCH_REQUEST && resultCode == Activity.RESULT_OK) {
            
            startCamera.setEnabled(false);

            // process photo
            processPhoto();

            startCamera.setEnabled(true);
        }
    }

    private void processPhoto() {
        // process image
        InputImage img;
        try {
            img = InputImage.fromFilePath(Objects.requireNonNull(getContext()), image_uri);

            TextRecognizer recognizer = TextRecognition.getClient();
            // Task failed with exception
            recognizer.process(img).addOnSuccessListener(text -> {
                // Task completed successfully
                if (text.getText().length() != 0) {
                    Text.TextBlock block = text.getTextBlocks().get(0);
                    Text.Line line = block.getLines().get(0);
                    String lineText = line.getText();

                    // Parse Line
                    String[] tokens = lineText.split(" ");
                    StringBuilder str = new StringBuilder();

                    // Parse Tokens and attempt to extract pokemon name
                    for (String token : tokens) {
                        String tok = token;

                        int idx = tok.indexOf("(");
                        if (idx > -1) {
                            str.append(tok.substring(idx + 1));
                            continue;
                        }

                        idx = tok.indexOf(")");
                        if (idx > -1) {
                            str.append(tok.substring(idx + 1));
                            continue;
                        }

                        int stagIdx = tok.indexOf("STAGE");
                        if (stagIdx > -1) {
                            if (tok.length() > 5) {
                                if (tok.charAt(5) == 'T' && tok.charAt(4) == 'E') {
                                    tok = tok.substring(6);
                                } else {
                                    tok = tok.substring(5);
                                }
                            } else continue;
                        }

                        int basIdx = tok.indexOf("BASIC");
                        if (basIdx == -1 && !tok.matches(".*[0-9]+.*")) {
                            if (str.length() != 0) {
                                str.append(" ");
                            }
                            str.append(tok);
                        }
                    }

                    String pokeName = str.toString();
                    if (pokeName.length() == 0) {
                        Toast.makeText(getContext(), "Unable to extract Pokemon's Name. Try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Send query request to TCG API to check if pokeName exists
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = BASE_URL + pokeName + "\"";

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                        try {
                            int count = response.getInt("count");
                            if (count != 0) {
                                JSONArray pokeData = response.getJSONArray("data");

                                // Fetch data that will be the same across all variations of current pokemon
                                JSONObject obj = pokeData.getJSONObject(0);

                                String name = obj.getString("name");
                                String HP = obj.getString("hp");
                                String rarity = obj.getString("rarity");

                                JSONArray attacksArr;
                                List<Object> attacks = new ArrayList<>();
                                if (obj.has("attacks")) {
                                    attacksArr = obj.getJSONArray("attacks");
                                    for (int i = 0; i < attacksArr.length(); i++) {
                                        attacks.add(attacksArr.getString(i));
                                    }
                                } else {
                                    attacks.add("");
                                }

                                JSONArray typeArr;
                                List<Object> types = new ArrayList<>();
                                if (obj.has("types")) {
                                    typeArr = obj.getJSONArray("types");
                                    for (int i = 0; i < typeArr.length(); i++) {
                                        types.add(typeArr.getString(i));
                                    }
                                } else {
                                    types.add("");
                                }


                                JSONArray evolveArr;
                                List<Object> evolvesTo = new ArrayList<>();
                                if (obj.has("evolvesTo")) {
                                    evolveArr = obj.getJSONArray("evolvesTo");
                                    for (int i = 0; i < evolveArr.length(); i++) {
                                        evolvesTo.add(evolveArr.getString(i));
                                    }
                                } else {
                                    evolvesTo.add("");
                                }

                                // Prep variables and views for download tasks
                                DOWNLOAD_TASKS = count;
                                TASKS_COMPLETED = 0;
                                load_text.setVisibility(View.VISIBLE);
                                String txt = "Loading... 0/" + DOWNLOAD_TASKS;
                                load_text.setText(txt);
                                cards.clear();

                                for (int i = 0; i < pokeData.length(); i++) {
                                    JSONObject pokeObj = pokeData.getJSONObject(i);
                                    JSONObject pokeImages = pokeObj.getJSONObject("images");
                                    String id = pokeObj.getString("id");
                                    String imgUrlStr = pokeImages.getString("large");
                                    JSONObject tcgPlayer;
                                    JSONObject pricesJSON;
                                    Map<String, Object> prices = new HashMap<>();
                                    if (pokeObj.has("tcgplayer")) {
                                        tcgPlayer = pokeObj.getJSONObject("tcgplayer");
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
                                        prices.put("none", -1);
                                    }

                                    PokeCard card = new PokeCard(id, name, HP, rarity, attacks, types, evolvesTo, prices);
                                    card.setImageUrl(imgUrlStr);
                                    cards.add(card);

                                    new DownloadTask().execute(stringToURL(imgUrlStr));
                                }
                            } else {
                                Toast.makeText(getContext(), "Unable to find pokemon: " + pokeName, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("REQUEST ERROR: ", error.toString()));
                    queue.add(request);
                } else {
                    Toast.makeText(getContext(), "No Text Found!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(Throwable::printStackTrace);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMatches() {
        CardsAdapter cardsAdapter = new CardsAdapter(cards, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cardsAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnCardClick(int position) {
        PokeCard card = cards.get(position);
        startCamera.setVisibility(View.INVISIBLE);
        btnSelect.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        selectedCard = card;
        selectedCardView.setImageBitmap(card.getImageBitmap());
        selectedCardView.setVisibility(View.VISIBLE);
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
                cards.get(TASKS_COMPLETED).setImageBitmap(result);
                TASKS_COMPLETED += 1;
                String loadTxt = "Loading... " + TASKS_COMPLETED + "/" + DOWNLOAD_TASKS;
                load_text.setText(loadTxt);
                if (TASKS_COMPLETED == DOWNLOAD_TASKS) {
                    displayMatches();
                    load_text.setVisibility(View.INVISIBLE);
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
