package com.pds.fast.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.util.List;

public class OverlapAvatarView extends ViewGroup {

    public final static int MODE_MIDDLE = 1;
    public final static int MODE_START = 2;
    public final static int MODE_END = 3;

    private final float mSpace = 0.8422f;
    private final LayoutInflater mInflater;
    private int mMaxChildCount = 5;
    private int mCurrentOffset;
    private OverlapAvatarListener listener;
    private IOverlapAvatar overlapAvatar;

    private int mode = MODE_MIDDLE;
    private int addIndex = 1;

    private AnimatorSet animatorSet;

    public OverlapAvatarView(Context context) {
        this(context, null);
    }

    public OverlapAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlapAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heiMeasure = MeasureSpec.getSize(heightMeasureSpec);
        int heiMode = MeasureSpec.getMode(heightMeasureSpec);
        int widMode = MeasureSpec.getMode(widthMeasureSpec);
        int widMeasure = MeasureSpec.getSize(widthMeasureSpec);

        int totalWidth = 0;
        int totalHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // 测量子View的宽和高,系统提供的measureChild
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth();
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight();
            if (i < mMaxChildCount) {
                if (i == 0) {
                    totalWidth = totalWidth + childWidth;
                } else {
                    totalWidth = (int) (totalWidth + childWidth * mSpace);
                }
            } else {
                break;
            }
            totalHeight = Math.max(totalHeight, childHeight);
        }
        //如果是exactly使用测量宽和高，否则使用自己设置的宽和高
        setMeasuredDimension((widMode == MeasureSpec.EXACTLY) ? widMeasure : totalWidth, (heiMode == MeasureSpec.EXACTLY) ? heiMeasure : totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mode == MODE_START) {
            onModeStart(changed, l, t, r, b);
        } else if (mode == MODE_MIDDLE && getChildCount() <= mMaxChildCount) {
            onModeMiddle(changed, l, t, r, b);
        } else {
            onModeEnd(changed, l, t, r, b);
        }
    }

    private void onModeMiddle(boolean changed, int l, int t, int r, int b) {
        int middle = getMeasuredWidth() / 2;
        int childCount = getChildCount();
        int remainder = childCount / 2;
        boolean isEven = childCount % 2 == 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int childWidthHalf = childWidth / 2;
            int cl;
            float cap = (childWidth * (1 - mSpace));
            int pp = (int) (childWidth * mSpace);
            if (childCount == 1) {
                cl = middle - childWidthHalf;
            } else if (childCount == 2) {
                if (0 == i) {
                    cl = (int) (middle + cap / 2f - childWidth) + mCurrentOffset;
                } else {
                    cl = (int) (middle - cap / 2f - mCurrentOffset);
                }
            } else {
                int realOffset = i > addIndex ? -mCurrentOffset : mCurrentOffset;
                if (isEven) {
                    if (i >= remainder) {
                        cl = (int) (middle - cap / 2f + pp * (i - remainder));
                    } else {
                        cl = (int) (middle + cap / 2f - pp * (remainder - i - 1)) - childWidth;
                    }
                } else {
                    if (i == remainder) {
                        cl = middle - childWidthHalf;
                    } else if (i > remainder) {
                        cl = (int) (middle + (pp - cap) / 2f + pp * (i - remainder - 1));
                    } else {
                        cl = (int) (middle - (pp - cap) / 2f - pp * (remainder - i - 1) - childWidth);
                    }
                }
                cl = cl + realOffset;
            }
            child.layout(cl, 0, cl + childWidth, childHeight);
        }
    }

    private void onModeStart(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int realOffset = i >= addIndex ? 0 : -mCurrentOffset;
            int left = (int) (childWidth * mSpace * i) + realOffset;
            child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
        }
    }

    private void onModeEnd(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int realOffset = i > addIndex ? -mCurrentOffset : 0;
            int left = (int) (childWidth * mSpace * i) + realOffset;
            child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setData(List<Object> list) {
        if (list == null) return;
        removeAllViews();
        int size = list.size();
        mCurrentOffset = 0;
        for (int i = 0; i < size; i++) {
            if (i >= mMaxChildCount) break;
            View itemView = buildItemView(mInflater);
            if (null == itemView) continue;
            bindData(itemView, list.get(i));
            ViewCompat.setTranslationZ(itemView, -i);
            this.addView(itemView);
        }
    }

    public void addData(Object ava) {
        addData(ava, null);
    }

    public void addData(Object data, OverlapAvatarListener listener) {
        if (null != animatorSet && animatorSet.isRunning()) return;
        this.listener = listener;

        if (mMaxChildCount <= 0 || null == data) return;
        View itemView = buildItemView(mInflater);
        if (null == itemView) return;
        if (null != overlapAvatar) overlapAvatar.addData(itemView, data);
    }

    public void addAvatarView(View itemView, int addIndex) {
        // 可以自己添加方法传入，但是要保证索引在数组范围内
        this.addIndex = addIndex;
        int childNum = getChildCount();
        if (mode == MODE_START) {
            if (addIndex < childNum) {
                addIndex++;
            } else {
                addIndex = childNum;
            }
        }
        ViewCompat.setTranslationZ(itemView, -addIndex);
        this.addView(itemView, addIndex);
        entryAnimator(itemView);
    }

    private View buildItemView(LayoutInflater inflater) {
        if (null != overlapAvatar) {
            return overlapAvatar.buildItemView(inflater);
        } else {
            return null;
        }
    }

    private void entryAnimator(View addView) {
        if (null != animatorSet) animatorSet.cancel();
        animatorSet = new AnimatorSet();
        int cAt = addIndex == 0 ? 1 : 0;
        float ra = (mode == MODE_MIDDLE && getChildCount() <= mMaxChildCount) ? mSpace * 0.5f : mSpace;
        if (getChildCount() >= 2) {
            mCurrentOffset = (int) (getChildAt(cAt).getMeasuredWidth() * ra);
        }

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.6f, 1f);
        scaleAnimator.setDuration(800);
        scaleAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            value = Math.min(value, 1.2f);
            addView.setScaleX(value);
            addView.setScaleY(value);
            if (value > 1f) {
                ViewCompat.setTranslationZ(addView, 1);
            } else if (value <= 1.01) {
                for (int i = 0; i < getChildCount(); i++) {
                    ViewCompat.setTranslationZ(getChildAt(i), -i);
                }
            }
        });

        ValueAnimator translationAnimator = ValueAnimator.ofInt(mCurrentOffset, 0);
        translationAnimator.setDuration(200);
        View endView = getChildAt(getChildCount() - 1);
        translationAnimator.addUpdateListener(animation -> {
            mCurrentOffset = (int) animation.getAnimatedValue();
            float fraction = animation.getAnimatedFraction();
            if (getChildCount() > mMaxChildCount) {
                endView.setAlpha(1 - fraction);
            }
            requestLayout();
        });

        if (mCurrentOffset <= 10 || getChildCount() < 2) {
            animatorSet.play(scaleAnimator);
        } else {
            animatorSet.playSequentially(scaleAnimator, translationAnimator);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (listener != null) {
                    listener.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentOffset = 0;
                if (getChildCount() > mMaxChildCount) {
                    removeViewAt(getChildCount() - 1);
                }
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }
        });
        animatorSet.start();
    }

    private void bindData(View view, Object data) {
        if (null != overlapAvatar && null != data) overlapAvatar.bindData(view, data);
    }

    public void setMaxChildCount(int count) {
        this.mMaxChildCount = count;
        int childCount = getChildCount();
        if (childCount > mMaxChildCount) {
            for (int i = 0; i < childCount - mMaxChildCount; i++) {
                removeViewAt(getChildCount() - 1);
            }
        }
    }

    public void setOverlapAvatar(IOverlapAvatar overlapAvatar) {
        this.overlapAvatar = overlapAvatar;
    }

    public interface OverlapAvatarListener {
        void onAnimationStart();

        void onAnimationEnd();
    }

    public interface IOverlapAvatar {
        View buildItemView(LayoutInflater inflater);

        void bindData(@NonNull View view, @NonNull Object data);

        void addData(@NonNull View view, @NonNull Object data);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != animatorSet) animatorSet.cancel();
    }
}