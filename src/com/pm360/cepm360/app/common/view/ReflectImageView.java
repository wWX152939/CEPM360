
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ReflectImageView extends ImageView {

    private Bitmap originalBitmap;

    public ReflectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectImageView(Context context) {
        this(context, null, 0);
    }

    public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        DoReflection(((BitmapDrawable) getDrawable()).getBitmap());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        DoReflection(bm);

    }

    public void setImageBitmap(Bitmap bm, boolean isFlected) {
        if (isFlected) {
            super.setImageBitmap(bm);
        }
    }

    @Override
    public void setImageResource(int resId) {

        originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
        DoReflection(originalBitmap);
    }

    private void DoReflection(Bitmap bitmap) {
        final int reflectionGap = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, false);
        Bitmap bitmap4Reflection = Bitmap.createBitmap(width,
                (height + height), Config.ARGB_8888);
        Canvas canvasRef = new Canvas(bitmap4Reflection);
        Paint deafaultPaint = new Paint();
        deafaultPaint.setAntiAlias(true);
        canvasRef.drawBitmap(bitmap, 0, 0, null);
        canvasRef.drawRect(0, height, width, height + reflectionGap,
                deafaultPaint);
        canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmap4Reflection.getHeight() + reflectionGap, 0x10ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvasRef.drawRect(0, height, width, bitmap4Reflection.getHeight()
                + reflectionGap, paint);
        this.setImageBitmap(bitmap4Reflection, true);
    }

}
