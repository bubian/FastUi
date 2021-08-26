package com.pds.fast.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastFlowLayout extends ViewGroup {

    private int maxRow = 1_000;
    // private Typeface font = ResourcesCompat.getFont(context, R.font.specific);
    private View endTipView;
    private Object startData;
    private Object endData;

    private int horizontalCap;
    private int verticalCap;
    public static final int FLOW = 0;
    public static final int START = 1;
    public static final int END = 2;

    private IFlow flow;
    private int layoutId;

    public FastFlowLayout(Context context) {
        super(context);
    }

    public FastFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FastFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FastFlowLayout setFastFlow(IFlow flow) {
        this.flow = flow;
        return this;
    }

    public FastFlowLayout setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public FastFlowLayout setMaxRow(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    public FastFlowLayout setHorizontalCap(int horizontalCap) {
        this.horizontalCap = horizontalCap;
        return this;
    }

    public FastFlowLayout setVerticalCap(int verticalCap) {
        this.verticalCap = verticalCap;
        return this;
    }

    public FastFlowLayout setStartText(Object startData) {
        this.startData = startData;
        return this;
    }

    public FastFlowLayout setEndText(Object endText) {
        this.endData = endText;
        return this;
    }

    public void setData(String data, String split) {
        String[] likeTags = data.split(split);
        setData(new ArrayList<>(Arrays.asList(likeTags)));
    }

    public void setData(List<Object> list) {
        if (null == list || list.size() < 1) {
            return;
        }
        removeAllViews();
        // 添加开头view
        if (null != startData) {
            addFlowView(buildStartFlowView(startData));
        }
        int index = 0;
        // 添加中间内容
        for (Object item : list) {
            addFlowView(buildFlowView(item));
            index++;
        }
        // 添加结束view
        if (null != endData) {
            addFlowView(endTipView = buildEndFlowView(endData));
        }
    }

    protected View buildFlowView(Object data) {
        return buildDefaultFlowView(data, FLOW);
    }

    protected View buildStartFlowView(Object startData) {
        return buildDefaultFlowView(startData, START);
    }

    protected View buildEndFlowView(Object startEnd) {
        return buildDefaultFlowView(startEnd, END);
    }

    protected View buildDefaultFlowView(Object data, int tipType) {
        if (layoutId > 0){
            return LayoutInflater.from(getContext()).inflate(layoutId,this,false);
        }
        return null != flow ? flow.buildFlowView(getContext(), data, tipType) : null;
    }

    private void addFlowView(View view) {
        if (null != view) {
            addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxRow <= 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int row = 0;

        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        // 测量尾部view
        int endTipViewWidthWidth = 0;
        int endTipViewWidthHeight = 0;
        if (null != endTipView) {
            measureChild(endTipView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams endViewLp = (MarginLayoutParams) endTipView.getLayoutParams();
            endTipViewWidthWidth = endTipView.getMeasuredWidth() + endViewLp.leftMargin + endViewLp.rightMargin;
            endTipViewWidthHeight = endTipView.getMeasuredHeight() + endViewLp.topMargin + endViewLp.bottomMargin;
        }

        // 控件最大宽度
        int maxWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        boolean isMaxWidth = false;
        for (int i = 0; i < cCount; i++) {
            if (isMaxWidth) {
                break;
            }
            // 通过索引拿到每一个子view
            View child = getChildAt(i);
            // 判断child的状态
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            boolean hasEndTipView = null != endTipView;
            if (child == endTipView && i != (cCount - 1)) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            boolean wrapOne = lineWidth + childWidth > maxWidth;
            // 是否是最后一行（这里的逻辑不能错）
            boolean isLastRow = wrapOne && (row + 1) >= maxRow;
            boolean wrapTwo = (lineWidth + endTipViewWidthWidth) > maxWidth && hasEndTipView;
            boolean wrap = !hasEndTipView && wrapOne || !isLastRow && wrapOne || wrapOne && wrapTwo;
            if (wrap) {
                lineWidth -= horizontalCap;
                // 换行
                row++;
                if (row >= maxRow) {
                    break;
                }
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                lineHeight += verticalCap;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                if (null == endTipView || (row + 1) < maxRow || (endTipViewWidthWidth + childWidth + lineWidth + horizontalCap) <= maxWidth) {
                    // 由于前面有可能把endTipView过滤掉，这里做容错处理
                    if (i == (cCount - 1) && hasEndTipView && child != endTipView) {
                        lineWidth += endTipViewWidthWidth;
                        lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                    } else {
                        lineWidth += childWidth;
                        lineHeight = Math.max(lineHeight, childHeight);
                    }
                    if (i != (cCount - 1)) {
                        lineWidth += horizontalCap;
                    }
                } else {
                    // 说明已经到达最后一行，强制结束循环
                    isMaxWidth = true;
                    lineWidth += endTipViewWidthWidth;
                    lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                }
            }
        }
        width = Math.max(lineWidth, width);
        height += lineHeight;
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (maxRow <= 0) {
            return;
        }
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
        boolean isMaxWidth = false;
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int maxWidth = width - left - top;
        View onLayoutView;
        for (int i = 0; i < cCount; i++) {
            if (isMaxWidth) {
                break;
            }
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            onLayoutView = child;
            if (i != (cCount - 1) && child == endTipView) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int realChildWidth = childWidth + lp.leftMargin + lp.rightMargin;

            boolean wrapOne = lineWidth + realChildWidth > maxWidth;

            // 是否是最后一行（这里的逻辑不能错）
            boolean isLastRow = wrapOne && (row + 1) >= maxRow;
            boolean wrapTwo = isLastRow && (endTipViewWidthWidth + lineWidth > maxWidth) && hasEndTipView;
            boolean wrap = !hasEndTipView && wrapOne || !isLastRow && wrapOne || wrapOne && wrapTwo;

            if (wrap) {
                // 换行
                ++row;
                if (row >= maxRow) {
                    break;
                }
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin + verticalCap;

                left = getPaddingLeft();
                top += lineHeight;
            }

            int realChildHeight = childHeight + lp.topMargin + lp.bottomMargin;
            if (!hasEndTipView || (row + 1) < maxRow || (realChildWidth + endTipViewWidthWidth + lineWidth + horizontalCap) <= maxWidth) {
                // 由于前面有可能把endTipView过滤掉，这里做容错处理
                if (i == (cCount - 1) && hasEndTipView && child != endTipView) {
                    lineWidth += endTipViewWidthWidth;
                    lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                    onLayoutView = endTipView;
                    isMaxWidth = true;
                } else {
                    lineWidth += realChildWidth;
                    lineHeight = Math.max(lineHeight, realChildHeight);
                }

            } else {
                isMaxWidth = true;
                lineWidth += endTipViewWidthWidth;
                lineHeight = Math.max(lineHeight, endTipViewWidthHeight);
                onLayoutView = endTipView;
            }

            MarginLayoutParams onLayoutViewLp = (MarginLayoutParams) onLayoutView.getLayoutParams();
            int lc = left + onLayoutViewLp.leftMargin;
            int tc = top + onLayoutViewLp.topMargin;
            int rc = lc + onLayoutView.getMeasuredWidth();
            int bc = tc + onLayoutView.getMeasuredHeight();
            onLayoutView.layout(lc, tc, rc, bc);
            left += onLayoutView.getMeasuredWidth() + onLayoutViewLp.leftMargin + onLayoutViewLp.rightMargin;

            lineWidth += horizontalCap;
            left += horizontalCap;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public interface IFlow {
        View buildFlowView(Context context, Object data, int tipType);
    }
}