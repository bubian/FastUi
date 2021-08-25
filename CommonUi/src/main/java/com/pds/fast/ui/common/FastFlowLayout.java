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

    private int maxRow = 1_000;
    // private Typeface font = ResourcesCompat.getFont(context, R.font.specific);
    private View endTipView;
    private String startData;
    private String endData;

    private int horizontalCap;

    private int verticalCap;

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

    public FastFlowLayout setHorizontalCap(int horizontalCap) {
        this.horizontalCap = horizontalCap;
        return this;
    }

    public FastFlowLayout setVerticalCap(int verticalCap) {
        this.verticalCap = verticalCap;
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
        if (null == tags || tags.size() < 1) {
            return;
        }
        // 添加开头view
        if (null != startData) {
            addFlowView(buildStartFlowView(startData));
        }
        int index = 0;
        // 添加中间内容
        for (String tag : tags) {
            addFlowView(buildTagView(tag, null == endData && (index + 1) == tags.size()));
            index++;
        }
        // 添加结束view
        if (null != endData) {
            addFlowView(endTipView = buildEndFlowView(endData));
        }
    }

    protected TextView buildTagView(String data, boolean isEndView) {
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
        tagView.setLayoutParams(params);
        if (!isAssistText) {
            tagView.setBackgroundColor(Color.BLUE);
            tagView.setPadding(dip2px(4), 0, dip2px(4), 0);
        } else {
            tagView.setBackgroundColor(Color.BLACK);
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
                    if (i != (cCount - 1)){
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
        // 设置子View的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int maxWidth = width - left - top;
        View onLayoutView;
        for (int i = 0; i < cCount; i++) {
            if (isMaxWidth) {
                break;
            }
            View child = getChildAt(i);
            // 判断child的状态
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

            // 如果需要换行
            if (wrap) {
                ++row;
                if (row >= maxRow) {
                    break;
                }
                // 重置我们的行宽和行高
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