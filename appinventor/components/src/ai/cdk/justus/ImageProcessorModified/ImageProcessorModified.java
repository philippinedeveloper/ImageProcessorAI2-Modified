package ai.cdk.justus.ImageProcessorModified;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.MediaUtil;
import com.google.appinventor.components.runtime.*;

import java.io.File;
import java.util.Date;

@DesignerComponent(versionName = ImageProcessorModified.VERSION_NAME,
    version = 2,
    description = "A component for complex image processing in MIT App Inventor.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject(external = true)
public class ImageProcessorModified extends AndroidNonvisibleComponent implements Component {

    public static final String VERSION = "1.1";
    public static final float DEFAULT_WEIGHT = 0.5f;
    private ComponentContainer container;
    private float weight = DEFAULT_WEIGHT;
    
    public ImageProcessorModified(ComponentContainer container) {
        super(container.$form());
        this.container = container;
        Log.d("CDK", "ImageProcessorModified Created");
    }

    @SimpleProperty(category = PropertyCategory.BEHAVIOR)
    public float Weight() {
        return weight;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT, defaultValue = "0.5")
    @SimpleProperty(description = "Weight for image processing. Range: [0,1]")
    public void Weight(float newWeight) {
        this.weight = Math.max(0, Math.min(1, newWeight));
    }

    @SimpleFunction
    public void ImageCombine(String imageA, String imageB) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapB = MediaUtil.getBitmapDrawable(container.$form(), imageB).getBitmap();
            
            int maxW = Math.max(bitmapA.getWidth(), bitmapB.getWidth());
            int maxH = Math.max(bitmapA.getHeight(), bitmapB.getHeight());
            Bitmap bitmapC = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < maxW; x++) {
                for (int y = 0; y < maxH; y++) {
                    int colA = bitmapA.getPixel(x, y);
                    int colB = bitmapB.getPixel(x, y);
                    int aC = (int) (Color.alpha(colA) * weight + Color.alpha(colB) * (1.0 - weight));
                    int rC = (int) (Color.red(colA) * weight + Color.red(colB) * (1.0 - weight));
                    int gC = (int) (Color.green(colA) * weight + Color.green(colB) * (1.0 - weight));
                    int bC = (int) (Color.blue(colA) * weight + Color.blue(colB) * (1.0 - weight));
                    bitmapC.setPixel(x, y, Color.argb(aC, rC, gC, bC));
                }
            }
            saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    @SimpleFunction
    public String ImageGrey(String imageA) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapC = Bitmap.createBitmap(bitmapA.getWidth(), bitmapA.getHeight(), Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < bitmapA.getWidth(); x++) {
                for (int y = 0; y < bitmapA.getHeight(); y++) {
                    int colA = bitmapA.getPixel(x, y);
                    int avgC = (Color.red(colA) + Color.green(colA) + Color.blue(colA)) / 3;
                    bitmapC.setPixel(x, y, Color.argb(Color.alpha(colA), avgC, avgC, avgC));
                }
            }
            return saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
            return "";
        }
    }

    @SimpleFunction
    public String ImageBlur(String imageA) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapC = Bitmap.createBitmap(bitmapA);
            
            for (int x = 1; x < bitmapA.getWidth() - 1; x++) {
                for (int y = 1; y < bitmapA.getHeight() - 1; y++) {
                    int colA = bitmapA.getPixel(x, y);
                    int colB = bitmapA.getPixel(x - 1, y);
                    int colC = bitmapA.getPixel(x + 1, y);
                    int colD = bitmapA.getPixel(x, y - 1);
                    int colE = bitmapA.getPixel(x, y + 1);
                    
                    int avgR = (Color.red(colA) + Color.red(colB) + Color.red(colC) + Color.red(colD) + Color.red(colE)) / 5;
                    int avgG = (Color.green(colA) + Color.green(colB) + Color.green(colC) + Color.green(colD) + Color.green(colE)) / 5;
                    int avgB = (Color.blue(colA) + Color.blue(colB) + Color.blue(colC) + Color.blue(colD) + Color.blue(colE)) / 5;
                    
                    bitmapC.setPixel(x, y, Color.argb(Color.alpha(colA), avgR, avgG, avgB));
                }
            }
            return saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
            return "";
        }
    }

    private String saveAndDispatch(Bitmap bitmap) {
        File image = new File(Environment.getExternalStorageDirectory(), "/Cimage.png");
        try (FileOutputStream fostream = new FileOutputStream(image)) {
            bitmap.compress(CompressFormat.PNG, 100, fostream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AfterProcess(image.getAbsolutePath());
        return image.getAbsolutePath();
    }

    @SimpleEvent
    public void AfterProcess(String image) {
        EventDispatcher.dispatchEvent(this, "AfterProcess", image);
    }
}

