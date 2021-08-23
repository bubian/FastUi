package com.pds.fast.assist.glide.apng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.github.penfeizhou.animation.FrameAnimationDrawable;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.apng.decode.APNGParser;
import com.github.penfeizhou.animation.io.ByteBufferReader;
import com.github.penfeizhou.animation.loader.ByteBufferLoader;
import com.github.penfeizhou.animation.loader.Loader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferAnimationDecoder implements ResourceDecoder<ByteBuffer, APNGDrawable> {

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER) && APNGParser.isAPNG(new ByteBufferReader(source)));
    }

    @Nullable
    @Override
    public Resource<APNGDrawable> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new ByteBufferLoader() {
            @Override
            public ByteBuffer getByteBuffer() {
                source.position(0);
                return source;
            }
        };
        FrameAnimationDrawable drawable;
        if (APNGParser.isAPNG(new ByteBufferReader(source))) {
            drawable = new APNGDrawable(loader);
        } else {
            return null;
        }

        return new DrawableResource<APNGDrawable>((APNGDrawable) drawable) {
            @NonNull
            @Override
            public Class<APNGDrawable> getResourceClass() {
                return APNGDrawable.class;
            }

            @Override
            public int getSize() {
                return source.limit();
            }

            @Override
            public void recycle() {
                ((FrameAnimationDrawable) drawable).stop();
            }
        };
    }
}
