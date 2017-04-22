package sharks_umass.scanit.apis;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextRecognizer;
import java.nio.ByteBuffer;

/**
 * Converts an image input into a String output.
 * Created by Tyler on 4/12/2017.
 */

public class ImageProcessor extends AppCompatActivity {

    public static final int NV16 = 16, NV21 = 17, YV12 = 842094169;

    private final Frame.Builder frameBuilder = new Frame.Builder();
    Context context;

    /**
     * Constructs an Image Processor with the appropriate context
     * @param c the context
     */
    public ImageProcessor(Context c){
        this.context = c;
    }

    /**
     * Gets text from an image's ByteBuffer, width, height, and format
     * @param data ByteBuffer data from image
     * @param width Image width (pixels?)
     * @param height Image height (pixels?)
     * @param format Byte format. One of NV16, NV21, or YV12.
     */
    public String convertInputToText(ByteBuffer data, int width, int height, int format) {
        frameBuilder.setImageData(data, width, height, format);
        return convert();
    }

    /**
     * Gets text from a Bitmap object
     * @param bitmap Bitmap object of image
     */
    public String convertInputToText(Bitmap bitmap){
        frameBuilder.setBitmap(bitmap);
        return convert();
    }

    private String convert(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        Frame f = frameBuilder.build();
        return textRecognizer.detect(f).toString();
    }

}