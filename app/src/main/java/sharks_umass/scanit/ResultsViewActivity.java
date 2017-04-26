package sharks_umass.scanit;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ResultsViewActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private ImageView imageView;
    private TextView titleView, descriptionView;
    private com.google.android.gms.common.api.GoogleApiClient GoogleApiClient;
    public DriveFile file;
    private File textFile;
    private static final String TAG = "Upload_file";
    private static final int REQUEST_CODE = 101;
    public static String drive_id;
    public static DriveId driveID;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);
        imageView = (ImageView) findViewById(R.id.imageView);
        titleView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        Intent shared = getIntent();
        titleView.setText(shared.getStringExtra("title"));
        descriptionView.setText(shared.getStringExtra("description"));
        textFile = new File(Environment.getExternalStorageDirectory()
                + File.separator +"Download"+ File.separator + "test.txt");
        verifyStoragePermissions(this);
    }

    public void onClickUploadFile(View view){
        //start the uploading process
        GoogleApiClient.connect();
        Drive.DriveApi.newDriveContents(GoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    /*build the google api client*/
    private void buildGoogleApiClient() {
        if (GoogleApiClient == null) {
            GoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    /*connect client to Google Play Services*/
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "In onStart() - connecting...");
        buildGoogleApiClient();
    }

    /*close connection to Google Play Services*/
    @Override
    protected void onStop() {
        super.onStop();
        if (GoogleApiClient != null) {
            Log.i(TAG, "In onStop() - disConnecting...");
            GoogleApiClient.disconnect();
        }
    }

    /**
     * Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack,
     * with user input going to it.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (GoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            GoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        //GoogleApiClient.connect();
    }

    /*Handles onConnectionFailed callbacks*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i(TAG, "In onActivityResult() - connecting...");
            GoogleApiClient.connect();
        }
    }

    /*handles connection callbacks*/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "in onConnected() - we're connected, let's do the work in the background...");
        Drive.DriveApi.newDriveContents(GoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    /*handles suspended connection callbacks*/
    @Override
    public void onConnectionSuspended(int cause) {
        switch (cause) {
            case 1:
                Log.i(TAG, "Connection suspended - Cause: " + "Service disconnected");
                break;
            case 2:
                Log.i(TAG, "Connection suspended - Cause: " + "Connection lost");
                break;
            default:
                Log.i(TAG, "Connection suspended - Cause: " + "Unknown");
                break;
        }
    }

    /*callback on getting the drive contents, contained in result*/
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Error creating new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();
                    new Thread() {
                        @Override
                        public void run() {
                            OutputStream outputStream = driveContents.getOutputStream();
                            addTextfileToOutputStream(outputStream);
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("testFile")
                                    .setMimeType("text/plain")
                                    .setDescription("This is a text file uploaded from device")
                                    .setStarred(true).build();
                            Drive.DriveApi.getRootFolder(GoogleApiClient)
                                    .createFile(GoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    /*get input stream from text file, read it and put into the output stream*/
    private void addTextfileToOutputStream(OutputStream outputStream) {
        Log.i(TAG, "adding text file to outputstream...");
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(textFile));
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.i(TAG, "problem converting input stream to output stream: " + e);
            e.printStackTrace();
        }
    }

    /*callback after creating the file, can get file info out of the result*/
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Error creating the file");
                        Toast.makeText(ResultsViewActivity.this,
                                "Error adding file to Drive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "File added to Drive");
                    Log.i(TAG, "Created a file with content: "
                            + result.getDriveFile().getDriveId());
                    Toast.makeText(ResultsViewActivity.this,
                            "File successfully added to Drive", Toast.LENGTH_SHORT).show();
                    final PendingResult<DriveResource.MetadataResult> metadata
                            = result.getDriveFile().getMetadata(GoogleApiClient);
                    metadata.setResultCallback(new
                                                       ResultCallback<DriveResource.MetadataResult>(){
                                                           @Override public void onResult(DriveResource.MetadataResult metadataResult) {
                                                               Metadata data = metadataResult.getMetadata();
                                                               Log.i(TAG, "Title: " + data.getTitle());
                                                               drive_id = data.getDriveId().encodeToString();
                                                               Log.i(TAG, "DrivId: " + drive_id);
                                                               driveID = data.getDriveId();
                                                               Log.i(TAG, "Description: " + data.getDescription().toString());
                                                               Log.i(TAG, "MimeType: " + data.getMimeType());
                                                               Log.i(TAG, "File size: " + String.valueOf(data.getFileSize()));
                                                           }
                                                       });
                }
            };

    /*callback when there there's an error connecting the client to the service.*/
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed");
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            Log.i(TAG, "trying to resolve the Connection failed error...");
            result.startResolutionForResult(this, REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
