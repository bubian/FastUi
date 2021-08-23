package com.pds.fast.assist.glide.apng;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.github.penfeizhou.animation.apng.APNGDrawable;

import java.io.File;

public class APngDrawableEncoder implements ResourceEncoder<APNGDrawable> {
    private static final String TAG = "ApngEncoder";

    @NonNull
    @Override
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.SOURCE;
    }

    @Override
    public boolean encode(@NonNull Resource<APNGDrawable> data, @NonNull File file, @NonNull Options options) {
       return false;
    }
}
