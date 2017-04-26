package sharks_umass.scanit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.os.AsyncTaskCompat;
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
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import sharks_umass.scanit.apis.Definer;
import sharks_umass.scanit.apis.DefinerResult;
import sharks_umass.scanit.apis.ImageProcessor;
import sharks_umass.scanit.apis.Solver;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CropViewActivity extends AppCompatActivity implements OnClickListener {

    private CropImageView cropImageView;
    private ImageButton convert, define, solve;

    private class SolverAsync extends AsyncTask<Void, Void, Void> {

        private String response = "";
        private String finalResult = "";

        SolverAsync(String response) {
            this.response = response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            Solver solver = new Solver();
            finalResult = solver.solve(response);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            generateTextFile(response, finalResult, 1);
            Intent i = new Intent(getApplicationContext(), ResultsViewActivity.class);
            i.putExtra("title", response);
            i.putExtra("description", finalResult);
            i.putExtra("imageType", "solve");
            startActivity(i);
            cropImageView.clearImage();
        }
    }

    private class DefinerAsync extends AsyncTask<Void, Void, Void>
    {
        private String response = "";
        private DefinerResult finalDefinerResult;

        DefinerAsync(String response) {
            this.response = response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            Definer definer = new Definer();
            finalDefinerResult = definer.define(response);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            generateTextFile(response, finalDefinerResult.getDefinition().split(":")[1] + "." + finalDefinerResult.getExample(), 0);
            Intent i = new Intent(getApplicationContext(), ResultsViewActivity.class);
            i.putExtra("title", finalDefinerResult.getWord());
            i.putExtra("description", finalDefinerResult.getDefinition().split(":")[1] + "\n\n" + finalDefinerResult.getExample());
            i.putExtra("imageType", "define");
            startActivity(i);
            cropImageView.clearImage();
        }

    }

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
        Intent i = new Intent(getApplicationContext(), ResultsViewActivity.class);
        Bitmap croppedImage = cropImageView.getCroppedImage();
        String response = new ImageProcessor().convertBitmapToText(croppedImage, getApplicationContext());
        switch (v.getId()) {
            case R.id.convert:
                i.putExtra("title", "Document Ready");
                i.putExtra("description", response);
                i.putExtra("imageType", "convert");
                cropImageView.clearImage();
                startActivity(i);
                break;
            case R.id.define:
                new DefinerAsync(response).execute();
                break;
            case R.id.solve:
                Toast.makeText(getApplicationContext(), "Processing Request", Toast.LENGTH_SHORT).show();
                new SolverAsync(response).execute();
                break;
        }
    }

    private void generateTextFile(String source, String response, int choice) {
        try  {
            PrintWriter writer = new PrintWriter(new File(Environment.getExternalStorageDirectory() + "/scanit_export.txt"));
            switch (choice) {
                case 0:
                    writer.println("Word: " + source + "\n\n" + "Definition: " + response.split(".")[0] + "\n\n" + "Example: " + response.split(".")[1]);
                    break;
                case 1:
                    writer.println("Equation: " + source + "\n\n" + "Results: " + response);
                    break;
                default:
                    writer.println(response);
            }
            writer.close();
        }
        catch (FileNotFoundException e) {}
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
