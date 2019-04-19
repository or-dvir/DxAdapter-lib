package com.hotmail.or_dvir.dxadapter

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import kotlin.math.roundToInt

////NOTE:
////cannot use interface instead of this class because we need to save the state
////of the mIsSelected variable
//abstract class DxItem(internal var mIsSelected: Boolean = false)
//{
//    //todo think about a way to maybe convert everything (including "mIsSelected"!) to interfaces
//    //todo or aliases so that the user does NOT have to inherit from this class
//
//    /**
//     * to prevent bugs, it is recommended that you return an @idRes Int here
//     */
//    abstract fun getViewType(): Int
//}

//abstract class DxItemExpandable(mInitialExpandedState: Boolean = false)
//    : DxItem()
//{
//    var isExpanded: Boolean = mInitialExpandedState
//
//    /**
//     * @return Int the resource id of view (which is part of the list item) that is expandable
//     */
//    @IdRes
//    abstract fun getExpandableViewId(): Int
//
//    /**
//     * @return Boolean whether or not clicking the item should trigger expand/collapse.
//     * if false, you must trigger expand and collapse yourself using
//     * [DxAdapter.expand] and [DxAdapter.collapse].
//     */
//    abstract fun expandAndCollapseOnItemClick(): Boolean
//}

/**
 * @property sensitivityAll Int speed sensitivity of all the listeners. The larger the number, the faster the
 * user has to scroll for the listeners to trigger.
 *
 * It's possible to set individual scroll listeners sensitivity using the optional parameters
 */
class DxScrollListener(internal val sensitivityAll: Int,
                       internal val sensitivityUp: Int = sensitivityAll,
                       internal val sensitivityDown: Int = sensitivityAll,
                       internal val sensitivityLeft: Int = sensitivityAll,
                       internal val sensitivityRight: Int = sensitivityAll)
{
    var onScrollUp: emptyListener? = null
    var onScrollDown: emptyListener? = null
    var onScrollLeft: emptyListener? = null
    var onScrollRight: emptyListener? = null
}

class DxItemVisibilityListener
{
    var onItemVisible: emptyListener? = null
    var onItemInvisible: emptyListener? = null
}

internal enum class DxScrollDirection
{
    UP,
    DOWN,
    LEFT,
    RIGHT
}

//todo add documentation!!!
class DxSwipeBackground (internal var mText: String,
                         private val mTextSizePx: Int,
                         internal var mPaddingPx: Int,
                         @ColorInt internal val mTextColor: Int,
                         @ColorInt internal val mBackgroundColor: Int?,
                         internal val mIcon: Drawable?)
{
    internal val mIconWidth = mIcon?.intrinsicWidth ?: 0
    internal val mHalfIconHeight = (mIcon?.intrinsicHeight ?: 0) / 2

    //todo make text size half the height of the itemView?!?!?!?!?!?!?!?!!?!?
    internal val mPaint = Paint().apply {
        textAlign = Paint.Align.LEFT
        textSize = mTextSizePx.toFloat()
        color = mTextColor
    }

    internal var mBackgroundColorDrawable =
        mBackgroundColor?.let { ColorDrawable(it) } ?: ColorDrawable()

    internal var mTextWidth = mPaint.measureText(mText.trim()).roundToInt()

    private var mTotalWidthToFit = 0

    init
    {
        var atLeastOne = false
        var both = false

        //adding text width
        if(mText.isNotBlank())
        {
            atLeastOne = true
            mTotalWidthToFit += mTextWidth
        }

        //adding icon width
        if(mIcon != null)
        {
            if(atLeastOne)
                both = true

            atLeastOne = true
            mTotalWidthToFit += mIcon.intrinsicWidth
        }

        //adding padding on left AND right,
        //or set to 0 so calculations using this variable will not be affected.
        if(atLeastOne)
            mTotalWidthToFit += (2 * mPaddingPx)
        //no text and no icon
        else
            mPaddingPx = 0

        //one more padding between icon and text
        if(both)
            mTotalWidthToFit += mPaddingPx
    }

    internal fun doesBackgroundFitInSwipeArea() =
        mBackgroundColorDrawable.bounds.width() >= mTotalWidthToFit
}