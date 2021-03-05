package edu.fsu.cs.PokeBox;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class ScannerFragment extends Fragment {
View view;
private Camera camera;
private CameraPreview mPreview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.fragment_scanner, container, false);

        //if device has a camera, open an instance, set the capture button and set the preview
        if(checkCameraHardware(getContext())) {
            camera = getCameraInstance();

            //setting the camera capture button
            ImageButton capture = view.findViewById(R.id.capture);
            capture.setImageResource(R.drawable.camera_capture);

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(getContext(), camera);
            FrameLayout preview = view.findViewById(R.id.camera);
            preview.addView(mPreview);


        }

        return view;
    }

    private boolean checkCameraHardware(Context context) {
        //checking if device has a camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
        }

        //returning camera
        return c;
    }



    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private final SurfaceHolder mHolder;
        private final Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback
            mHolder = getHolder();
            mHolder.addCallback(this);
            // required for Android version 3.0+
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // Start the preview with the surface created
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            //releasing camera in activity
        }

        //for making changes on the preview
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
            }

            // set any preview change



            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
