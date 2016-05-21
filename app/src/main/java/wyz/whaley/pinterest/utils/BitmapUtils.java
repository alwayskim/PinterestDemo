package wyz.whaley.pinterest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by alwayking on 16/3/13.
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    public static Bitmap createColorBitmap(Context context, int width, int height, int color) {
        int[] pix = new int[width * height];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                pix[index] = color;
            }
        Bitmap bitmap = Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        return bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int width) {
        int originHeight = bitmap.getHeight();
        int originWidth = bitmap.getWidth();
        Log.i(TAG, "resize width = " + width + " origin width= " + originWidth);

        float ratio = (float) width / originWidth;

        Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, (int) (ratio * originWidth), (int) (ratio * originHeight), true);
        return resizeBitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap decodeBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
