package com.pds.fast.assist.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.pds.fast.assist.glide.apng.APngDrawableEncoder;
import com.pds.fast.assist.glide.apng.ByteBufferAnimationDecoder;
import com.pds.fast.assist.glide.apng.StreamAnimationDecoder;

import java.io.InputStream;
import java.nio.ByteBuffer;

@GlideModule
public class FastAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);

        registry.prepend(APNGDrawable.class, new APngDrawableEncoder());
        //todo 目前引入的三方 apng加载 不能指定使用缓存，不能转换 transform
        ByteBufferAnimationDecoder byteBufferAnimationDecoder = new ByteBufferAnimationDecoder();
        StreamAnimationDecoder streamAnimationDecoder = new StreamAnimationDecoder(byteBufferAnimationDecoder);
        registry.prepend(InputStream.class, APNGDrawable.class, streamAnimationDecoder);
        registry.prepend(ByteBuffer.class, APNGDrawable.class, byteBufferAnimationDecoder);


    }
}
