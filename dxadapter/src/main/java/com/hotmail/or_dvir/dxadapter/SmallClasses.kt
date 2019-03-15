package com.hotmail.or_dvir.dxadapter

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.IdRes

//NOTE:
//cannot use interface instead of this class because we need to save the state
//of the mIsSelected variable
abstract class DxItem(internal var mIsSelected: Boolean = false)
{
    //todo think about a way to maybe convert everything (including "mIsSelected"!) to interfaces
    //todo or aliases so that the user does NOT have to inherit from this class

    /**
     * to prevent bugs, it is recommended that you return an @idRes Int here
     */
    abstract fun getViewType(): Int

    //todo when documenting, explain that these can be overridden to change behavior
    open fun isDraggable() = true
    open fun isSelectable() = true
    open fun isSwipeable() = true
}

abstract class DxItemExpandable(mInitialExpandedState: Boolean = false)
    : DxItem()
{
    var mIsExpanded: Boolean = mInitialExpandedState

    /**
     * @return Int the resource id of view (which is part of the list item) that is expandable
     */
    @IdRes
    abstract fun getExpandableViewId(): Int

    /**
     * @return Boolean whether or not clicking the item should trigger expand/collapse.
     * if false, you must trigger expand and collapse yourself using
     * [DxAdapter.expand] and [DxAdapter.collapse].
     */
    abstract fun expandAndCollapseOnItemClick(): Boolean
}

class DxSwipeBackground (val mText: String,
                         val mTextSizePx: Float,
                         @ColorInt val mTextColor: Int,
                         @ColorInt val mBackgroundColor: Int?)
{
    internal val mPaint = Paint().apply {
        textSize = mTextSizePx
        color = mTextColor
    }

    internal var mBackgroundColorDrawable =
        mBackgroundColor?.let { ColorDrawable(it) } ?: ColorDrawable()
}