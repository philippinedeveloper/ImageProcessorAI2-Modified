/* Extension created by Justus, modified by Bextdev
 * 
 * Licensed under Apache License V2
 *
 * No conflicts with the original extension
 *
 */

package ai.cdk.justus.ImageProcessorModified;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.util.*;
import com.google.appinventor.components.runtime.*;

import android.widget.ImageView;

@DesignerComponent(versionName = ImageProcessorModified.VERSION_NAME,
    version = 3,
    description = "A component for complex image processing in MIT App Inventor.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "aiwebres/icon.png")
@SimpleObject(external = true)
public class ImageProcessorModified extends AndroidNonvisibleComponent implements Component {

    public static final String VERSION_NAME = "1.2";
    public static final float DEFAULT_WEIGHT = 0.5f;
    private ComponentContainer container;
    private float weight = DEFAULT_WEIGHT;
    private ImageView i;

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

    @SimpleProperty(description = "The Image component to update with the processed image.")
    public void Image(AndroidViewComponent image) {
        this.i = (ImageView) image.getView();
    }

    @SimpleFunction
    public void ImageCombine(@Asset String imageA, @Asset String imageB) {
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
            updateImageView(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    @SimpleFunction
public void ImageGrey(@Asset String image) {
    try {
        Bitmap bitmap = MediaUtil.getBitmapDrawable(container.$form(), image).getBitmap();
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int col = bitmap.getPixel(x, y);
                int avg = (Color.red(col) + Color.green(col) + Color.blue(col)) / 3;
                bitmap2.setPixel(x, y, Color.argb(Color.alpha(col), avg, avg, avg));
            }
        }
        updateImageView(bitmap2);
    } catch (IOException e) {
        Log.e("Image", "Unable to load image");
    }
}

    @SimpleFunction
public void ImageInvert(@Asset String image) {
    try {
        Bitmap bitmap = MediaUtil.getBitmapDrawable(container.$form(), image).getBitmap();
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int col = bitmap.getPixel(x, y);
                int r = 255 - Color.red(col);
                int g = 255 - Color.green(col);
                int b = 255 - Color.blue(col);
                bitmap2.setPixel(x, y, Color.argb(Color.alpha(col), r, g, b));
            }
        }
        updateImageView(bitmap2);
    } catch (IOException e) {
        Log.e("Image", "Unable to load image");
    }
}

        @SimpleFunction
    public void ImageBlur(@Asset String image) {
        try {
            Bitmap bitmap = MediaUtil.getBitmapDrawable(container.$form(), image).getBitmap();
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            
            int radius = 2; // Blur radius
            for (int x = 0; x < bitmap.getWidth(); x++) {
                for (int y = 0; y < bitmap.getHeight(); y++) {
                    int rTotal = 0, gTotal = 0, bTotal = 0, aTotal = 0, count = 0;
                    
                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dy = -radius; dy <= radius; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < bitmap.getWidth() && ny >= 0 && ny < bitmap.getHeight()) {
                                int col = bitmap.getPixel(nx, ny);
                                aTotal += Color.alpha(col);
                                rTotal += Color.red(col);
                                gTotal += Color.green(col);
                                bTotal += Color.blue(col);
                                count++;
                            }
                        }
                    }
                    bitmap2.setPixel(x, y, Color.argb(aTotal / count, rTotal / count, gTotal / count, bTotal / count));
                }
            }
            updateImageView(bitmap2);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    @SimpleFunction
    public void ImageSepia(@Asset String image) {
        try {
            Bitmap bitmap = MediaUtil.getBitmapDrawable(container.$form(), image).getBitmap();
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < bitmap.getWidth(); x++) {
                for (int y = 0; y < bitmap.getHeight(); y++) {
                    int col = bitmap.getPixel(x, y);
                    int r = Color.red(col);
                    int g = Color.green(col);
                    int b = Color.blue(col);
                    
                    int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                    int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                    int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                    
                    tr = Math.min(255, tr);
                    tg = Math.min(255, tg);
                    tb = Math.min(255, tb);
                    
                    bitmap2.setPixel(x, y, Color.argb(Color.alpha(col), tr, tg, tb));
                }
            }
            updateImageView(bitmap2);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    private void updateImageView(Bitmap bitmap) {
            i.setImageBitmap(bitmap);
    }
}

