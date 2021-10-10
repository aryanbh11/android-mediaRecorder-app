package comp5216.sydney.edu.au.mediarecorder;

import android.Manifest;
import android.app.Activity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Permissions class taken directly from the lab sheet. Used to ask user for permission to access
 * various features of their device.
 */
public class MarshmallowPermission {

    private Activity activity;

    /**
     * Constructor to create an instance of this class
     * @param activity
     */
    public MarshmallowPermission(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check if app has permission to record audio
     * @return true if app has permission and false if it doesn't
     */
    public boolean checkPermissionForRecord(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if app has permission to write to device storage
     * @return true if app has permission and false if it doesn't
     */
    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if app has permission to use device camera
     * @return true if app has permission and false if it doesn't
     */
    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Request permissions for camera, audio and file writing
     */
    public void requestPermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)){
            Toast.makeText(activity, "Camera permission needed. Please allow in App " +
                    "Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    3);
            System.out.print("get camera permission");
        }
    }

}
