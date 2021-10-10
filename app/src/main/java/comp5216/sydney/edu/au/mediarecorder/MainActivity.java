package comp5216.sydney.edu.au.mediarecorder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Activity Class. Handles logic behind the MainView that the user sees when (s)he opens
 * the app.
 */
public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    private GridView gridView;
    private CustomMediaAdapter gridAdapter;
    private MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    private ArrayList<String> mediaPaths = new ArrayList<String>();

    // Variables to check connection
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private boolean wifiConnected = false;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    private boolean toUpdate = false;
    private Timer myTimer;

    // Time Period
    private final int FG_PERIOD = 5 * 60000;

    // Register a request to start an activity for result and register the result callback
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    updateMediaPaths();
                    gridAdapter = new CustomMediaAdapter(this, mediaPaths);
                    gridView.setAdapter(gridAdapter);
                    Log.i("Message", "All g broski 🤙");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // Request for permissions
        if (!marshmallowPermission.checkPermissionForCamera() ||
            !marshmallowPermission.checkPermissionForRecord() ||
            !marshmallowPermission.checkPermissionForExternalStorage()) {
            marshmallowPermission.requestPermissions();
        }

        updateMediaPaths();
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new CustomMediaAdapter(this, mediaPaths);
        gridView.setAdapter(gridAdapter);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            wifiConnected = true;
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED,
                            false)) {
                        // WiFi Connection Established
                        wifiConnected = true;
                        Log.i("WIFI CONNECTION 😈", "CONNECTED");
                        if (toUpdate) {
                            Log.i("AUTOMATIC UPDATE", "WIFI Connection Established" +
                                    "... syncronising now");
                            updateMediaPaths();
                            updateCloud(mediaPaths, true);
                        }
                    } else {
                        // WiFi connection was lost
                        wifiConnected = false;
                        Log.i("WIFI CONNECTION 😈", "DISCONNECTED");
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        myTimer = new Timer();
    }

    private void updateMediaPaths() {
        mediaPaths.clear();
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++) {
            mediaPaths.add(files[i].getAbsolutePath());
            Log.d("Files", "FilePath:" + mediaPaths.get(i));
        }
    }

    private void updateCloud(ArrayList<String> mediaURIStrings, boolean displayToast) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        for (String mediaFilePath: mediaURIStrings) {
            // Uploading All Files
            StorageReference fileReference = storageRef.child(mediaFilePath);
            Uri fileNameURI = Uri.fromFile(new File(mediaFilePath));
            fileReference.putFile(fileNameURI).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (displayToast){
                                Toast.makeText(getApplicationContext(), "Media Files Uploaded",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (displayToast) {
                                Toast.makeText(getApplicationContext(), "Something Went Wrong",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * On click handler for the Open Camera button
     * @param view
     */
    public void onOpenCameraClick(View view) {
        // Handler for Open Camera Button Click
        Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
        mLauncher.launch(intent);
    }

    /**
     * On click handler for the Update Cloud button
     * @param view
     */
    public void onUpdateCloudClick(View view) {
        // Handler for Update Cloud Button Click
        if (wifiConnected) {
            updateCloud(mediaPaths, true);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Syncronise with Cloud Server")
                    .setMessage("You are not connected to WiFi. Would you like to proceed?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateCloud(mediaPaths, true);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            toUpdate = true;
                            String msg = "Data will be backed up when this device is " +
                                    "connected to a WIFI network.";
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * Function which is triggered when the app switches to background
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Log.i("APP RUNNING STATUS", "BACKGROUND 😭");

        // Stop timer when app is running in bg
        myTimer.cancel();
    }

    /**
     * Function which is triggered when the app switches to foreground or when app is started
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Log.i("APP RUNNING STATUS", "FOREGROUND 🤩");

        // Restart timer as app comes to FOREGROUND
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (wifiConnected) {
                    // Automatic Update Cloud
                    updateMediaPaths();
                    updateCloud(mediaPaths, false);

                    Log.i("AUTOMATIC UPDATE", "AFTER "  + Integer.toString(FG_PERIOD)
                            + " ms ⏰");
                } else {
                    toUpdate = true;
                }

            }
        }, 0, FG_PERIOD);
    }
}