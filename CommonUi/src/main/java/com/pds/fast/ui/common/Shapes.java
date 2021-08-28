package com.pds.fast.ui.common;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

import androidx.annotation.ColorInt;

/**
 * GradientDrawable
 */
public class Shapes {
    public static final int LINEAR_GRADIENT = 0;
    public static final int RADIAL_GRADIENT = 1;
    public static final int SWEEP_GRADIENT = 2;
    public static final int RECTANGLE = 0;
    public static final int OVAL = 1;
    public static final int LINE = 2;
    public static final int RING = 3;

    private Shapes() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static class Config {
        private int shapeType = RECTANGLE;
        private int width;
        private int height;
        private float gradientCenterX;
        private float gradientCenterY;
        private boolean useLevel;
        private int gradient = LINEAR_GRADIENT;
        @ColorInt
        private int[] colors;
        private float angle;
        private float gradientRadius;
        @ColorInt
        private Integer argb;
        private int strokeWidth;
        private float dashWidth;
        private float dashGap;
        @ColorInt
        private int orientation;
        private float radius;
        private float topLeftRadius;
        private float topRightRadius;
        private float bottomLeftRadius;
        private float bottomRightRadius;
    }

    public static class Builder {

        private final Shapes.Config config = new Shapes.Config();

        public Builder() {
        }

        public Builder(int shapeType) {
            config.shapeType = shapeType;
        }

        public Shapes.Builder setSize(int width, int height) {
            config.width = width;
            config.height = height;
            return this;
        }

        public Shapes.Builder setGradientCenter(int gradientCenterX, int gradientCenterY) {
            config.gradientCenterX = (float) gradientCenterX;
            config.gradientCenterY = (float) gradientCenterY;
            return this;
        }

        public Shapes.Builder setUseLevel(boolean useLevel) {
            config.useLevel = useLevel;
            return this;
        }

        public Shapes.Builder setGradientType(int gradient) {
            config.gradient = gradient;
            return this;
        }

        public Shapes.Builder setGradientColors(@ColorInt int[] colors) {
            config.colors = colors;
            return this;
        }

        public Shapes.Builder setGradientAngle(float angle) {
            config.angle = angle;
            return this;
        }

        public Shapes.Builder setGradientRadius(float gradientRadius) {
            config.gradientRadius = gradientRadius;
            return this;
        }

        public Shapes.Builder setSolid(@ColorInt int argb) {
            config.argb = argb;
            return this;
        }

        public Shapes.Builder setStroke(int strokeWidth, @ColorInt int orientation, int dashWidth, int dashGap) {
            config.strokeWidth = strokeWidth;
            config.orientation = orientation;
            config.dashWidth = (float) dashWidth;
            config.dashGap = (float) dashGap;
            return this;
        }

        public Shapes.Builder setCornerRadius(float radius) {
            config.radius = radius;
            return this;
        }

        public Shapes.Builder setCornerRadii(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
            config.topLeftRadius = topLeftRadius;
            config.topRightRadius = topRightRadius;
            config.bottomRightRadius = bottomRightRadius;
            config.bottomLeftRadius = bottomLeftRadius;
            return this;
        }

        public Drawable build() {
            if (config.shapeType != RECTANGLE && config.shapeType != OVAL && config.shapeType != LINE && config.shapeType != RING) {
                throw new IllegalArgumentException("shape:" + config.shapeType + " is illegal");
            } else {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(config.shapeType);
                gradientDrawable.setSize(config.width, config.height);
                int orientation;
                if (config.colors != null) {
                    gradientDrawable.setGradientCenter(config.gradientCenterX, config.gradientCenterY);
                    gradientDrawable.setUseLevel(config.useLevel);
                    gradientDrawable.setGradientType(config.gradient);
                    gradientDrawable.setColors(config.colors);
                    if (config.gradient == LINEAR_GRADIENT) {
                        if ((orientation = (int) config.angle % 360) % 45 != 0) {
                            throw new IllegalArgumentException("gradient angle attribute should to be a multiple of 45");
                        }

                        switch (orientation) {
                            case 0:
                                gradientDrawable.setOrientation(Orientation.LEFT_RIGHT);
                                break;
                            case 45:
                                gradientDrawable.setOrientation(Orientation.BL_TR);
                                break;
                            case 90:
                                gradientDrawable.setOrientation(Orientation.BOTTOM_TOP);
                                break;
                            case 135:
                                gradientDrawable.setOrientation(Orientation.BR_TL);
                                break;
                            case 180:
                                gradientDrawable.setOrientation(Orientation.RIGHT_LEFT);
                                break;
                            case 225:
                                gradientDrawable.setOrientation(Orientation.TR_BL);
                                break;
                            case 270:
                                gradientDrawable.setOrientation(Orientation.TOP_BOTTOM);
                                break;
                            case 315:
                                gradientDrawable.setOrientation(Orientation.TL_BR);
                        }
                    } else {
                        gradientDrawable.setGradientRadius(config.gradientRadius);
                    }
                }

                if (config.argb != null) {
                    gradientDrawable.setColor(config.argb);
                }

                if (config.strokeWidth != 0) {
                    if (config.dashWidth != 0.0F) {
                        int strokeWidth = config.strokeWidth;
                        orientation = config.orientation;
                        float dashWidth = config.dashWidth;
                        float dashGap = config.dashGap;
                        gradientDrawable.setStroke(strokeWidth, orientation, dashWidth, dashGap);
                    } else {
                        gradientDrawable.setStroke(config.strokeWidth, config.orientation);
                    }
                }

                if (this.config.radius != 0.0F) {
                    gradientDrawable.setCornerRadius(config.radius);
                } else if (config.topLeftRadius != config.radius
                        || config.topRightRadius != config.radius
                        || config.bottomLeftRadius != config.radius
                        || config.bottomRightRadius != config.radius) {

                    float[] radii = new float[8];
                    radii[0] = config.topLeftRadius;
                    radii[1] = config.topLeftRadius;
                    radii[2] = config.topRightRadius;
                    radii[3] = config.topRightRadius;
                    radii[4] = config.bottomRightRadius;
                    radii[5] = config.bottomRightRadius;
                    radii[6] = config.bottomLeftRadius;
                    radii[7] = config.bottomLeftRadius;
                    gradientDrawable.setCornerRadii(radii);
                }
                return gradientDrawable;
            }
        }
    }
}
