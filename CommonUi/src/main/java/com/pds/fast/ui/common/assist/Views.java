package com.pds.fast.ui.common.assist;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;

public class Views {

    public static void removeParent(View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void roundCorner(@NonNull View view, float dpRadius) {
        view.setOutlineProvider(new RoundCornerOutlineProvider(dip2px(dpRadius)));
        view.setClipToOutline(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void roundCornerPx(@NonNull View view, float pxRadius) {
        view.setOutlineProvider(new RoundCornerOutlineProvider(pxRadius));
        view.setClipToOutline(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class RoundCornerOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public RoundCornerOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            int leftMargin = 0;
            int topMargin = 0;
            Rect selfRect = new Rect(leftMargin, topMargin,
                    rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
            outline.setRoundRect(selfRect, mRadius);
        }
    }

    //获取手机本地图片的宽度和高度
    public static Pair<Integer, Integer> getImageWidthHeight(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                width = options.outHeight;
                height = options.outWidth;
                break;
        }
        return new Pair<>(width, height);
    }


    public static int dip2px(float var0) {
        return (int)(var0 * Resources.getSystem().getDisplayMetrics().density + 0.5F);
    }
}
