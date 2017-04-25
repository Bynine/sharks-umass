package sharks_umass.scanit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.io.File;

import sharks_umass.scanit.apis.ImageProcessor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CropViewActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private ImageButton convert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);
        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic.jpg");
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(bmp);
        String response = new ImageProcessor().convertBitmapToText(bmp, getApplicationContext());
        Log.d("SUCCESS", response);
        cropImageView.rotateImage(90);
        convert = (ImageButton) findViewById(R.id.convert);
        bmp = null;
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPopup();
            }
        });
    }

    private void setupPopup() {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
