package com.pds.fast.assist.glide.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.pds.fast.assist.Assist;
import com.pds.fast.assist.glide.helper.BlurBitmapUtil;
import com.pds.fast.assist.glide.helper.FastBlur;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

public class AcapellaBlur extends BitmapTransformation {
    private static final String TAG = "AcapellaBlur";
    private static final int DP_BOTTOM = 60;
    private static final int DP_SHOW_BOTTOM = 30;
    public static final double TRANSPARENT_RATE = 0.4;
    private static int BLUR_RADIUS = 25;
    private static int DP_ROUND_CORNER = 16;

    private int mRadius;
    private int mRoundCorner;

    public AcapellaBlur() {
        this(BLUR_RADIUS, DP_ROUND_CORNER);
    }

    public AcapellaBlur(int radius, float dpRoundCorner) {
        super();
        mRadius = radius;
        mRoundCorner = Assist.dip2px(Assist.application, dpRoundCorner);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        long time = System.currentTimeMillis();
        Bitmap bitmap = createBlurBitmap(pool, outWidth, outHeight, toTransform);
        Log.i(TAG, "transform: " + (System.currentTimeMillis() - time));
        BitmapResource.obtain(bitmap, pool);
        return bitmap;
    }


    public Bitmap createBlurBitmap(BitmapPool bitmapPool, int outWidth, int outHeight, Bitmap source) {
        Bitmap result;
        Bitmap bpSmall = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * 0.1), (int) (source.getHeight() * 0.1), false);
        Bitmap bpLowQuality = compressBitmap(bpSmall);
        Bitmap bpCenterCrop = TransformationUtils.centerCrop(bitmapPool, bpLowQuality, outWidth, outHeight);
        Bitmap bpBottom = Bitmap.createBitmap(bpCenterCrop, mRoundCorner, outHeight - Assist.dip2px(Assist.application, DP_BOTTOM), outWidth - 2 * mRoundCorner, Assist.dip2px(Assist.application, DP_BOTTOM));
        bpBottom = TransformationUtils.roundedCorners(bitmapPool, bpBottom, mRoundCorner);
        bpBottom = addWhiteEdge(bitmapPool, bpBottom); // 底部加白边再做高斯模糊
        Bitmap bpBlurBottom = blur(bpBottom, mRadius * 2);
        bpBlurBottom = TransformationUtils.roundedCorners(bitmapPool, bpBlurBottom, mRoundCorner);

        Bitmap bpBlurBody = BlurBitmapUtil.blurBitmap(Assist.application, source, mRadius);
        bpBlurBody = TransformationUtils.centerCrop(bitmapPool, bpBlurBody, outWidth, outHeight);
        bpBlurBody = TransformationUtils.roundedCorners(bitmapPool, bpBlurBody, mRoundCorner);

        result = bitmapPool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        Rect from = new Rect(0, 0, bpBlurBody.getWidth(), bpBlurBody.getHeight());
        Rect to = new Rect(0, 0, outWidth, outHeight - Assist.dip2px(Assist.application, DP_SHOW_BOTTOM));
        Rect bottomFrom = new Rect(0, 0, bpBlurBottom.getWidth(), bpBlurBottom.getHeight());
        Rect bottomTo = new Rect(0, outHeight - Assist.dip2px(Assist.application, DP_BOTTOM), outWidth, outHeight);
        paint.setDither(true);
        paint.setAlpha((int) (255 * TRANSPARENT_RATE));
        canvas.drawBitmap(bpBlurBottom, bottomFrom, bottomTo, paint);
        paint.setAlpha(255);
        canvas.drawBitmap(bpBlurBody, from, to, paint);
        return result;
    }

    private Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
        return newBitmap;
    }

    private Bitmap addWhiteEdge(BitmapPool pool, Bitmap source) {
        Bitmap bitmap = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int edgeWidth = Assist.dip2px(Assist.application, 18);
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        Rect from = new Rect(0, 0, source.getWidth(), source.getHeight());
        Rect to = new Rect(edgeWidth, edgeWidth, source.getWidth() - edgeWidth, source.getHeight() - edgeWidth);
        canvas.drawColor(Color.parseColor("#F3F3F3"));
        canvas.drawBitmap(source, from, to, paint);
        return bitmap;
    }

    private Bitmap blur(Bitmap source, int radius) {
        return FastBlur.blur(source, radius, true);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
