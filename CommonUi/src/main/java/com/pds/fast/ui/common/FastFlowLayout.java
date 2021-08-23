package com.pds.fast.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastFlowLayout extends ViewGroup {

    private int maxRow = 1;
    private List<View> lineViews = new ArrayList<>();
    private final List<List<View>> mAllViews = new ArrayList<>();
    private final List<Integer> mLineHeight = new ArrayList<>();
    // private Typeface font = ResourcesCompat.getFont(context, R.font.specific);
    private View endTipView;
    private String startData;
    private String endData;

    private int middleMargin;

    public FastFlowLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FastFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FastFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    public FastFlowLayout setMaxRow(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    public FastFlowLayout setMiddleMargin(int middleMargin) {
        this.middleMargin = middleMargin;
        return this;
    }

    public FastFlowLayout setStartText(String startData) {
        this.startData = startData;
        return this;
    }

    public FastFlowLayout setEndText(String endText) {
        this.endData = endText;
        return this;
    }

    public void setData(String tags, String split) {
        String[] likeTags = tags.split(split);
        setData(new ArrayList<>(Arrays.asList(likeTags)));
    }


    public void setData(List<String> tags) {
        if (null == tags) {
            return;
        }
        // 添加开头view
        if (null != startData) {
            addFlowView(buildStartFlowView(startData));
        }
        // 添加中间内容
        for (String tag : tags) {
            addFlowView(buildTagView(tag));
        }
        // 添加结束view
        if (null != endData) {
            addFlowView(endTipView = buildEndFlowView(endData));
        }
    }

    protected TextView buildTagView(String data) {
        return buildDefaultTagView(data, false, false);
    }

    protected View buildStartFlowView(String startData) {
        return buildDefaultTagView(startData, false, true);
    }

    protected View buildEndFlowView(String startEnd) {
        return buildDefaultTagView(startEnd, true, true);
    }

    private TextView buildDefaultTagView(String text, boolean isEndView, boolean isAssistText) {
        Context context = getContext();
        TextView tagView = new TextView(context);
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, dip2px(17));
        if (!isEndView) {
            params.setMarginEnd(middleMargin);
        }
        tagView.setLayoutParams(params);
        if (!isAssistText) {
            tagView.setBackgroundColor(Color.BLUE);
            tagView.setPadding(dip2px(4), 0, dip2px(4), 0);
        }
        tagView.setGravity(Gravity.CENTER_VERTICAL);
        tagView.setTextColor(context.getResources().getColor(R.color.color_a6a6a6));
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tagView.setText(text);
        return tagView;
    }


    private void addFlowView(View view) {
        if (null != view) {
            addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int row = 0;

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;

        // 记录每一行的宽度与高度
        int lineWidth = 0;
        int lineHeight = 0;

        // 得到内部元素的个数
        int cCount = getChildCount();
        int endTipViewWidthWidth = 0;
        int endTipViewWidthHeight = 0;
        if (null != endTipView) {
            measureChild(endTipView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams endViewLp = (MarginLayoutParams) endTipView.getLayoutParams();
            endTipViewWidthWidth = endTipView.getMeasuredWidth() + endViewLp.leftMargin + endViewLp.rightMargin;
            endTipViewWidthHeight = endTipView.getMeasuredHeight() + endViewLp.topMargin + endViewLp.bottomMargin;
        }

        for (int i = 0; i < cCount; i++) {
            // 通过索引拿到每一个子view
            View child = getChildAt(i);
            // 测量子View的宽和高,系统提供的measureChild
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到LayoutParams
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            int maxWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
            if (child == endTipView) {
                continue;
            }
            // 换行 判断 当前的宽度大于 开辟新行
            if (lineWidth + childWidth > maxWidth) {
                row++;
                if (row >= maxRow) {
                    break;
                }
                // 对比得到最大的宽度
                width = Math.max(width, lineWidth);
                // 重置lineWidth
                lineWidth = childWidth;
                // 记录行高
                height += lineHeight;
                lineHeight = childHeight;
            } else { // 未换行
                if ((endTipViewWidthWidth + childWidth + lineWidth <= maxWidth) || null != endTipView || (row + 1) < maxRow) {
                    // 叠加行宽
                    lineWidth += childWidth;
                    // 得到当前行最大的高度
                    lineHeight = Math.max(lineHeight, childHeight);
                } else {
                    // 叠加行宽
                    lineWidth += endTipViewWidthWidth;
                    // 得到当前行最大的高度
                    lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                }
            }
        }
        // 特殊情况,最后一个控件
        width = Math.max(lineWidth, width);
        height += lineHeight;

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        lineViews.clear();
        // 当前ViewGroup的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        int row = 0;
        boolean hasEndTipView = null != endTipView;
        int endTipViewWidthWidth = 0;
        int endTipViewWidthHeight = 0;
        if (hasEndTipView) {
            MarginLayoutParams endViewLp = (MarginLayoutParams) endTipView.getLayoutParams();
            endTipViewWidthWidth = endTipView.getMeasuredWidth() + endViewLp.leftMargin + endViewLp.rightMargin;
            endTipViewWidthHeight = endTipView.getMeasuredHeight() + endViewLp.topMargin + endViewLp.bottomMargin;
        }
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (i != (cCount - 1) && child == endTipView) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int maxWidth = width - getPaddingLeft() - getPaddingRight();
            boolean wrap = childWidth + lineWidth + lp.leftMargin + lp.rightMargin > maxWidth;
            boolean wrapTwo = (endTipViewWidthWidth + lineWidth > maxWidth) && hasEndTipView;
            // 如果需要换行
            if (wrap || wrapTwo) {
                ++row;
                if (row >= maxRow) {
                    break;
                }
                // 记录LineHeight
                mLineHeight.add(lineHeight);
                // 记录当前行的Views
                mAllViews.add(lineViews);
                // 重置我们的行宽和行高
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                // 重置我们的View集合
                lineViews.clear();
            }

            int realChildWidth = childWidth + lp.leftMargin + lp.rightMargin;
            int realChildHeight = childHeight + lp.topMargin + lp.bottomMargin;

            if ((realChildWidth + endTipViewWidthWidth + lineWidth <= maxWidth) || !hasEndTipView || (row + 1) < maxRow) {
                lineWidth += realChildWidth;
                lineHeight = Math.max(lineHeight, realChildHeight);
                lineViews.add(child);
            } else {
                if (lineViews.contains(endTipView)) {
                    continue;
                }
                lineWidth += endTipViewWidthWidth;
                lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                lineViews.add(endTipView);
            }
        }// for end
        // 处理最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        // 设置子View的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 行数
        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            // 当前行的所有的View
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                // 判断child的状态
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                // 为子View进行布局
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }


    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public int dip2px(double dpValue) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }
}