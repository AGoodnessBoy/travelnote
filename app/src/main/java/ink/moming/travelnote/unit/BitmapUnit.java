package ink.moming.travelnote.unit;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import ink.moming.travelnote.R;

/**
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 */

public class BitmapUnit {
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 根据Resources压缩图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options);
        return src;
    }

    /**
     * 根据地址压缩图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return src;
    }

    public static String bitmap2String(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        boolean b = bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        if (!b){
            return null;
        }

        byte[] bs = stream.toByteArray();

        String s = Base64.encodeToString(bs,Base64.DEFAULT);

        return s;
    }


    public static Bitmap string2Bitmap(String s){
        Bitmap bitmap = null;
        try {
            byte[] bytes;
            bytes = Base64.decode(s,Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }

}
