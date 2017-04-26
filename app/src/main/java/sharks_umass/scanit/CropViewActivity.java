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
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.io.File;

import sharks_umass.scanit.apis.Definer;
import sharks_umass.scanit.apis.DefinerResult;
import sharks_umass.scanit.apis.ImageProcessor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CropViewActivity extends AppCompatActivity implements OnClickListener {

    private CropImageView cropImageView;
    private ImageButton convert, define, solve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic.jpg"));
        cropImageView.rotateImage(90);
        convert = (ImageButton) findViewById(R.id.convert);
        define = (ImageButton) findViewById(R.id.define);
        solve = (ImageButton) findViewById(R.id.solve);
        convert.setOnClickListener(this);
        define.setOnClickListener(this);
        solve.setOnClickListener(this);
    }

    private void setupPopup(View v) {
//        cropImageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic.jpg"));
        Intent i = new Intent(getApplicationContext(), ResultsViewActivity.class);
        Bitmap croppedImage = cropImageView.getCroppedImage();
        String response = new ImageProcessor().convertBitmapToText(croppedImage, getApplicationContext());
        switch (v.getId()) {
            case R.id.convert:
                Log.d("RESPONSE", response);
                i.putExtra("title", "Document Ready");
                i.putExtra("description", response);
                i.putExtra("imageType", "convert");
                break;
            case R.id.define:
                // definer API
                i.putExtra("title", "Document Ready");
                i.putExtra("description", response);
                i.putExtra("imageType", "convert");
                break;
            case R.id.solve:
                // solver
                i.putExtra("title", "Document Ready");
                i.putExtra("description", response);
                i.putExtra("imageType", "convert");
                break;
        }
        cropImageView.clearImage();
        startActivity(i);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(define)) Log.d("TEST", "EQUAL");
        else Log.d("TEST", "NOT EQUAL");
        setupPopup(v);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}
