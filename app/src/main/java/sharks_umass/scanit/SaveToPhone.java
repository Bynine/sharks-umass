package sharks_umass.scanit;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tyler on 4/23/2017.
 */

public class SaveToPhone {

    FileOutputStream outputStream;

    public void save(String filename, String toSave, Context c) throws IOException {
        outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(toSave.getBytes());
        outputStream.close();
        File file = new File(c.getFilesDir(), filename);
    }
}
