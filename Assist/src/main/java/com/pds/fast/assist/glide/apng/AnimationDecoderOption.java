package com.pds.fast.assist.glide.apng;

import com.bumptech.glide.load.Option;

public final class AnimationDecoderOption {


    public static final Option<Boolean> DISABLE_ANIMATION_APNG_DECODER = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER", false);

    private AnimationDecoderOption() {
    }
}
