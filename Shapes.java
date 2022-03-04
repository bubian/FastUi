package com.stones.toolkits.android.shape;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Shapes {
    /**
     * Gradient is linear (default.)
     */
    public static final int LINEAR_GRADIENT = 0;

    /**
     * Gradient is circular.
     */
    public static final int RADIAL_GRADIENT = 1;

    /**
     * Gradient is a sweep.
     */
    public static final int SWEEP_GRADIENT = 2;

    @IntDef({LINEAR_GRADIENT, RADIAL_GRADIENT, SWEEP_GRADIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GradientType {
    }

    /**
     * Shape is a rectangle, possibly with rounded corners
     */
    public static final int RECTANGLE = 0;
    /**
     * Shape is an ellipse
     */
    public static final int OVAL = 1;

    /**
     * Shape is a line
     */
    public static final int LINE = 2;

    /**
     * Shape is a ring.
     */
    public static final int RING = 3;

    @IntDef({RECTANGLE, OVAL, LINE, RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    private Shapes() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static class Builder {
        private final Elements elements;

        public Builder(@Shape int shape) {
            this.elements = new Elements();
            this.elements.shape = shape;
        }

        public Builder setSize(int width, int height) {
            elements.width = width;
            elements.height = height;
            return this;
        }

        public Builder setGradientCenter(int x, int y) {
            elements.centerX = x;
            elements.centerY = y;
            return this;
        }

        public Builder setUseLevel(boolean useLevel) {
            elements.useLevel = useLevel;
            return this;
        }

        public Builder setGradientType(@GradientType int gradientType) {
            elements.gradientType = gradientType;
            return this;
        }

        public Builder setGradientColors(@ColorInt int[] colors) {
            elements.gradientColors = colors;
            return this;
        }

        public Builder setGradientAngle(float angle) {
            elements.gradientAngle = angle;
            return this;
        }

        public Builder setGradientRadius(float radius) {
            elements.gradientRadius = radius;
            return this;
        }

        public Builder setSolid(@ColorInt int solid) {
            elements.solid = solid;
            return this;
        }

        public Builder setStroke(int strokeWidth, @ColorInt int strokeColor, int strokeDashWidth, int strokeDashGap) {
            elements.strokeWidth = strokeWidth;
            elements.strokeColor = strokeColor;
            elements.strokeDashWidth = strokeDashWidth;
            elements.strokeDashGap = strokeDashGap;
            return this;
        }

        public Builder setCornerRadius(float radius) {
            elements.radius = radius;
            return this;
        }

        public Builder setCornerRadii(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
            elements.topLeftRadius = topLeftRadius;
            elements.topRightRadius = topRightRadius;
            elements.bottomRightRadius = bottomRightRadius;
            elements.bottomLeftRadius = bottomLeftRadius;
            return this;
        }

        public Drawable build() {
            if (elements.shape != RECTANGLE && elements.shape != OVAL && elements.shape != LINE && elements.shape != RING) {
                throw new IllegalArgumentException("shape:" + elements.shape + " is illegal");
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(elements.shape);

            //size
            gradientDrawable.setSize(elements.width, elements.height);

            //gradient
            if (elements.gradientColors != null) {
                gradientDrawable.setGradientCenter(elements.centerX, elements.centerY);
                gradientDrawable.setUseLevel(elements.useLevel);
                gradientDrawable.setGradientType(elements.gradientType);
                gradientDrawable.setColors(elements.gradientColors);
                if (elements.gradientType == LINEAR_GRADIENT) {
                    int angle = (int) elements.gradientAngle;
                    angle %= 360;
                    if (angle % 45 != 0) {
                        throw new IllegalArgumentException("gradient angle attribute should to be a multiple of 45");
                    }
                    switch (angle) {
                        case 0:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                            break;
                        case 45:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.BL_TR);
                            break;
                        case 90:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                            break;
                        case 135:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.BR_TL);
                            break;
                        case 180:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
                            break;
                        case 225:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.TR_BL);
                            break;
                        case 270:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                            break;
                        case 315:
                            gradientDrawable.setOrientation(GradientDrawable.Orientation.TL_BR);
                            break;
                    }
                } else {
                    gradientDrawable.setGradientRadius(elements.gradientRadius);
                }
            }

            if (elements.solid != null) {
                gradientDrawable.setColor(elements.solid);
            }

            if (elements.strokeWidth != 0) {
                if (elements.strokeDashWidth != 0.0f) {
                    gradientDrawable.setStroke(elements.strokeWidth, elements.strokeColor, elements.strokeDashWidth, elements.strokeDashGap);
                } else {
                    gradientDrawable.setStroke(elements.strokeWidth, elements.strokeColor);
                }
            }

            if (elements.radius != 0) {
                gradientDrawable.setCornerRadius(elements.radius);
            } else if (elements.topLeftRadius != elements.radius || elements.topRightRadius != elements.radius
                    || elements.bottomLeftRadius != elements.radius || elements.bottomRightRadius != elements.radius) {
                gradientDrawable.setCornerRadii(new float[]{
                        elements.topLeftRadius, elements.topLeftRadius,
                        elements.topRightRadius, elements.topRightRadius,
                        elements.bottomRightRadius, elements.bottomRightRadius,
                        elements.bottomLeftRadius, elements.bottomLeftRadius
                });
            }


            return gradientDrawable;
        }
    }

    private static class Elements {
        @Shape
        private int shape;
        private int width = -1;
        private int height = -1;
        private float centerX = 0.5f;
        private float centerY = 0.5f;
        private boolean useLevel = false;
        @GradientType
        private int gradientType = LINEAR_GRADIENT;
        @ColorInt
        private int[] gradientColors = null;
        private float gradientAngle = 0;
        private float gradientRadius = 0.5f;
        @ColorInt
        private Integer solid = null;
        private int strokeWidth = 0;
        private float strokeDashWidth = 0.0f;
        private float strokeDashGap = 0.0f;
        @ColorInt
        private int strokeColor;
        private float radius = 0.0f;
        private float topLeftRadius = 0.0f;
        private float topRightRadius = 0.0f;
        private float bottomLeftRadius = 0.0f;
        private float bottomRightRadius = 0.0f;
    }
}
