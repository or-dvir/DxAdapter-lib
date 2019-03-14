package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import java.util.*

class DxItemTouchCallback<ITEM: DxItem>(private val mAdapter: DxAdapter<ITEM, *>)
    : ItemTouchHelper.Callback()
{
    //todo test drag and drop and callbacks with grid layout manager!!!!

    /**
     * default value: FALSE
     *
     * if TRUE, long-clicking an item will initiate drag-and-drop
     *
     * if FALSE, long-clicking an item will NOT initiate drag-and-drop
     *
     * NOTE: be aware that if you enable this feature and long-click is also used to select items,
     * (for example with [DxAdapter.defaultItemSelectionBehavior]) then long-clicking might not
     * produce the intended result (selecting an item when actually meant to drag and vice-versa)
     */
    var dragOnLongClick = false

    /**
     * see [ItemTouchHelper.Callback.getSwipeThreshold] for details
     */
    var swipeThreshold: Float? = null

    /**
     * sets a fixed value for the swipe escape velocity.
     * this value is overridden by [swipeEscapeVelocityMultiplier] (if set).
     *
     * see [ItemTouchHelper.Callback.getSwipeEscapeVelocity] for more details.
     */
    var swipeEscapeVelocity: Float? = null

    /**
     * sets a value for the swipe escape velocity as a multiplier
     * of the device's default value.
     * this value overrides [swipeEscapeVelocity]
     *
     * see [ItemTouchHelper.Callback.getSwipeEscapeVelocity] for details
     */
    var swipeEscapeVelocityMultiplier: Float? = null

    /**
     * if your list is actually a grid, set this value to TRUE.
     * otherwise, drag-and-drop will not work as expected
     */
    var isGridLayoutManager = false

    //todo add function for AFTER move?????????
    /**
     * NOTE: this will trigger JUST BEFORE the items are moved
     */
    var onItemsAboutToMove: onItemsMovedListener<ITEM>? = null

    /**
     * FIRST: the direction of allowed swiping. one or more of:
     * [LEFT][ItemTouchHelper.LEFT], [RIGHT][ItemTouchHelper.RIGHT],
     * [START][ItemTouchHelper.START], [END][ItemTouchHelper.END].
     *
     * NOTE: the directions you give here will affect the "direction" parameter for the callback.
     * for example, if you provide [START][ItemTouchHelper.START] and [END][ItemTouchHelper.END],
     * the callback will also return [START][ItemTouchHelper.START] and [END][ItemTouchHelper.END] and NOT
     * [LEFT][ItemTouchHelper.LEFT] and [RIGHT][ItemTouchHelper.RIGHT]
     *
     * SECOND: a callback which will triggered after an item is dismissed.
     */
    var onItemSwiped: Pair<Int, onItemDismissedListener<ITEM>>? = null

    var swipeTextLeft: DxSwipeText? = null
        set(value)
        {
            value?.mPaint?.textAlign = Paint.Align.LEFT
            field = value
        }

    var swipeTextRight: DxSwipeText? = null
        set(value)
        {
            value?.mPaint?.textAlign = Paint.Align.RIGHT
            field = value
        }

    override fun getMovementFlags(recycler: RecyclerView, holder: ViewHolder): Int
    {
        val item = mAdapter.mItems[holder.adapterPosition]
        val dragFlags =
                when
                {
                    !item.isDraggable() -> 0
                    isGridLayoutManager -> //enable drag in all directions
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    else -> //only enable drag UP and DOWN
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }

        val swipeFlags =
            if (!item.isSwipeable() || onItemSwiped == null)
                0
            else
                //for sure onItemSwiped is not null because of the "if" above
                onItemSwiped!!.first

        //todo should i allow swiping when using grid layout??? maybe let the user decide????
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recycler: RecyclerView,
                        dragged: ViewHolder,
                        target: ViewHolder): Boolean
    {
        val dragPos = dragged.adapterPosition
        val targetPos = target.adapterPosition

        mAdapter.apply {
            onItemsAboutToMove?.invoke(mItems[dragPos], mItems[targetPos], dragPos, targetPos)
            Collections.swap(mItems, dragPos, targetPos)
            notifyItemMoved(dragPos, targetPos)
        }

        return true
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: ViewHolder,
                             dx: Float,
                             dy: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean)
    {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            //todo can i make this more efficient???? meaning NOT setting these variables here
            //todo but instead take them from swipeTextLeft and swipeTextRight???????
            var swipeColor: ColorDrawable? = null
            var swipePaint: Paint? = null
            var swipeRect: Rect? = null
            var swipeText = ""
            val itemView = viewHolder.itemView
            val isSwipingRight = dx > 0


            //todo a lot of repetition between swipeTextRight and swipeTextLeft
            //todo can i make it better???
            if (isSwipingRight)
            {
                swipeTextRight?.apply {
                    swipeRect = mRect
                    swipeText = mText
                    swipePaint = mPaint
                    swipeColor = mBackgroundColorDrawable?.apply {
                        setBounds(itemView.left,
                                  itemView.top,
                                  itemView.left + dx.toInt(),
                                  itemView.bottom)
                    }
                }
            }

            //Swiping left
            else
            {
                swipeTextLeft?.apply {
                    swipeRect = mRect
                    swipeText = mText
                    swipePaint = mPaint
                    swipeColor = mBackgroundColorDrawable?.apply {
                        setBounds(itemView.right + dx.toInt(),
                                  itemView.top,
                                  itemView.right,
                                  itemView.bottom)
                    }
                }
            }

            //todo this is forcing the user to set a background color for swiping!!!!
            //todo make it possible to just have mText
            //NOTE:
            //this MUST come AFTER the above "when" statement
            swipeColor?.apply {
                //NOTE:
                //drawing background MUST come BEFORE drawing the mText
                draw(c)
                swipePaint?.let {
                    var halfTextWidth = (swipeRect!!.width() / 2f)
                    //if swiping left, make the width negative because we need to SUBTRACT it
                    //when drawing the mText
                    if (!isSwipingRight)
                        halfTextWidth *= -1

                    it.getTextBounds(swipeText, 0, swipeText.length, swipeRect)
                    c.drawText(swipeText,
                               bounds.exactCenterX() + halfTextWidth,
                        //todo not sure why i have to divide by 4 and not 2...
                               bounds.exactCenterY() + (swipeRect!!.height() / 4f),
                               it)
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
    }

    override fun onSwiped(holder: ViewHolder, direction: Int)
    {
        mAdapter.apply {
            holder.adapterPosition.let {
                onItemSwiped?.second?.invoke(mItems[it], it, direction)
            }
        }
    }

    internal fun setDragHandle(@IdRes handleId: Int,
                             itemTouchHelper: ItemTouchHelper)
    {
        mAdapter.dragAndDropWithHandle =
            Pair(handleId, { holder -> itemTouchHelper.startDrag(holder) })
    }

    override fun isLongPressDragEnabled(): Boolean
    {
        //todo when documenting, make a note of this!!!
        return if (mAdapter.dragAndDropWithHandle != null)
            false
        else
            dragOnLongClick
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float) =
        swipeEscapeVelocityMultiplier ?: swipeEscapeVelocity ?: super.getSwipeEscapeVelocity(defaultValue)

    override fun getSwipeThreshold(viewHolder: ViewHolder) =
        swipeThreshold ?: super.getSwipeThreshold(viewHolder)

    //todo allow swiping with a handle!!! similar to drag and drop with a handle - look online for examples
    override fun isItemViewSwipeEnabled() = onItemSwiped?.first != null
}