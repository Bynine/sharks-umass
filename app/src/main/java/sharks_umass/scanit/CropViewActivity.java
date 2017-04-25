package sharks_umass.scanit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CropViewActivity extends AppCompatActivity implements CropImageView.OnCropImageCompleteListener {

    private CropImageView cropImageView;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);
        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic.jpg");
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setOnCropImageCompleteListener(this);
        cropImageView.setImageBitmap(bmp);
        cropImageView.rotateImage(90);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {

    }
}
