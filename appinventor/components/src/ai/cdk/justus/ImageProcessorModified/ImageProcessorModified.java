/* Extension created by Justus, modified by Bextdev
 * 
 * Licensed under Apache License V2
 *
 * No conflicts with the original extension
 *
 */

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
    iconName = "aiwebres/icon.png")
@SimpleObject(external = true)
public class ImageProcessorModified extends AndroidNonvisibleComponent implements Component {

    public static final String VERSION_NAME = "1.1";
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
            
            int maxW = Math.min(bitmapA.getWidth(), bitmapB.getWidth());
            int maxH = Math.min(bitmapA.getHeight(), bitmapB.getHeight());
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
    public void ImageGrey(String imageA) {
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
            saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    @SimpleFunction
    public void ImageInvert(String imageA) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapC = Bitmap.createBitmap(bitmapA.getWidth(), bitmapA.getHeight(), Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < bitmapA.getWidth(); x++) {
                for (int y = 0; y < bitmapA.getHeight(); y++) {
                    int colA = bitmapA.getPixel(x, y);
                    int rC = 255 - Color.red(colA);
                    int gC = 255 - Color.green(colA);
                    int bC = 255 - Color.blue(colA);
                    bitmapC.setPixel(x, y, Color.argb(Color.alpha(colA), rC, gC, bC));
                }
            }
            saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    // New function for Image Blur
    @SimpleFunction
    public void ImageBlur(String imageA) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapC = Bitmap.createBitmap(bitmapA.getWidth(), bitmapA.getHeight(), Bitmap.Config.ARGB_8888);

            // Simple blur kernel (5x5)
            int blurRadius = 5;
            int[] kernel = new int[blurRadius * blurRadius];
            for (int i = 0; i < kernel.length; i++) {
                kernel[i] = 1;
            }

            // Apply blur
            for (int x = blurRadius / 2; x < bitmapA.getWidth() - blurRadius / 2; x++) {
                for (int y = blurRadius / 2; y < bitmapA.getHeight() - blurRadius / 2; y++) {
                    int r = 0, g = 0, b = 0, a = 0;
                    for (int i = -blurRadius / 2; i < blurRadius / 2; i++) {
                        for (int j = -blurRadius / 2; j < blurRadius / 2; j++) {
                            int col = bitmapA.getPixel(x + i, y + j);
                            a += Color.alpha(col);
                            r += Color.red(col);
                            g += Color.green(col);
                            b += Color.blue(col);
                        }
                    }
                    int newColor = Color.argb(a / kernel.length, r / kernel.length, g / kernel.length, b / kernel.length);
                    bitmapC.setPixel(x, y, newColor);
                }
            }
            saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    // New function for Image Sepia
    @SimpleFunction
    public void ImageSepia(String imageA) {
        try {
            Bitmap bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            Bitmap bitmapC = Bitmap.createBitmap(bitmapA.getWidth(), bitmapA.getHeight(), Bitmap.Config.ARGB_8888);

            for (int x = 0; x < bitmapA.getWidth(); x++) {
                for (int y = 0; y < bitmapA.getHeight(); y++) {
                    int colA = bitmapA.getPixel(x, y);

                    int r = Color.red(colA);
                    int g = Color.green(colA);
                    int b = Color.blue(colA);

                    int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                    int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                    int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                    r = Math.min(255, tr);
                    g = Math.min(255, tg);
                    b = Math.min(255, tb);

                    bitmapC.setPixel(x, y, Color.argb(Color.alpha(colA), r, g, b));
                }
            }
            saveAndDispatch(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    private void saveAndDispatch(Bitmap bitmap) {
        File image = new File(Environment.getExternalStorageDirectory(), "/Cimage.png");
        try (FileOutputStream fostream = new FileOutputStream(image)) {
            bitmap.compress(CompressFormat.PNG, 100, fostream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AfterProcess(image.getAbsolutePath());
    }

    @SimpleEvent
    public void AfterProcess(String image) {
        EventDispatcher.dispatchEvent(this, "AfterProcess", image);
    }
}
