package edu.fsu.cs.PokeBox;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class ScannerFragment extends Fragment {

    // Storage/Camera Permissions
    private static final int REQUEST_PERMS = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int CAMERA_LAUNCH_REQUEST = 1348;

    View view;
    private ImageView imageView;
    private Button startCamera;
    private Uri image_uri;
    private TextView resView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.fragment_scanner, container, false);

        resView = view.findViewById(R.id.scanResult);

        //set up fragment background
        imageView = view.findViewById(R.id.camera_background);

        //set up camera launcher button
        startCamera = view.findViewById(R.id.capture);
        startCamera.setOnClickListener(v -> {
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
            startCamera.setText("Processing Photo...");

            // process photo
            processPhoto();

            startCamera.setEnabled(true);
            startCamera.setText(R.string.camera_launch_button_text);
        }
    }

    private void processPhoto() {
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // process image
        InputImage img;
        try {
            img = InputImage.fromFilePath(Objects.requireNonNull(getContext()), image_uri);

            TextRecognizer recognizer = TextRecognition.getClient();
            // Task failed with exception
            Task<Text> result = recognizer.process(img).addOnSuccessListener(text -> {
                // Task completed successfully
                resView.setVisibility(View.VISIBLE);
                resView.setBackgroundColor(getResources().getColor(R.color.black));
                resView.setText(text.getText());
//                    for (Text.TextBlock block : text.getTextBlocks()) {
//                        String blockText = block.getText();
//                        for (Text.Line line : block.getLines()) {
//                            String lineText = line.getText();
//                            for (Text.Element element: line.getElements()) {
//                                String elementText = element.getText();
//                                Toast.makeText(getContext(), elementText, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }

            }).addOnFailureListener(Throwable::printStackTrace);
        } catch(IOException e) {
            e.printStackTrace();
        }

        imageView.setImageURI(image_uri);

    }
}
