package sharks_umass.scanit;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Saves a file to the user's phone.
 * Created by Tyler on 4/23/2017.
 */

public class SaveToPhone {

    private final String TAG = "SaveToPhone";

    /**
     *
     * @param fileName The name the file to be saved, i.e. "foo.txt"
     * @param bytes The file's information in bytes
     * @param c App's context
     * @throws IOException Make sure file url exists!
     */
    public void save(String fileName, byte[] bytes, Context c) {
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "Can't write to external storage!");
            return;
        }
        FileOutputStream outputStream;
        File savedFile = new File(c.getExternalFilesDir(null), fileName);
        try {
            outputStream = c.openFileOutput(savedFile.getAbsolutePath(), Context.MODE_PRIVATE);
            outputStream.write(bytes);
            outputStream.close();
        }
        catch (IOException io){
            Log.e(TAG, "Could not write file: " + io.getMessage());
        }
    }

    /**
     *  Taken from Android Developers website
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}