package com.pds.fast.example.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pds.fast.ui.common.banner.FastBannerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OverLapBanner extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWC = LayoutParams.WRAP_CONTENT;

    private static final int NO_PLACE_HOLDER = -1;
    private static final int MAX_VALUE = Integer.MAX_VALUE;
    private float mPageScrollPositionOffset;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnItemClickListener mOnItemClickListener;

    private AutoSwitchTask mAutoSwitchTask;

    private ViewPager mViewPager;

    // 资源集合
    private List<?> mData;
    // 是否开启自动轮播
    private boolean mIsAutoPlay = false;
    // 自动播放时间
    private int mAutoPlayTime = 5000;
    // viewpager从最后一张到第一张的动画效果
    private int mSlideScrollMode = OVER_SCROLL_ALWAYS;
    private BannerAdapter mAdapter;
    /*默认图片切换速度为1s*/
    private int mPageChangeDuration = 1000;
    /*是否是第一次不可见*/
    private boolean mIsFirstInvisible = true;
    /*非自动轮播状态下是否可以循环切换*/
    private boolean mIsHandLoop = false;
    /*轮播框架占位图*/
    private Bitmap mPlaceholderBitmap = null;
    @DrawableRes
    private int mPlaceholderDrawableResId;

    private ImageView mPlaceholderImg;

    /*是否开启一屏显示多个模式*/
    private boolean mIsClipChildrenMode;
    /*一屏显示多个模式左右间距*/
    private int mClipChildrenLeftMargin;
    /*一屏显示多个模式左右间距*/
    private int mClipChildrenRightMargin;
    /*一屏显示多个模式上下间距*/
    private int mClipChildrenTopBottomMargin;
    /*viewpager之间的间距*/
    private int mViewPagerMargin;
    /*少于三张是否支持一屏多显模式*/
    private boolean mIsClipChildrenModeLessThree;

    private int mBannerBottomMargin = 0;
    private int currentPos = 0;
    private int layoutResId = -1;
    private boolean isCanClickSide = true;
    private boolean overlapStyle = true;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_XY;

    BasePageTransformer pageTransformer = new OverLapPageTransformer();

    public void setAdapter(BannerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public OverLapBanner(Context context) {
        this(context, null);
    }

    public OverLapBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverLapBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mAutoSwitchTask = new AutoSwitchTask(this);
        mViewPagerMargin = dp2px(context, 10);
    }


    // 设置图片轮播框架占位图
    private void setBannerPlaceholderDrawable() {
        if (mPlaceholderDrawableResId != NO_PLACE_HOLDER) {
            mPlaceholderBitmap = BitmapFactory.decodeResource(getResources(), mPlaceholderDrawableResId);
        }
        if (mPlaceholderBitmap != null && mPlaceholderImg == null) {
            mPlaceholderImg = new ImageView(getContext());
            mPlaceholderImg.setScaleType(mScaleType);
            mPlaceholderImg.setImageBitmap(mPlaceholderBitmap);
            LayoutParams layoutParams = new LayoutParams(RMP, RMP);
            addView(mPlaceholderImg, layoutParams);
        }
    }

    // 移除图片轮播框架占位图
    private void removeBannerPlaceHolderDrawable() {
        if (mPlaceholderImg != null && this.equals(mPlaceholderImg.getParent())) {
            removeView(mPlaceholderImg);
            mPlaceholderImg = null;
        }
    }

    public void setBannerData(List<?> models) {
        setBannerData(-1, models);
    }

    public void setBannerData(@LayoutRes int layoutResId, List<?> models) {
        if (models == null) models = new ArrayList<>();
        if (models.isEmpty()) {
            mIsAutoPlay = false;
            mIsClipChildrenMode = false;
        }
        if (!mIsClipChildrenModeLessThree && models.size() < 3) {
            mIsClipChildrenMode = false;
        }
        this.layoutResId = layoutResId;
        mData = models;
        initViewPager();
        if (!models.isEmpty()) {
            removeBannerPlaceHolderDrawable();
        } else {
            setBannerPlaceholderDrawable();
        }
    }

    public void setScrollDuration(int duration) {
        try {
            Field scrollerField = mViewPager.getClass().getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, new BannerScroller(getContext(), duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewPager() {
        if (mViewPager != null && this.equals(mViewPager.getParent())) {
            this.removeView(mViewPager);
            mViewPager = null;
        }
        currentPos = 0;
        mViewPager = new WrapViewPager(getContext());
        // setScrollDuration(mPageChangeDuration);
        mViewPager.setAdapter(new XBannerPageAdapter());
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOverScrollMode(mSlideScrollMode);
        mViewPager.setPageTransformer(true, pageTransformer);
        LayoutParams layoutParams = new LayoutParams(RMP, RMP);
        layoutParams.setMargins(0, 0, 0, mBannerBottomMargin);
        if (mIsClipChildrenMode) {
            setClipChildren(false);
            mViewPager.setClipToPadding(false);
            mViewPager.setOffscreenPageLimit(5);
            mViewPager.setClipChildren(false);
            mViewPager.setPadding(mClipChildrenLeftMargin, mClipChildrenTopBottomMargin, mClipChildrenRightMargin, mBannerBottomMargin);
            mViewPager.setPageMargin(this.overlapStyle ? -mViewPagerMargin : mViewPagerMargin);
        }
        addView(mViewPager, 0, layoutParams);
        // 为ViewPager设置一个当前页（为了实现ViewPager的反向滑动），正常情况下ViewPager刚开始不能反向滑(viewpager的数目已经设置为无限大)
        if (mIsAutoPlay && getRealCount() != 0) {
            currentPos = MAX_VALUE / 2 - (MAX_VALUE / 2) % getRealCount();
            mViewPager.setCurrentItem(currentPos);
            startAutoPlay();
        } else {
            if (mIsHandLoop && getRealCount() != 0) {
                currentPos = MAX_VALUE / 2 - (MAX_VALUE / 2) % getRealCount();
                mViewPager.setCurrentItem(currentPos);
            }
            switchToPoint(currentPos);
        }
    }

    public OverLapBanner setClipChildrenRightMargin(int clipChildrenRightMargin) {
        mClipChildrenRightMargin = clipChildrenRightMargin;
        return this;
    }

    public OverLapBanner setClipChildrenLeftMargin(int clipChildrenLeftMargin) {
        mClipChildrenLeftMargin = clipChildrenLeftMargin;
        return this;
    }

    public int getRealCount() {
        return mData == null ? 0 : mData.size();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPageScrollPositionOffset = positionOffset;
        if (mData != null && mData.size() != 0) {
            if (positionOffset > 0.5) {
                switchToPoint(getRealPosition(position + 1));
            } else {
                switchToPoint(getRealPosition(position));
            }
        }

        if (null != mOnPageChangeListener && getRealCount() != 0) {
            mOnPageChangeListener.onPageScrolled(position % getRealCount(), positionOffset, positionOffsetPixels);
        }
    }

    private Object getSafeItemData(int position) {
        if (isSafe(mData, position)) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    private boolean isSafe(List<?> list, int position) {
        if (null == list || list.size() <= position || position <= 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (getRealCount() == 0) return;
        currentPos = getRealPosition(position);
        switchToPoint(currentPos);
        if (mOnPageChangeListener != null) mOnPageChangeListener.onPageSelected(currentPos);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) mOnPageChangeListener.onPageScrollStateChanged(state);
    }

    private View mCurrentView;
    public View getPrimaryItem() {
        return mCurrentView;
    }

    private class XBannerPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mIsAutoPlay ? MAX_VALUE : (mIsHandLoop ? MAX_VALUE : getRealCount());
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentView = (View) object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view;
            if (layoutResId <= 0) {
                view = null == mAdapter ? new ImageView(getContext()) : mAdapter.getBannerItemView(container.getContext());
            } else {
                view = LayoutInflater.from(getContext()).inflate(layoutResId, container, false);
            }

            final int realPosition = getRealPosition(position);
            if (getRealCount() > 0) {
                if (mOnItemClickListener != null && !mData.isEmpty()) {
                    view.setOnClickListener(v -> {
                        if (isCanClickSide) {
                            setBannerCurrentItem(realPosition, true);
                        }
                        mOnItemClickListener.onItemClick(OverLapBanner.this, getSafeItemData(realPosition), v, realPosition);
                    });
                }

                if (null != mAdapter && null != mData && !mData.isEmpty()) {
                    mAdapter.loadBanner(OverLapBanner.this, getSafeItemData(realPosition), view, realPosition);
                }
            }
            Log.d("BasePageTransformer", " position=test111=" + realPosition + " currentPos = " + currentPos + " mData = " + mData.size());
            container.addView(view, 0);

//            if (realPosition != currentPos && mData.size() > 4) {
//                float scale;
//                if (currentPos - 2 < 0) {
//                    int cr = currentPos - realPosition;
//                    if (cr >= 0) {
//                        scale = -0.1f * cr;
//                    } else {
//                        scale = (realPosition - mData.size()) * 0.16f;
//                    }
//                } else if ((currentPos + 2 + 1) > mData.size()) {
//                    int pc = realPosition - currentPos;
//                    if (pc >= 0) {
//                        scale = pc * 0.16f;
//                    } else {
//                        scale = (mData.size() - currentPos) * 0.16f;
//                    }
//                } else {
//                    scale = (realPosition - currentPos) * 0.16f;
//                }
//                if (null != pageTransformer) {
//                    Log.d("BasePageTransformer", " pp=test111=" + scale);
//                    pageTransformer.transformPage(view, scale);
//                }
//            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
        }

    }

    private int getRealPosition(int position) {
        return position % getRealCount();
    }

    private void switchToPoint(int currentPoint) {
        if (null != mAdapter) mAdapter.switchToPoint(getSafeItemData(currentPoint));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mViewPager != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float touchX = ev.getRawX();
                    int paddingLeft = mViewPager.getLeft();
                    if (touchX >= paddingLeft && touchX < FastBannerUtils.getScreenWidth(getContext()) - paddingLeft) {
                        stopAutoPlay();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startAutoPlay();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startAutoPlay() {
        stopAutoPlay();
        if (mIsAutoPlay) {
            postDelayed(mAutoSwitchTask, mAutoPlayTime);
        }
    }

    public void stopAutoPlay() {
        if (mAutoSwitchTask != null) {
            removeCallbacks(mAutoSwitchTask);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void setAutoPlayAble(boolean mAutoPlayAble) {
        this.mIsAutoPlay = mAutoPlayAble;
        stopAutoPlay();
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    // 设置viewpager间距
    public OverLapBanner setViewPagerMargin(@Dimension int viewPagerMargin) {
        this.mViewPagerMargin = viewPagerMargin;
        if (mViewPager != null) {
            mViewPager.setPageMargin(viewPagerMargin);
        }
        return this;
    }

    public OverLapBanner setHandLoop(boolean handLoop) {
        mIsHandLoop = handLoop;
        return this;
    }

    // 是否开启一屏多显模式
    public OverLapBanner setIsClipChildrenMode(boolean mIsClipChildrenMode) {
        this.mIsClipChildrenMode = mIsClipChildrenMode;
        return this;
    }

    //获取当前位置
    public int getBannerCurrentItem() {
        if (mViewPager == null || mData == null || mData.size() == 0) {
            return -1;
        } else {
            return mViewPager.getCurrentItem() % getRealCount();
        }
    }

    // 切换到指定位置
    public void setBannerCurrentItem(int position) {
        setBannerCurrentItem(position, false);
    }

    // 切换到指定位置
    public void setBannerCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager == null || mData == null || position > getRealCount() - 1) {
            return;
        }
        if (mIsAutoPlay || mIsHandLoop) {
            int currentItem = mViewPager.getCurrentItem();
            int realCurrentItem = getRealPosition(currentItem);
            int offset = position - realCurrentItem;
            if (offset < 0) {
                for (int i = -1; i >= offset; i--) {
                    mViewPager.setCurrentItem(currentItem + i, smoothScroll);
                }
            } else if (offset > 0) {
                for (int i = 1; i <= offset; i++) {
                    mViewPager.setCurrentItem(currentItem + i, smoothScroll);
                }
            }
            startAutoPlay();
        } else {
            mViewPager.setCurrentItem(position, smoothScroll);
        }
    }

    // 设置一屏多页模式下是否支持点击侧边切换
    public void setCanClickSide(boolean canClickSide) {
        isCanClickSide = canClickSide;
    }

    // 设置轮播框架占位图
    public void setBannerPlaceholderImg(@DrawableRes int mPlaceholderDrawableResId, ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        this.mPlaceholderDrawableResId = mPlaceholderDrawableResId;
        setBannerPlaceholderDrawable();
    }

    // 设置轮播框架占位图
    public void setBannerPlaceholderImg(Bitmap mPlaceholderBitmap, ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        this.mPlaceholderBitmap = mPlaceholderBitmap;
        setBannerPlaceholderDrawable();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (VISIBLE == visibility) {
            startAutoPlay();
        } else if (GONE == visibility || INVISIBLE == visibility) {
            onInvisibleToUser();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onInvisibleToUser();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    private static class AutoSwitchTask implements Runnable {
        private final WeakReference<OverLapBanner> mXBanner;

        private AutoSwitchTask(OverLapBanner mXBanner) {
            this.mXBanner = new WeakReference<>(mXBanner);
        }

        @Override
        public void run() {
            OverLapBanner banner = mXBanner.get();
            if (banner != null) {
                if (banner.mViewPager != null) {
                    int currentItem = banner.mViewPager.getCurrentItem() + 1;
                    banner.mViewPager.setCurrentItem(currentItem);
                }
                banner.startAutoPlay();
            }
        }
    }

    private void onInvisibleToUser() {
        stopAutoPlay();
        // 处理 RecyclerView 中从对用户不可见变为可见时卡顿的问题
        if (!mIsFirstInvisible && mIsAutoPlay && mViewPager != null && getRealCount() > 0 && mPageScrollPositionOffset != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
        }
        mIsFirstInvisible = false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(OverLapBanner banner, Object model, View view, int position);
    }

    public interface BannerAdapter {
        void loadBanner(OverLapBanner banner, Object model, View view, int position);

        View getBannerItemView(Context context);

        void switchToPoint(Object currentPoint);
    }

    public class BannerScroller extends Scroller {
        private int mDuration;

        BannerScroller(Context context, int duration) {
            super(context);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    public int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
