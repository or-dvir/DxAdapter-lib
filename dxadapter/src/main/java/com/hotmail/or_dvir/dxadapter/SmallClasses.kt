package com.hotmail.or_dvir.dxadapter

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import kotlin.math.roundToInt

internal enum class DxScrollDirection { UP, DOWN, LEFT, RIGHT }

/**
 * speed sensitivity for all the listeners. The larger the number, the faster the
 * user has to scroll for the listeners to trigger.
 *
 * It's possible to set individual scroll listeners sensitivity using the optional parameters
 */

/**
 * a class containing individual scroll directions listeners (up, down, left, right),
 * with given sensitivities.
 *
 * The higher the sensitivity, the faster the user has to scroll for the listeners to trigger.
 *
 * @param sensitivityAll convenience parameter to set sensitivity for all directions.
 * @param sensitivityUp optional sensitivity for scrolling up
 * @param sensitivityDown optional sensitivity for scrolling down
 * @param sensitivityLeft optional sensitivity for scrolling left
 * @param sensitivityRight optional sensitivity for scrolling right
 */
class DxScrollListener(internal val sensitivityAll: Int,
                       internal val sensitivityUp: Int = sensitivityAll,
                       internal val sensitivityDown: Int = sensitivityAll,
                       internal val sensitivityLeft: Int = sensitivityAll,
                       internal val sensitivityRight: Int = sensitivityAll)
{
    /**
     * a listener to be invoked when the list is scrolled up,
     * if the amount of scroll exceeds [sensitivityUp]
     */
    var onScrollUp: emptyListener? = null
    /**
     * a listener to be invoked when the list is scrolled down,
     * if the amount of scroll exceeds [sensitivityDown]
     */
    var onScrollDown: emptyListener? = null
    /**
     * a listener to be invoked when the list is scrolled left,
     * if the amount of scroll exceeds [sensitivityLeft]
     */
    var onScrollLeft: emptyListener? = null
    /**
     * a listener to be invoked when the list is scrolled right,
     * if the amount of scroll exceeds [sensitivityRight]
     */
    var onScrollRight: emptyListener? = null
}

/**
 * a helper class for drawing the "swiped area" of an item being swiped
 *
 * @param mText the text to show on the swiped area. should be as short as possible
 * @param mTextSizePx the size of the text, in pixels
 * @param mTextColor the color of the text
 * @param mPaddingPx amount of padding between the edge of the item and the text and/or icon, in pixels.
 * note that if you have both text AND an icon, this is also the amount of space between them.
 * @param mBackgroundColor the background color of the swiped area
 * @param mIcon an optional [Drawable] icon to show on the swiped area
 */
class DxSwipeBackground (internal val mText: String,
                         private val mTextSizePx: Int,
                         @ColorInt internal val mTextColor: Int,
                         internal var mPaddingPx: Int,
                         @ColorInt internal val mBackgroundColor: Int?,
                         internal val mIcon: Drawable?)
{
    internal val mIconWidth = mIcon?.intrinsicWidth ?: 0
    internal val mHalfIconHeight = (mIcon?.intrinsicHeight ?: 0) / 2

    //todo make text size half the height of the itemView?????
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