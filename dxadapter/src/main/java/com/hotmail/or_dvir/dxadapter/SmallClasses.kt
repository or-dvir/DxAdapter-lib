package com.hotmail.or_dvir.dxadapter

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import kotlin.math.roundToInt

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

//todo add documentation!!!
class DxSwipeBackground (var mText: String,
                         val mTextSizePx: Int,
                         val mPaddingPx: Int,
                         @ColorInt val mTextColor: Int,
                         @ColorInt val mBackgroundColor: Int?,
                         val mIcon: Drawable?)
{
    internal val mHalfIconHeight = mIcon?.let { it.intrinsicHeight / 2 }

    internal val mPaint = Paint().apply {
        textSize = mTextSizePx.toFloat()
        color = mTextColor
    }

    internal var mBackgroundColorDrawable =
        mBackgroundColor?.let { ColorDrawable(it) } ?: ColorDrawable()

    internal var mTotalWidthToFit: Int

    internal var mTextWidth = mPaint.measureText(mText).roundToInt()

    init
    {
        //padding both left and right
        mTotalWidthToFit = mTextWidth + (2 * mPaddingPx)

        mIcon?.apply {
            //add padding between icon and text
            mTotalWidthToFit += mPaddingPx + intrinsicWidth
        }
    }

    internal fun reverseTextAlign()
    {
        mPaint.textAlign = mPaint.textAlign.let {
            when (it)
            {
                Paint.Align.RIGHT -> Paint.Align.LEFT
                Paint.Align.LEFT -> Paint.Align.RIGHT
                else -> it
            }
        }
    }

    internal fun doesBackgroundFitInSwipeArea() =
        mBackgroundColorDrawable.bounds.width() >= mTotalWidthToFit
}