package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSwipeable
import java.util.*
import kotlin.math.roundToInt

/**
 * a class that handles all dragging and swiping behaviour.
 * @param ITEM the item type of your adapter
 * @param mAdapter your adapter
 */
open class DxItemTouchCallback<ITEM: IItemBase>(private val mAdapter: DxAdapter<ITEM, *>)
    : ItemTouchHelper.Callback()
{
    //todo add support for different types of layout managers (grid/staggered/horizontal)

    //todo make everything OPEN so it can be overridden
    private val mTextRect = Rect()
    private var mDoesBackFit = false
    private var mIconTop = 0
    private var mIconBottom = 0
    private var mIconLeft = 0
    private var mIconRight = 0
    private var mTextX = 0f
    private var mTextY = 0f
    private var mIsSwipingLeft = false
    private var mSwipeBackgroundForDrawing: DxSwipeBackground? = null
    private var onItemSwiped: Pair<Int, onItemSwipedListener<ITEM>>? = null

    /**
     * the background when swiping left.
     *
     * note that if you override [getSwipeBackgroundLeft], its' returned value
     * is used and this variable is ignored
     */
    open var swipeBackgroundLeft: DxSwipeBackground? = null
    /**
     * the background when swiping right.
     *
     * note that if you override [getSwipeBackgroundRight], its' returned value
     * is used and this variable is ignored
     */
    open var swipeBackgroundRight: DxSwipeBackground? = null
    /**
     * default value: FALSE
     *
     * if TRUE, long-clicking an item will initiate drag-and-drop.
     *
     * note that if you enable this feature and long-click is also used to select items,
     * (for example with [IAdapterSelectable.defaultItemSelectionBehavior]) then long-click might not
     * produce the intended result (selecting an item when actually meant to drag and vice-versa)
     */
    open var dragOnLongClick = false
    /**
     * set a background color to highlight a dragged item
     */
    @field:ColorInt
    open var dragBackgroundColor: Int? = null
    /**
     * see [ItemTouchHelper.Callback.getSwipeThreshold] for details
     */
    open var swipeThreshold: Float? = null
    /**
     * see [ItemTouchHelper.Callback.getSwipeEscapeVelocity] for more details.
     *
     * this value is overridden by [swipeEscapeVelocityMultiplier] (if set).
     */
    open var swipeEscapeVelocity: Float? = null
    /**
     * sets a value for the swipe escape velocity as a multiplier
     * of the device's default value.
     *
     * this value overrides [swipeEscapeVelocity].
     *
     *  see [ItemTouchHelper.Callback.getSwipeEscapeVelocity] for more details.
     */
    open var swipeEscapeVelocityMultiplier: Float? = null
    /**
     * a listener to be invoked just before the items are moved
     */
    open var onItemMove: onItemsMoveListener<ITEM>? = null
    /**
     * this function enables the swipeable functionality.
     * note that your items must also implement [IItemSwipeable].
     *
     * @param swipeDirections the direction of allowed swiping. one or more of:
     * [LEFT][ItemTouchHelper.LEFT], [RIGHT][ItemTouchHelper.RIGHT],
     * [START][ItemTouchHelper.START], [END][ItemTouchHelper.END].
     * @param onSwipeListener
     *     a callback to invoke when an item has been swiped. note that the direction parameter of the listener is the same
     *     as the one provided in [swipeDirections].
     *     for example: if you provided [swipeDirections] with [LEFT][ItemTouchHelper.LEFT] and/or [RIGHT][ItemTouchHelper.RIGHT]
     *     then that is the direction that the listener will have as its parameter
     *     (and NOT [START][ItemTouchHelper.START] and/or [END][ItemTouchHelper.END])
     */
    open fun enableSwiping(swipeDirections: Int, onSwipeListener: onItemSwipedListener<ITEM>)
    {
        onItemSwiped = Pair(swipeDirections, onSwipeListener)
    }

    /**
     * returns a [DxSwipeBackground] to show when swiping left,
     * or null to not show any background.
     *
     * note that this function will be called many many times during the swipe.
     * it is therefore recommended to return a pre-existing [DxSwipeBackground] rather than
     * creating one in this function
     *
     * also note that if you override this function, [swipeBackgroundLeft] will be ignored
     *
     * @param item the item that is being swiped (can be used to return different values
     * depending on the item state).
     */
    open fun getSwipeBackgroundLeft(item: ITEM): DxSwipeBackground? = swipeBackgroundLeft
    /**
     * returns a [DxSwipeBackground] to show when swiping right,
     * or null to not show any background.
     *
     * note that this function will be called many many times during the swipe.
     * it is therefore recommended to return a pre-existing [DxSwipeBackground] rather than
     * creating one in this function
     *
     * also note that if you override this function, [swipeBackgroundRight] will be ignored
     *
     * @param item the item that is being swiped (can be used to return different values
     * depending on the item state).
     */
    open fun getSwipeBackgroundRight(item: ITEM): DxSwipeBackground? = swipeBackgroundRight

    override fun getMovementFlags(recycler: RecyclerView, holder: ViewHolder): Int
    {
        val item = mAdapter.getFilteredAdapterItems()[holder.adapterPosition]
        val dragFlags =
            if (item !is IItemDraggable)
                0
            else
                ItemTouchHelper.UP or ItemTouchHelper.DOWN

        val swipeFlags =
            if (item !is IItemSwipeable || onItemSwiped == null)
                0
            else
                //for sure onItemSwiped is not null because of the "if" above
                onItemSwiped!!.first

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recycler: RecyclerView,
                        dragged: ViewHolder,
                        target: ViewHolder): Boolean
    {
        val dragPos = dragged.adapterPosition
        val targetPos = target.adapterPosition

        mAdapter.apply {
            val filteredItems = getFilteredAdapterItems()
            onItemMove?.invoke(filteredItems[dragPos], filteredItems[targetPos], dragPos, targetPos)
            Collections.swap(filteredItems, dragPos, targetPos)
            notifyItemMoved(dragPos, targetPos)
        }

        return true
    }

    private fun calculateIconLeft(swipeBack: DxSwipeBackground,
                                  isSwipingLeft: Boolean): Int
    {
        mDoesBackFit = swipeBack.doesBackgroundFitInSwipeArea()
        var temp: Int

        swipeBack.apply {
            swipeBack.mBackgroundColorDrawable.bounds.let { bounds ->
                return when
                {
                    mDoesBackFit && isSwipingLeft ->
                    {
                        temp = bounds.right - mIconWidthPx
                        if(mIconWidthPx > 0)
                            temp -= paddingPx
                        temp
                    }

                    //swiping right
                    mDoesBackFit ->
                    {
                        temp = bounds.left
                        if(mIconWidthPx > 0)
                            temp += paddingPx
                        temp
                    }

                    //mDoesBackFit is FALSE
                    isSwipingLeft ->
                    {
                        temp = bounds.left + paddingPx
                        if (mTextWidthPx > 0)
                            temp += mTextWidthPx + paddingPx
                        temp
                    }
                    //swiping right
                    else ->
                    {
                        temp = bounds.right - paddingPx - mIconWidthPx
                        //NOTE:
                        //do NOT do "temp -= <expression>" here because the order of
                        //operations is slightly different and with certain values it will
                        //cause bugs
                        if (mTextWidthPx > 0)
                            temp = temp - mTextWidthPx - paddingPx
                        temp
                    }
                }
            }
        }
    }

    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
                             dx: Float, dy: Float, actionState: Int, isCurrentlyActive: Boolean)
    {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            val itemView = viewHolder.itemView
            mIsSwipingLeft = dx < 0

            mSwipeBackgroundForDrawing = when
            {
                //not swiping, or swiping but item is exactly in the middle.
                //adapter position can be -1 if the item is being removed from the adapter.
                //in such cases, we don't draw the background
                dx == 0f || viewHolder.adapterPosition == -1 -> null

                mIsSwipingLeft ->
                    getSwipeBackgroundLeft(
                        mAdapter.getFilteredAdapterItems()[viewHolder.adapterPosition])?.apply {
                        mBackgroundColorDrawable.setBounds(itemView.right + dx.roundToInt(),
                                                           itemView.top,
                                                           itemView.right,
                                                           itemView.bottom)
                    }
                //swiping right
                else ->
                    getSwipeBackgroundRight(
                        mAdapter.getFilteredAdapterItems()[viewHolder.adapterPosition])?.apply {
                        mBackgroundColorDrawable.setBounds(itemView.left,
                                                           itemView.top,
                                                           itemView.left + dx.roundToInt(),
                                                           itemView.bottom)
                    }
            }

            mSwipeBackgroundForDrawing?.apply {
                //NOTE:
                //drawing background MUST come BEFORE drawing the mText
                mBackgroundColorDrawable.let { backDraw ->
                    backDraw.draw(canvas)

                    mIconTop = backDraw.bounds.centerY() - mHalfIconHeight
                    mIconBottom = backDraw.bounds.centerY() + mHalfIconHeight
                    mIconLeft = calculateIconLeft(this, mIsSwipingLeft)
                    mIconRight = mIconLeft + mIconWidthPx

                    dxIcon?.mIconDrawable?.apply {
                        setBounds(mIconLeft, mIconTop, mIconRight, mIconBottom)
                        draw(canvas)
                    }

                    dxText?.apply {
                        mPaint.getTextBounds(text, 0, text.length, mTextRect)

                        mTextY = backDraw.bounds.exactCenterY() + (mTextRect.height() / 4f)
                        mTextX =
                            if (mIsSwipingLeft)
                                mIconLeft.toFloat() - paddingPx - mTextWidthPx
                            //swiping right
                            else
                                mIconRight.toFloat() + paddingPx

                        canvas.drawText(text, mTextX, mTextY, mPaint)
                    }
                }
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
    }

    override fun onSwiped(holder: ViewHolder, direction: Int)
    {
        mAdapter.apply {
            holder.adapterPosition.let {
                onItemSwiped?.second?.invoke(getFilteredAdapterItems()[it], it, direction)
            }
        }
    }

    internal fun setDragHandle(@IdRes handleId: Int,
                             itemTouchHelper: ItemTouchHelper)
    {
        mAdapter.dragAndDropWithHandle =
            Pair(handleId, { holder -> itemTouchHelper.startDrag(holder) })
    }

    override fun isLongPressDragEnabled() = dragOnLongClick

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float
    {
        return swipeEscapeVelocityMultiplier?.let {
            swipeEscapeVelocityMultiplier!! * defaultValue
        }
            ?: swipeEscapeVelocity ?: super.getSwipeEscapeVelocity(defaultValue)
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder) =
        swipeThreshold ?: super.getSwipeThreshold(viewHolder)

    override fun isItemViewSwipeEnabled() = onItemSwiped?.first != null


    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int)
    {
        dragBackgroundColor?.apply {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder is DxHolder)
                viewHolder.itemView.background = ColorDrawable(dragBackgroundColor!!)
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder)
    {
        super.clearView(recyclerView, viewHolder)

        dragBackgroundColor?.apply {
            if (viewHolder is DxHolder)
                viewHolder.apply { itemView.background = originalBackground }
        }
    }
}