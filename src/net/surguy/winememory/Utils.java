package net.surguy.winememory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Inigo Surguy
 * @todo Write some documentation!
 */
public class Utils {

    // http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
    // decodes image and scales it to reduce memory consumption
    static Bitmap bitmapFromFile(File f, int imageSize){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > imageSize || o.outWidth > imageSize) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(imageSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File was unexpectedly deleted : " + f + " with " + e, e);
        } catch (IOException e) {
            throw new IllegalStateException("File was unexpectedly deleted : " + f + " with " + e, e);
        }
    }

}
