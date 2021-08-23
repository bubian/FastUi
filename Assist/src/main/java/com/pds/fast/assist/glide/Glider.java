package com.pds.fast.assist.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.pds.fast.assist.Assist;
import com.pds.fast.assist.R;
import com.pds.fast.assist.glide.transform.CircleTransform;
import com.pds.fast.assist.glide.transform.CircleTransformWithBorder;
import com.pds.fast.assist.glide.transform.Corners;
import com.pds.fast.assist.glide.transform.CoverBlur;

public class Glider {
    private static final String TAG = "Glider";
    private static boolean enable;

    public static void loadCommonCover(ImageView imageView, String imgUrl) {
        loadRoundCorner(imageView, imgUrl, Assist.dip2px(imageView.getContext(), 6));
    }

    public static void loadRoundCorner(ImageView imageView, String imgUrl, float cornerRadius) {
        loadRoundCorner(imageView, imgUrl, R.mipmap.ic_launcher, cornerRadius);
    }

    public static void loadRoundCoverCorner(ImageView imageView, String imgUrl, float cornerRadius) {
        loadRoundCorner(imageView, imgUrl, R.mipmap.ic_launcher, cornerRadius);
    }

    public static void loadRoundCornerNotCenterCrop(ImageView imageView, String imgUrl, float cornerRadius) {
        loadRoundCornerNotCenterCrop(imageView, imgUrl, R.color.color_F0F0F0, cornerRadius);
    }

    public static void loadAvatar(ImageView imageView, String imgUrl) {
        loadAvatar(imageView, imgUrl,Assist.getColor(imageView.getContext(),R.color.color_F0F0F0));
    }

    public static void loadAvatar(ImageView imageView, String imgUrl, int color) {
        loadAvatar(imageView, imgUrl, R.mipmap.ic_launcher, color);
    }

    public static void loadAvatar(ImageView imageView, String imgUrl, int defaultResId, int borderColor) {
        loadAvatar(imageView, imgUrl, defaultResId, 0.5f, borderColor);
    }

    public static void loadAvatarNoBorder(ImageView imageView, String imgUrl) {
        loadAvatarNoBorder(imageView, imgUrl, R.mipmap.ic_launcher);
    }

    public static void loadAvatarNoBorder(ImageView imageView, String imgUrl, int defaultId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        RequestBuilder<Drawable> requestBuilder = GlideApp.with(imageView)
                .load(defaultId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new CircleTransform())
                );
        GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(new CircleTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(requestBuilder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadAvatar(ImageView imageView, String imgUrl, int defaultResId, float borderWidth, int borderColor) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        RequestBuilder<Drawable> requestBuilder = GlideApp.with(imageView)
                .load(defaultResId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new CircleTransformWithBorder(borderWidth, borderColor))
                );
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(new CircleTransformWithBorder(borderWidth, borderColor))
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(requestBuilder)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadDefaultAvatar(ImageView imageView, int resId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        int borderColor = ContextCompat.getColor(Assist.application, R.color.color_F0F0F0);
        RequestBuilder<Drawable> requestBuilder = GlideApp.with(imageView)
                .load(R.mipmap.ic_launcher)
                .apply(new RequestOptions().centerCrop()
                        .transform(new CircleTransformWithBorder(0.5f, borderColor))
                );
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(resId)
                .transform(new CircleTransformWithBorder(0.5f, borderColor))
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(requestBuilder)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadAvatarForRecoPage(ImageView imageView, String imgUrl, int borderColor) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .placeholder(R.mipmap.ic_launcher)
                .load(imgUrl)
                .transform(new CircleTransformWithBorder(0.5f, borderColor))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadRoundCorner(ImageView imageView, String imgUrl, int placeHolderResId, float cornerRadius) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(placeHolderResId)
                .error(placeHolderResId)
                .transform(new CenterCrop(), new RoundedCorners((int) cornerRadius))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadRoundCornerNotCenterCrop(ImageView imageView, String imgUrl, int colorResId, float cornerRadius) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }

        GradientDrawable colorDrawable = new GradientDrawable();
        colorDrawable.setColor(ContextCompat.getColor(context, colorResId));
        colorDrawable.setCornerRadius(cornerRadius);
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(colorDrawable)
                .error(colorDrawable)
                .transform(new RoundedCorners((int) cornerRadius))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }


    public static void loadVideoCoverRound(ImageView imageView, ImageView bg, String imgUrl, float cornerRadius) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadCoverBlur(bg, imgUrl, cornerRadius);
                        return false;
                    }
                });

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void downloadImage(Context context, String imgUrl, OnDownloadListener onDownloadListener) {
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideApp.with(context)
                .asBitmap()
                .load(imgUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (onDownloadListener != null) {
                            onDownloadListener.onDownloadSuccess(resource, transition);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public static void load(ImageView imageView, int imgUrl) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadIgnoreEnable(ImageView imageView, int imgUrl) {
        Context context = imageView.getContext();
        if (Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void load(ImageView imageView, int imgUrl, Transformation<Bitmap>... transformations) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(transformations)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void load(ImageView imageView, String imgUrl) {
        load(imageView, imgUrl, 0);
    }

    public static void load(ImageView imageView, String imgUrl, Transformation<Bitmap>... transformations) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(transformations)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void load(ImageView imageView, String imgUrl, int placeHolderResId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(placeHolderResId)
                .error(placeHolderResId)
                .transform(new CenterCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest, imgUrl);
    }

    public static void loadObject(ImageView imageView, Object imgUrl) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        addGifLifecycler(imageView, drawableGlideRequest);
    }

    //方便调试
    private static void addGifLifecycler(ImageView imageView, GlideRequest<Drawable> drawableGlideRequest, String imgUrl) {
        addGifLifecycler(imageView, drawableGlideRequest);
    }

    private static void addGifLifecycler(ImageView imageView, GlideRequest<Drawable> drawableGlideRequest) {
        drawableGlideRequest.into(imageView);
//        GliderViewUtils.bindActivityLifecycle();
    }

    public static void loadNoClip(ImageView imageView, String imgUrl, int placeHolder) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(placeHolder)
                .error(0)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadNoClip(ImageView imageView, String imgUrl) {
        loadNoClip(imageView, imgUrl, 0);
    }

    public static void loadAvatarPedants(ImageView imageView, String imgUrl) {
        Context context = imageView.getContext();
        if (Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .load(imgUrl)
                .skipMemoryCache(true)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void load(ImageView imageView, String imgUrl, Transformation<Bitmap> transformation, int placeHolderResId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(placeHolderResId)
                .error(placeHolderResId)
                .transform(transformation)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadBlurWithFail(ImageView imageView, String imgUrl, int blurRadius, int onFailResId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .skipMemoryCache(false)
                .override(150, 150)
                .placeholder(onFailResId)
                .error(onFailResId)
                .transform(new CoverBlur(blurRadius))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadBlurCover(ImageView imageView, String imgUrl, int blurRadius, int onFailResId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(onFailResId)
                .error(onFailResId)
                .override(150, 150)
                .skipMemoryCache(false)
                .transform(new CoverBlur(blurRadius))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .apply(requestOptions);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadCoverBlur(ImageView imageView, String imgUrl, float cornerRadius) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(new CoverBlur(), new RoundedCorners((int) cornerRadius))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadCoverBlur(ImageView imageView, String imgUrl) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(new CoverBlur())
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }


    public static void loadCoverBlur(ImageView imageView, String imgUrl, int failImg) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .transform(new CoverBlur())
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(GlideApp.with(context)
                        .asDrawable()
                        .load(failImg).transform(new CoverBlur())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadVideoCover(ImageView imageView, ImageView bg, String imgUrl) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadCoverBlur(bg, imgUrl);
                        return false;
                    }
                });

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadBitmap(ImageView imageView, String imgUrl, SimpleTarget<Bitmap> simpleTarget) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideApp.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .transition(BitmapTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(simpleTarget);
    }

    public static void loadRes(ImageView imageView, int resId) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(resId)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade());

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadThumb(ImageView imageView, String url, float sizeMultiplier) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(url)
                .thumbnail(sizeMultiplier)
                .transition(DrawableTransitionOptions.withCrossFade());

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public static void loadFeedGallery(ImageView imageView, String url, boolean leftCorner, boolean rightCorner) {
        Context context = imageView.getContext();
        if (!enable || Assist.assertContextIllegal(context)) {
            return;
        }
        GlideRequest<Drawable> drawableGlideRequest = GlideApp.with(context)
                .asDrawable()
                .load(url)
                .transform(new Corners(Assist.dip2px(imageView.getContext(),6), leftCorner, rightCorner))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        addGifLifecycler(imageView, drawableGlideRequest);
    }

    public interface OnDownloadListener {
        void onDownloadSuccess(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition);
    }

    public static void pause(Context context) {
        if (Assist.assertContextIllegal(context)) {
            return;
        }
        if (!GlideApp.with(context).isPaused()) {
            GlideApp.with(context).pauseRequests();
        }
    }

    public static void resume(Context context) {
        if (Assist.assertContextIllegal(context)) {
            return;
        }
        if (GlideApp.with(context).isPaused()) {
            GlideApp.with(context).resumeRequests();
        }
    }

    public static void enable() {
        enable = true;
    }
}
