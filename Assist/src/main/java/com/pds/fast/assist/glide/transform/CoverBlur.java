package com.pds.fast.assist.glide.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.pds.fast.assist.Assist;
import com.pds.fast.assist.glide.helper.BlurBitmapUtil;

import java.security.MessageDigest;

public class CoverBlur extends BitmapTransformation {
    private static final String TAG = "CoverBlur";
    private static int BLUR_RADIUS = 25;

    private int mRadius;

    public CoverBlur() {
        this(BLUR_RADIUS);
    }

    public CoverBlur(int radius) {
        super();
        mRadius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = createBlurBitmap(pool, outWidth, outHeight, toTransform);
        BitmapResource.obtain(bitmap, pool);
        return bitmap;
    }

    public Bitmap createBlurBitmap(BitmapPool bitmapPool, int outWidth, int outHeight, Bitmap source) {
        Bitmap result;
        Bitmap bpBlurBody = BlurBitmapUtil.blurBitmap(Assist.application, source, mRadius);
        bpBlurBody = TransformationUtils.centerCrop(bitmapPool, bpBlurBody, outWidth, outHeight);
        result = bitmapPool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        Rect from = new Rect(0, 0, bpBlurBody.getWidth(), bpBlurBody.getHeight());
        Rect to = new Rect(0, 0, outWidth, outHeight);
        canvas.drawBitmap(bpBlurBody, from, to, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
