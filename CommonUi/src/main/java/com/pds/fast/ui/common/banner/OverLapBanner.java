package com.pds.fast.ui.common.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pds.fast.ui.common.R;
import com.pds.fast.ui.common.banner.entity.BaseBannerInfo;
import com.pds.fast.ui.common.banner.transformers.OverLapPageTransformer;

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

    /**
     * 指示点位置
     */
    private static final int CENTER = 1;

    private AutoSwitchTask mAutoSwitchTask;

    private ViewPager mViewPager;

    // 指示点容器左右内间距
    private int mPointContainerLeftRightPadding;
    // 资源集合
    private List<?> mData;

    // 是否开启自动轮播
    private boolean mIsAutoPlay = true;
    // 自动播放时间
    private int mAutoPlayTime = 5000;

    // viewpager从最后一张到第一张的动画效果
    private int mSlideScrollMode = OVER_SCROLL_ALWAYS;

    // 提示文案数据
    private List<String> mTipData;

    private XBannerAdapter mAdapter;

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

    @LayoutRes
    private int layoutResId = -1;

    private boolean isCanClickSide = true;

    private boolean overlapStyle = false;

    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_XY;

    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    @Deprecated
    public void setAdapter(XBannerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void loadImage(XBannerAdapter mAdapter) {
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
        initCustomAttrs(context, attrs);
    }

    private void init(Context context) {
        mAutoSwitchTask = new AutoSwitchTask(this);
        mClipChildrenLeftMargin = FastBannerUtils.dp2px(context, 30);
        mViewPagerMargin = FastBannerUtils.dp2px(context, 10);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.fastBanner);
        if (typedArray != null) {
            mIsAutoPlay = typedArray.getBoolean(R.styleable.fastBanner_isAutoPlay, true);
            mIsHandLoop = typedArray.getBoolean(R.styleable.fastBanner_isHandLoop, false);
            mAutoPlayTime = typedArray.getInteger(R.styleable.fastBanner_AutoPlayTime, 5000);
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(R.styleable.fastBanner_pointContainerLeftRightPadding, mPointContainerLeftRightPadding);
            mPageChangeDuration = typedArray.getInt(R.styleable.fastBanner_pageChangeDuration, mPageChangeDuration);
            mPlaceholderDrawableResId = typedArray.getResourceId(R.styleable.fastBanner_placeholderDrawable, NO_PLACE_HOLDER);
            mIsClipChildrenMode = typedArray.getBoolean(R.styleable.fastBanner_isClipChildrenMode, false);
            mClipChildrenLeftMargin = typedArray.getDimensionPixelSize(R.styleable.fastBanner_clipChildrenLeftMargin, mClipChildrenLeftMargin);
            mClipChildrenRightMargin = typedArray.getDimensionPixelSize(R.styleable.fastBanner_clipChildrenRightMargin, mClipChildrenRightMargin);
            mClipChildrenTopBottomMargin = typedArray.getDimensionPixelSize(R.styleable.fastBanner_clipChildrenTopBottomMargin, mClipChildrenTopBottomMargin);
            mViewPagerMargin = typedArray.getDimensionPixelSize(R.styleable.fastBanner_viewpagerMargin, mViewPagerMargin);
            mIsClipChildrenModeLessThree = typedArray.getBoolean(R.styleable.fastBanner_isClipChildrenModeLessThree, false);
            mBannerBottomMargin = typedArray.getDimensionPixelSize(R.styleable.fastBanner_bannerBottomMargin, mBannerBottomMargin);
            int scaleTypeIndex = typedArray.getInt(R.styleable.fastBanner_android_scaleType, -1);
            if (scaleTypeIndex >= 0 && scaleTypeIndex < sScaleTypeArray.length) {
                mScaleType = sScaleTypeArray[scaleTypeIndex];
            }
            typedArray.recycle();
        }
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

    public void setBannerData(@NonNull List<? extends BaseBannerInfo> models) {
        setBannerData(R.layout.xbanner_item_image, models);
    }

    public void setBannerData(@LayoutRes int layoutResId, List<? extends BaseBannerInfo> models) {
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
            scrollerField.set(this, new FastBannerScroller(getContext(), duration));
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
        mViewPager = new ViewPager(getContext());
        setScrollDuration(mPageChangeDuration);
        mViewPager.setAdapter(new XBannerPageAdapter());
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOverScrollMode(mSlideScrollMode);
        mViewPager.setPageTransformer(true, new OverLapPageTransformer());
        LayoutParams layoutParams = new LayoutParams(RMP, RMP);
        layoutParams.setMargins(0, 0, 0, mBannerBottomMargin);
        if (mIsClipChildrenMode) {
            setClipChildren(false);
            mViewPager.setClipToPadding(false);
            mViewPager.setOffscreenPageLimit(4);
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
            switchToPoint(0);
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
        TextView mTipTv = new TextView(getContext());
        if (mTipTv != null && mData != null && mData.size() != 0 && mData.get(0) instanceof BaseBannerInfo) {
            if (positionOffset > 0.5) {
                mTipTv.setText(((BaseBannerInfo) mData.get(getRealPosition(position + 1))).getXBannerTitle());
                mTipTv.setAlpha(positionOffset);
            } else {
                mTipTv.setText(((BaseBannerInfo) mData.get(getRealPosition(position))).getXBannerTitle());
                mTipTv.setAlpha(1 - positionOffset);
            }
        } else if (mTipTv != null && mTipData != null && !mTipData.isEmpty()) {
            if (positionOffset > 0.5) {
                mTipTv.setText(mTipData.get(getRealPosition(position + 1)));
                mTipTv.setAlpha(positionOffset);
            } else {
                mTipTv.setText(mTipData.get(getRealPosition(position)));
                mTipTv.setAlpha(1 - positionOffset);
            }
        }

        if (null != mOnPageChangeListener && getRealCount() != 0) {
            mOnPageChangeListener.onPageScrolled(position % getRealCount(), positionOffset, positionOffsetPixels);
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

    private class XBannerPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mIsAutoPlay ? MAX_VALUE : (mIsHandLoop ? MAX_VALUE : getRealCount());
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final View view = LayoutInflater.from(getContext()).inflate(layoutResId, container, false);
            if (getRealCount() > 0) {
                final int realPosition = getRealPosition(position);
                if (mOnItemClickListener != null && !mData.isEmpty()) {
                    view.setOnClickListener(new OnDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if (isCanClickSide) {
                                setBannerCurrentItem(realPosition, true);
                            }
                            mOnItemClickListener.onItemClick(OverLapBanner.this, mData.get(realPosition), v, realPosition);
                        }
                    });
                }
                if (null != mAdapter && !mData.isEmpty()) {
                    mAdapter.loadBanner(OverLapBanner.this, mData.get(realPosition), view, realPosition);
                }
            }
            view.setScaleY(0.6f);
            ViewCompat.setTranslationZ(view, -Float.MAX_VALUE);
            container.addView(view);
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
        TextView mTipTv = new TextView(getContext());
        if (mTipTv != null && mData != null && mData.size() != 0 && mData.get(0) instanceof BaseBannerInfo) {
            mTipTv.setText(((BaseBannerInfo) mData.get(currentPoint)).getXBannerTitle());
        } else if (mTipTv != null && mTipData != null && !mTipData.isEmpty()) {
            // mTipTv.setText(mTipData.get(currentPoint));
            mTipTv.setText("哈哈哈哈哈哈哈哈哈哈");
        }
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

    //设置非自动轮播状态下是否可以循环切换
    public void setHandLoop(boolean handLoop) {
        mIsHandLoop = handLoop;
    }

    // 是否开启一屏多显模式
    public void setIsClipChildrenMode(boolean mIsClipChildrenMode) {
        this.mIsClipChildrenMode = mIsClipChildrenMode;
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

    public interface XBannerAdapter {
        void loadBanner(OverLapBanner banner, Object model, View view, int position);
    }

    public class FastBannerScroller extends Scroller {
        private int mDuration;

        FastBannerScroller(Context context, int duration) {
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
}
