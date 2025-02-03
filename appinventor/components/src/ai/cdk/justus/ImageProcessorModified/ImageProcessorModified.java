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
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.MediaUtil;
import com.google.appinventor.components.runtime.*;

import android.widget.ImageView;

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
    private AndroidViewComponent imageView;

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

    // New property for setting ImageView component
    @SimpleProperty(description = "The ImageView component to update with the processed image.")
    public void ImageView(AndroidViewComponent view) {
        this.imageView = view;
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
            updateImageView(bitmapC);
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
            updateImageView(bitmapC);
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
            updateImageView(bitmapC);
        } catch (IOException e) {
            Log.e("Image", "Unable to load image");
        }
    }

    // Update ImageView directly with the new processed Bitmap
    private void updateImageView(Bitmap bitmap) {
        if (imageView instanceof ImageView) {
            ImageView imgView = (ImageView) imageView.getView();
            imgView.setImageBitmap(bitmap);
        }
    }
}

