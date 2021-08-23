package com.pds.fast.assist.glide.apng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.apng.decode.APNGParser;
import com.github.penfeizhou.animation.io.StreamReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamAnimationDecoder implements ResourceDecoder<InputStream, APNGDrawable> {

    private final ResourceDecoder<ByteBuffer, APNGDrawable> byteBufferDecoder;

    public StreamAnimationDecoder(ResourceDecoder<ByteBuffer, APNGDrawable> byteBufferDecoder) {
        this.byteBufferDecoder = byteBufferDecoder;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) {
        return (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER) && APNGParser.isAPNG(new StreamReader(source)));
    }

    @Nullable
    @Override
    public Resource<APNGDrawable> decode(@NonNull final InputStream source, int width, int height, @NonNull Options options) throws IOException {
        byte[] data = inputStreamToBytes(source);
        if (data == null) {
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        return byteBufferDecoder.decode(byteBuffer, width, height, options);
    }


    private static byte[] inputStreamToBytes(InputStream is) {
        final int bufferSize = 16384;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufferSize);
        try {
            int nRead;
            byte[] data = new byte[bufferSize];
            while ((nRead = is.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            return null;
        }
        return buffer.toByteArray();
    }
}
