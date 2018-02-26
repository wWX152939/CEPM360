package com.pm360.cepm360.app.module.change;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageUtils {

    public static int reckonThumbnail(int oldWidth, int oldHeight,
            int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int scale = (int) (oldWidth / (float) newWidth);
            if (scale <= 1)
                scale = 1;
            return scale;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int scale = (int) (oldHeight / (float) newHeight);
            if (scale <= 1)
                scale = 1;
            return scale;
        }
        return 1;
    }

    public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }

    public static String savaPhotoToLocal(Bitmap btp, String path) {
        File tempFile = new File(path);
        String filePath = tempFile.getAbsolutePath();
        try {
            FileOutputStream fileOut = new FileOutputStream(tempFile);
            btp.compress(CompressFormat.JPEG, 100, fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;
    }
    
    @SuppressWarnings("deprecation")
    public static String UriToPath(Activity activity, Uri uri) {
        String path = "";
        String[] imgs = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, imgs, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        path = cursor.getString(index);
        if(Integer.parseInt(Build.VERSION.SDK) < 14){  
            cursor.close();  
        }       
        return path;
    }
}
