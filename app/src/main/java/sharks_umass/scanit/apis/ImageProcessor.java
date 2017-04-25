package sharks_umass.scanit.apis;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.nio.ByteBuffer;

/**
 * Converts an image input into a String output.
 * Created by Tyler on 4/12/2017.
 */

public class ImageProcessor extends AppCompatActivity {

    public static final int NV16 = 16, NV21 = 17, YV12 = 842094169;
    private final Frame.Builder frameBuilder = new Frame.Builder();
    private final String NOTEXT = "No text detected";

    /**
     * Gets text from an image's ByteBuffer, width, height, and format
     * @param data ByteBuffer data from image
     * @param width Image width (pixels?)
     * @param height Image height (pixels?)
     * @param format Byte format. Use the values present in ImageProcessor.
     */
    public String convertDataToText(ByteBuffer data, int width, int height, int format, Context context) {
        frameBuilder.setImageData(data, width, height, format);
        return convert(context);
    }

    /**
     * Gets text from a Bitmap object
     * @param bitmap Bitmap object of image
     */
    public String convertBitmapToText(Bitmap bitmap, Context context){
        frameBuilder.setBitmap(bitmap);
        return convert(context);
    }

    private String convert(Context context) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        Frame f = frameBuilder.build();
        SparseArray<TextBlock> items = textRecognizer.detect(f);
        String result = "";
        for (int i = 0; i < items.size(); ++i){
            TextBlock item = items.get(i);
            result = result.concat(item.getValue());
        }
        if (result.isEmpty()) result = NOTEXT;
        textRecognizer.release();
        return result;
    }

}