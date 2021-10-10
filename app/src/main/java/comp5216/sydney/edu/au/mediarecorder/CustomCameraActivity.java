package comp5216.sydney.edu.au.mediarecorder;

import android.app.Activity;
import android.hardware.Camera;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class which handles logic behind the custom camera interface view which the user visits on
 * clicking the Open Camera button
 */
public class CustomCameraActivity extends Activity {
    private boolean recordMode; // true -> record button, false -> stop button
    private Camera camera;
    private MediaRecorder videoRecorder;
    private FrameLayout frameLayout;
    private Button recordVideoButton;
    private CameraSurfaceView cameraSurfaceView;

    private Camera.PictureCallback mPictureCallback  = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // set file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            String photoFileName = "IMG_" + timeStamp + ".jpg"; // Create a photo file reference
            File file = getFileUri(photoFileName);

            if (file == null) {
                // Activity finishes OK, return the data
                setResult(RESULT_CANCELED); // Set result code and bundle data for response
                finish(); // Close the activity, pass data to parent
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();

                camera.startPreview();
                Log.i("Message", "picture saved to gallery 📷");

                // Activity finishes OK, return the data
                setResult(RESULT_OK); // Set result code and bundle data for response
                finish(); // Close the activity, pass data to parent

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_camera);

        frameLayout = (FrameLayout) findViewById(R.id.cameraFrameLayout);
        recordVideoButton = (Button) findViewById(R.id.recordVideo);
        recordMode = true;

        // Setup Camera
        camera = Camera.open();
        cameraSurfaceView = new CameraSurfaceView(this, camera);
        frameLayout.addView(cameraSurfaceView);

    }

    // Returns the Uri for a photo/media stored on disk given the fileName and type
    private File getFileUri(String fileName) {
        try {
            // Get safe media storage directory depending on type
            String storagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath();
            File mediaStorageDir = new File(storagePath);
            Log.i("FILE URI", "🔗" + storagePath);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
                Log.i("Folder Created", "✅");
            }

            // Create the file target for the media based on filename
            File outputFile = new File(mediaStorageDir, fileName);

            return outputFile;

        } catch (Exception ex) {
            Log.e("getFileUri", ex.getStackTrace().toString());
        }
        return null;
    }

    /**
     * On click handler for the Take Photo 📷 button
     * @param view
     */
    public void onTakePhotoClick(View view) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallback);
        }
    }

    /**
     * On click Handler for the Record Video 📹 and Stop 🔴 Buttons
     * @param view
     * @throws IOException
     */
    public void onRecordVideoClick(View view) throws IOException {
        if (recordMode) {
            recordVideoButton.setText("🔴");

            camera.unlock();
            videoRecorder = new MediaRecorder();
            videoRecorder.setCamera(camera);
            videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // create a video file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            String videoFileName = "VID_" + timeStamp + ".mp4"; // Create a video file reference
            File file = getFileUri(videoFileName);

            videoRecorder.setOrientationHint(90);
            videoRecorder.setProfile(CamcorderProfile.get(1));
            videoRecorder.setOutputFile(file.getAbsolutePath());
            videoRecorder.prepare(); // Prepare MediaRecorder
            videoRecorder.start(); // Start Recording

        } else {
            videoRecorder.stop(); // Stop Recording
            videoRecorder.reset();
            videoRecorder.release();

            camera.lock();
            camera.stopPreview();
            camera.release();

            // Activity finishes OK, return the data
            setResult(RESULT_OK); // Set result code and bundle data for response
            finish(); // Close the activity, pass data to parent
        }
        recordMode = !recordMode;
    }
}
