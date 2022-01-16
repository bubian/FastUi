package com.pds.fast.ui.common.layoutmanager

import android.graphics.PointF
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.pds.fast.ui.common.assist.intersects
import com.pds.fast.ui.common.assist.isAdjacentTo
import kotlin.math.roundToInt

class SpannedGridLayoutManager(val spans: Int = 1, itemHeight: Int? = 0) : RecyclerView.LayoutManager() {

    enum class Direction { START, END }

    private var layoutStart = 0
    private var layoutEnd = 0
    private var scroll = 0
    private val childFrames = mutableMapOf<Int, Rect>()
    private var pendingScrollToPosition: Int? = null
    var itemOrderIsStable = false
    var spanSizeLookup: SpanSizeLookup? = SpanSizeLookup()
    private val rectHelper: RectHelper = RectHelper(this)
    private var childNum = 0

    init {
        rectHelper.itemHeight = itemHeight ?: 0
    }

    open class SpanSizeLookup(var lookupFunction: ((Int, Int) -> SpanSize)? = null) {
        private var cache = SparseArray<SpanSize>()
        private var usesCache = false

        fun getSpanSize(position: Int, itemWidth: Int): SpanSize =
            if (usesCache) cache[position] ?: getSpanSizeFromFunction(position, itemWidth).also { cache.put(position, it) }
            else getSpanSizeFromFunction(position, itemWidth)

        private fun getSpanSizeFromFunction(position: Int, itemWidth: Int): SpanSize =
            lookupFunction?.invoke(position, itemWidth) ?: getDefaultSpanSize()

        protected open fun getDefaultSpanSize(): SpanSize = SpanSize(1, 1)
        fun invalidateCache(): Unit = cache.clear()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = GridLayoutManager.LayoutParams(-1, -2)

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        if (!(itemCount > 0 && !state.isPreLayout)) return
        childNum = state.itemCount
        rectHelper.clear()
        rectHelper.childNum = childNum

        val rvW = View.MeasureSpec.getSize(widthSpec)
        rectHelper.itemWidth = ((rvW - paddingStart - paddingEnd) * 1f / spans).toInt()

        var spanW = 0
        for (i in 0 until childNum) {
            val spanSize = spanSizeLookup?.getSpanSize(i, rectHelper.itemWidth) ?: SpanSize(1, 1)
            spanW += spanSize.width
            val childRect = rectHelper.findRect(i, spanSize)
            rectHelper.pushRect(i, childRect)
        }
        val freeRect = rectHelper.freeRect
        val spanH = if (freeRect.isNullOrEmpty()) (spanW * 1f / 3).roundToInt() else freeRect[freeRect.size - 1].top

        val realH = spanH * getSizeForOrientation() + paddingTop + paddingBottom

        val hs = View.MeasureSpec.makeMeasureSpec(realH, View.MeasureSpec.EXACTLY);
        val ws = View.MeasureSpec.makeMeasureSpec(rvW, View.MeasureSpec.EXACTLY);
        super.onMeasure(recycler, state, ws, hs)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        childFrames.clear()
        layoutStart = getPaddingStartForOrientation()
        layoutEnd = if (scroll != 0) ((scroll - layoutStart) / getSizeForOrientation()) * getSizeForOrientation() else getPaddingEndForOrientation()

        detachAndScrapAttachedViews(recycler)
        val pendingScrollToPosition = pendingScrollToPosition
        if (itemCount != 0 && pendingScrollToPosition != null && pendingScrollToPosition >= spans) {
            val currentRow = rectHelper.rows.filter { (_, value) -> value.contains(pendingScrollToPosition) }.keys.firstOrNull()
            if (currentRow != null) scroll = getPaddingStartForOrientation() + (currentRow * getSizeForOrientation())
            this.pendingScrollToPosition = null
        }

        // Fill from start to visible end
        fillGap(Direction.END, recycler, state)
        recycleChildrenOutOfBounds(Direction.END, recycler)

        // Check if after changes in layout we aren't out of its bounds
        val overScroll = scroll + height - layoutEnd - getPaddingEndForOrientation()
        val isLastItemInScreen = (0 until childCount).map { getPosition(getChildAt(it)!!) }.contains(itemCount - 1)
        val allItemsInScreen = itemCount == 0 || (getFirstVisiblePosition() == 0 && isLastItemInScreen)
        if (!allItemsInScreen && overScroll > 0) {
            scrollBy(overScroll, state)
            if (overScroll > 0) fillBefore(recycler) else fillAfter(recycler)
        }
    }

    private fun measureChild(position: Int, view: View) {
        val itemWidth = rectHelper.itemWidth
        val itemHeight = getSizeForOrientation()
        val spanSize = spanSizeLookup?.getSpanSize(position, itemWidth) ?: SpanSize(1, 1)

        val usedSpan = spanSize.width
        if (usedSpan > this.spans || usedSpan < 1) spanSize.width = this.spans

        val rect = rectHelper.findRect(position, spanSize)

        // Multiply the rect for item width and height to get positions
        val left = rect.left * itemWidth
        val right = rect.right * itemWidth
        val top = rect.top * itemHeight
        val bottom = rect.bottom * itemHeight

        val insetsRect = Rect()
        calculateItemDecorationsForChild(view, insetsRect)

        // Measure child
        val width = right - left - insetsRect.left - insetsRect.right
        val height = bottom - top - insetsRect.top - insetsRect.bottom
        view.layoutParams.let {
            it.width = width
            it.height = height
        }
        measureChildWithMargins(view, width, height)
        childFrames[position] = Rect(left, top, right, bottom)
    }

    private fun layoutChild(position: Int, view: View) {
        childFrames[position]?.let {
            val scroll = this.scroll

            val startPadding = getPaddingStartForOrientation()
            layoutDecorated(
                view, it.left + paddingLeft, it.top - scroll + startPadding, it.right + paddingLeft, it.bottom - scroll + startPadding
            )
        }
        updateEdgesWithNewChild(view)
    }

    private fun makeAndAddView(position: Int, direction: Direction, recycler: RecyclerView.Recycler) = makeView(position, direction, recycler)
        .also { if (direction == Direction.END) addView(it) else addView(it, 0) }

    private fun makeView(position: Int, direction: Direction, recycler: RecyclerView.Recycler) = recycler.getViewForPosition(position)
        .also {
            measureChild(position, it)
            layoutChild(position, it)
        }

    private fun updateEdgesWithNewChild(view: View) {
        val childStart = getChildStart(view) + scroll + getPaddingStartForOrientation()
        if (childStart < layoutStart) layoutStart = childStart
        val newLayoutEnd = childStart + getSizeForOrientation()
        if (newLayoutEnd > layoutEnd) layoutEnd = newLayoutEnd
    }

    private fun recycleChildrenOutOfBounds(direction: Direction, recycler: RecyclerView.Recycler) {
        if (direction == Direction.END) recycleChildrenFromStart(direction, recycler) else recycleChildrenFromEnd(direction, recycler)
    }

    private fun recycleChildrenFromStart(direction: Direction, recycler: RecyclerView.Recycler) {
        val childCount = childCount
        val start = getPaddingStartForOrientation()
        val toDetach = mutableListOf<View>()
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childEnd = getChildEnd(child)
            if (childEnd < start) toDetach.add(child)
        }
        for (child in toDetach) {
            removeAndRecycleView(child, recycler)
            updateEdgesWithRemovedChild(child, direction)
        }
    }

    private fun recycleChildrenFromEnd(direction: Direction, recycler: RecyclerView.Recycler) {
        val childCount = childCount
        val end = height + getPaddingEndForOrientation()
        val toDetach = mutableListOf<View>()

        for (i in (0 until childCount).reversed()) {
            val child = getChildAt(i) ?: continue
            val childStart = getChildStart(child)
            if (childStart > end) toDetach.add(child)
        }
        for (child in toDetach) {
            removeAndRecycleView(child, recycler)
            updateEdgesWithRemovedChild(child, direction)
        }
    }

    private fun updateEdgesWithRemovedChild(view: View, direction: Direction) {
        val childStart = getChildStart(view) + scroll
        val childEnd = getChildEnd(view) + scroll
        if (direction == Direction.END) layoutStart = getPaddingStartForOrientation() + childEnd
        else if (direction == Direction.START) layoutEnd = getPaddingStartForOrientation() + childStart
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State) = computeScrollOffset()
    override fun computeHorizontalScrollOffset(state: RecyclerView.State) = computeScrollOffset()
    private fun computeScrollOffset() = if (childCount == 0) 0 else getFirstVisiblePosition()
    override fun computeVerticalScrollExtent(state: RecyclerView.State) = childCount
    override fun computeHorizontalScrollExtent(state: RecyclerView.State) = childCount
    override fun computeVerticalScrollRange(state: RecyclerView.State) = state.itemCount
    override fun computeHorizontalScrollRange(state: RecyclerView.State) = state.itemCount
    override fun canScrollVertically() = true
    override fun canScrollHorizontally() = false
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) = scrollBy(dx, recycler, state)
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) = scrollBy(dy, recycler, state)
    private fun scrollBy(delta: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (delta == 0) return 0
        val canScrollBackwards = (getFirstVisiblePosition()) >= 0 && 0 < scroll && delta < 0
        val canScrollForward = (getFirstVisiblePosition() + childCount) <= state.itemCount
                && (scroll + height) < (layoutEnd + getSizeForOrientation() + getPaddingEndForOrientation())
        delta > 0
        if (!(canScrollBackwards || canScrollForward)) return 0
        val correctedDistance = scrollBy(-delta, state)

        val direction = if (delta > 0) Direction.END else Direction.START
        recycleChildrenOutOfBounds(direction, recycler)
        fillGap(direction, recycler, state)
        return -correctedDistance
    }

    private fun scrollBy(distance: Int, state: RecyclerView.State): Int {
        val paddingEndLayout = getPaddingEndForOrientation()

        val start = 0
        val end = layoutEnd + getSizeForOrientation() + paddingEndLayout

        scroll -= distance
        var correctedDistance = distance

        // Correct scroll if was out of bounds at start
        if (scroll < start) {
            correctedDistance += scroll
            scroll = start
        }

        // Correct scroll if it would make the layout scroll out of bounds at the end
        if (scroll + height > end && (getFirstVisiblePosition() + childCount + spans) >= state.itemCount) {
            correctedDistance -= (end - scroll - height)
            scroll = end - height
        }

        offsetChildrenHorizontal(correctedDistance)
        return correctedDistance
    }

    override fun scrollToPosition(position: Int) {
        pendingScrollToPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                if (childCount == 0) return null
                val direction = if (targetPosition < getFirstVisiblePosition()) -1 else 1
                return PointF(0f, direction.toFloat())
            }

            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private fun fillGap(direction: Direction, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (direction == Direction.END) fillAfter(recycler) else fillBefore(recycler)
    }

    private fun fillBefore(recycler: RecyclerView.Recycler) {
        val currentRow = (scroll - getPaddingStartForOrientation()) / getSizeForOrientation()
        val lastRow = (scroll + height - getPaddingStartForOrientation()) / getSizeForOrientation()
        for (row in (currentRow until lastRow).reversed()) {
            val positionsForRow = rectHelper.findPositionsForRow(row).reversed()
            for (position in positionsForRow) {
                if (findViewByPosition(position) != null) continue
                makeAndAddView(position, Direction.START, recycler)
            }
        }
    }

    private fun fillAfter(recycler: RecyclerView.Recycler) {
        val visibleEnd = scroll + height

        val lastAddedRow = layoutEnd / getSizeForOrientation()
        val lastVisibleRow = visibleEnd / getSizeForOrientation()

        for (rowIndex in lastAddedRow..lastVisibleRow) {
            val row = rectHelper.rows[rowIndex] ?: continue

            for (itemIndex in row) {
                if (findViewByPosition(itemIndex) != null) continue
                makeAndAddView(itemIndex, Direction.END, recycler)
            }
        }
    }

    override fun getDecoratedMeasuredWidth(child: View) = childFrames[getPosition(child)]?.width() ?: 0
    override fun getDecoratedMeasuredHeight(child: View) = childFrames[getPosition(child)]?.height() ?: 0

    override fun getDecoratedTop(child: View): Int {
        val position = getPosition(child)
        val decoration = getTopDecorationHeight(child)
        var top = childFrames[position]!!.top + decoration
        top -= scroll
        return top
    }

    override fun getDecoratedRight(child: View): Int {
        val position = getPosition(child)
        val decoration = getLeftDecorationWidth(child) + getRightDecorationWidth(child)
        return childFrames[position]!!.right + decoration
    }

    override fun getDecoratedLeft(child: View): Int {
        val position = getPosition(child)
        val decoration = getLeftDecorationWidth(child)
        return childFrames[position]!!.left + decoration
    }

    override fun getDecoratedBottom(child: View): Int {
        val position = getPosition(child)
        val decoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child)
        var bottom = childFrames[position]!!.bottom + decoration
        bottom -= scroll - getPaddingStartForOrientation()
        return bottom
    }

    private fun getFirstVisiblePosition(): Int {
        val view = getChildAt(0)
        return if (childCount == 0 || null == view) 0 else getPosition(view)
    }

    private fun getPaddingStartForOrientation() = paddingTop
    private fun getSizeForOrientation() = if (rectHelper.itemHeight > 0) rectHelper.itemHeight else rectHelper.itemWidth

    private fun getPaddingEndForOrientation() = paddingBottom
    private fun getChildStart(child: View) = getDecoratedTop(child)
    private fun getChildEnd(child: View) = getDecoratedBottom(child)
    override fun onSaveInstanceState(): Parcelable? = if (itemOrderIsStable && childCount > 0) SavedState(getFirstVisiblePosition()) else null

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as? SavedState
        if (savedState != null) {
            val firstVisibleItem = savedState.firstVisibleItem
            scrollToPosition(firstVisibleItem)
        }
    }

    class SavedState(val firstVisibleItem: Int) : Parcelable {

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source.readInt())
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(firstVisibleItem)
        }

        override fun describeContents() = 0
    }
}

open class RectHelper(private val layoutManager: SpannedGridLayoutManager) {

    private val rectComparator = Comparator<Rect> { rect1, rect2 ->
        if (rect1.top == rect2.top) if (rect1.left < rect2.left) -1 else 1
        else if (rect1.top < rect2.top) -1 else 1
    }

    var childNum = 0
    val rows = mutableMapOf<Int, Set<Int>>()
    private val rectCache = mutableMapOf<Int, Rect>()
    val freeRect = mutableListOf<Rect>()
    var itemWidth: Int = 0
    var itemHeight: Int = 0

    private val vCacheRect = Rect(0, 0, layoutManager.spans, Int.MAX_VALUE)
    private val reuseRect = Rect(0, 0, 0, 0)
    private val possibleNewRect = mutableListOf<Rect>()
    private val adjacentRect = mutableListOf<Rect>()

    fun clear() {
        rows.clear()
        rectCache.clear()
        freeRect.clear()
        resetFreeRect()
    }

    init {
        resetFreeRect()
    }

    private fun resetFreeRect() {
        val initialFreeRect = vCacheRect.apply { set(0, 0, layoutManager.spans, Int.MAX_VALUE) }
        freeRect.add(initialFreeRect)
    }

    fun findRect(position: Int, spanSize: SpanSize) = rectCache[position] ?: findRectForSpanSize(spanSize, position)

    open fun findRectForSpanSize(spanSize: SpanSize, position: Int): Rect {
        val lane = freeRect.firstOrNull {
            reuseRect.set(it.left, it.top, it.left + spanSize.width, it.top + spanSize.height)
            it.contains(reuseRect)
        } ?: return Rect(0, 0, 0 + spanSize.width, 0 + spanSize.height)

        return if (childNum == (position + 1)) Rect(lane.left, lane.top, lane.right, lane.top + spanSize.height)
        else Rect(lane.left, lane.top, lane.left + spanSize.width, lane.top + spanSize.height)
    }

    fun pushRect(position: Int, rect: Rect) {
        val start = rect.top
        rows[rect.top] = (rows[start]?.toMutableSet() ?: mutableSetOf()).apply { add(position) }

        val end = rect.bottom
        val endIndex = end - 1
        rows[endIndex] = (rows[endIndex]?.toMutableSet() ?: mutableSetOf()).apply { add(position) }

        rectCache[position] = rect
        subtract(rect)
    }

    fun findPositionsForRow(rowPosition: Int) = rows[rowPosition] ?: emptySet()

    protected open fun subtract(subtractedRect: Rect) {
        val interestingRect = freeRect.filter { it.isAdjacentTo(subtractedRect) || it.intersects(subtractedRect) }

        possibleNewRect.clear()
        adjacentRect.clear()

        for (free in interestingRect) {
            if (free.isAdjacentTo(subtractedRect) && !subtractedRect.contains(free)) adjacentRect.add(free)
            else {
                freeRect.remove(free)
                if (free.left < subtractedRect.left) possibleNewRect.add(Rect(free.left, free.top, subtractedRect.left, free.bottom))
                if (free.right > subtractedRect.right) possibleNewRect.add(Rect(subtractedRect.right, free.top, free.right, free.bottom))
                if (free.top < subtractedRect.top) possibleNewRect.add(Rect(free.left, free.top, free.right, subtractedRect.top))
                if (free.bottom > subtractedRect.bottom) possibleNewRect.add(Rect(free.left, subtractedRect.bottom, free.right, free.bottom))
            }
        }

        for (rect in possibleNewRect) {
            val isAdjacent = adjacentRect.firstOrNull { it != rect && it.contains(rect) } != null
            if (isAdjacent) continue

            val isContained = possibleNewRect.firstOrNull { it != rect && it.contains(rect) } != null
            if (isContained) continue

            freeRect.add(rect)
        }
        freeRect.sortWith(rectComparator)
    }
}

class SpanSize(var width: Int, var height: Int)