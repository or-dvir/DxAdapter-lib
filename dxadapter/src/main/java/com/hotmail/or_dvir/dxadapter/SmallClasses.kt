package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import kotlin.math.roundToInt


internal enum class DxScrollDirection { UP, DOWN, LEFT, RIGHT }

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
 * convenience class to be used with [DxSwipeBackground].
 *
 * note that the provided icon will be scaled according to [desiredHeightPx] while
 * maintaining aspect ratio. it is therefore recommended that [iconRes] will be large
 * rather than small because scaling down is preferable to scaling up
 * (scaling up can reduce image quality).
 *
 * @param iconRes the resources id of the icon to be drawn when swiping an item
 * @param desiredHeightPx the desired height of the icon, in pixels (recommended to use
 * DP from dimens.xml)
 */
class DxSwipeIcon(context: Context,
                  @DrawableRes val iconRes: Int,
                  private val desiredHeightPx: Int)
{
    internal val mIconDrawable: Drawable

    init
    {
        context.apply {
            val bitmap = BitmapFactory.decodeResource(resources, iconRes)
            val ratio = bitmap.width.toFloat() / bitmap.height
            val scaledWidth = (desiredHeightPx * ratio).toInt()

            val bitmapResized = Bitmap.createScaledBitmap(bitmap,
                                                          scaledWidth,
                                                          desiredHeightPx,
                                                          true)

            mIconDrawable = BitmapDrawable(resources, bitmapResized)
        }
    }
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
 * @param mDxIcon an optional icon to show on the swiped area
 */
class DxSwipeBackground (internal val mText: String,
                         private val mTextSizePx: Int,
                         @ColorInt internal val mTextColor: Int,
                         internal var mPaddingPx: Int,
                         @ColorInt internal val mBackgroundColor: Int?,
                         internal val mDxIcon: DxSwipeIcon?)
{

    //todo make text and icon parameters into their own classes???
    // it will make the constructor smaller and i could still use annotations like @ColorInt

    internal val mIconWidth = mDxIcon?.mIconDrawable?.intrinsicWidth ?: 0
    internal val mHalfIconHeight = (mDxIcon?.mIconDrawable?.intrinsicHeight ?: 0) / 2

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
        if(mDxIcon != null)
        {
            if(atLeastOne)
                both = true

            atLeastOne = true
            mTotalWidthToFit += mIconWidth
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