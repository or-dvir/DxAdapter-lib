package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

internal enum class DxScrollDirection
{
    UP,
    DOWN,
    LEFT,
    RIGHT
}

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
class DxSwipeIcon(val context: Context,
                  @DrawableRes val iconRes: Int,
                  private val desiredHeightPx: Int)
{
    internal val mIconDrawable: Drawable

    init
    {
        context.apply {
            val bitmap = getBitmap(iconRes)
            val ratio = bitmap.width.toFloat() / bitmap.height
            val scaledWidth = (desiredHeightPx * ratio).toInt()

            val bitmapResized = Bitmap.createScaledBitmap(bitmap,
                                                          scaledWidth,
                                                          desiredHeightPx,
                                                          true)

            mIconDrawable = BitmapDrawable(resources, bitmapResized)
        }
    }

    private fun getBitmap(drawableId: Int): Bitmap
    {
        ContextCompat.getDrawable(context, drawableId)?.apply {
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)

            return bitmap
        }

        throw NullPointerException("could not create bitmap (bitmap was null)")

//        val drawable = ContextCompat.getDrawable(context, drawableId)
//        val bitmap =
//            Bitmap.createBitmap(drawable?.intrinsicWidth,
//                                drawable?.intrinsicHeight,
//                                Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        drawable?.setBounds(0, 0, canvas.width, canvas.height)
//        drawable?.draw(canvas)
//        return bitmap
    }
}

/**
 * convenience class to be used with [DxSwipeBackground].
 *
 * note for [desiredSizePx]: it is recommended to use DP (and not SP) from dimens.xml
 * so it will look similar on different screens.
 * use DP and not SP because it is assumed that your list items are sized
 * using DP and therefore the text size should be calculated by the same scale.
 *
 * if you use SP for text, then the text size setting on the users' device
 * could ruin the proportions of your list items and the text size.
 *
 * @param text the text to show when swiping an item
 * @param desiredSizePx the desired size of the text, in pixels (see note in class description)
 * @param textColor the color of the text
 */
class DxSwipeText(internal val text: String,
                  private val desiredSizePx: Float,
                  @ColorInt internal val textColor: Int)
{
    internal val mPaint = Paint().apply {
        //todo check if this alignment works for RTL languages
        textAlign = Paint.Align.LEFT
        textSize = desiredSizePx
        color = textColor
    }
}

/**
 * a helper class for drawing the "swiped area" of an item being swiped
 *
 * @param mText the text to show on the swiped area. should be as short as possible
 * @param mTextSizePx the size of the text, in pixels
 * @param mTextColor the color of the text
 * @param paddingPx amount of padding between the edge of the item and the text and/or icon, in pixels.
 * note that if you have both text AND an icon, this is also the amount of space between them.
 * @param backgroundColor the background color of the swiped area
 * @param dxIcon an optional icon to show on the swiped area
 */
class DxSwipeBackground(/*internal val mText: String,
                         private val mTextSizePx: Int,
                         @ColorInt internal val mTextColor: Int,*/
    internal var paddingPx: Int,
    @ColorInt internal val backgroundColor: Int?,
    internal val dxText: DxSwipeText?,
    internal val dxIcon: DxSwipeIcon?)
{
    internal val mIconWidthPx = dxIcon?.mIconDrawable?.intrinsicWidth ?: 0
    internal val mTextWidthPx = dxText?.let { it.mPaint.measureText(it.text.trim()).toInt() } ?: 0
    internal val mHalfIconHeight = (dxIcon?.mIconDrawable?.intrinsicHeight ?: 0) / 2

//    internal val mPaint = Paint().apply {
//        textAlign = Paint.Align.LEFT
//        textSize = mTextSizePx.toFloat()
//        color = mTextColor
//    }

    internal var mBackgroundColorDrawable =
        backgroundColor?.let { ColorDrawable(it) } ?: ColorDrawable()

    private var mTotalWidthToFitPx = mTextWidthPx + mIconWidthPx

    init
    {
        //if at least one of item/text, add padding on both sides
        if (dxText != null || dxIcon != null)
            mTotalWidthToFitPx += (2 * paddingPx)
        //if none of text/icon, set paddingPx to 0 so calculations
        //using this variable will not be affected
        else
            paddingPx = 0

        //if both icon and text, add padding between them
        if (dxText != null && dxIcon != null)
            mTotalWidthToFitPx += paddingPx
    }

    internal fun doesBackgroundFitInSwipeArea() =
        mBackgroundColorDrawable.bounds.width() >= mTotalWidthToFitPx
}