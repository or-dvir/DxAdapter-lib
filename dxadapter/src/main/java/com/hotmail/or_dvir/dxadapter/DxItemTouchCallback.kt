package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
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

    private val mDrawRect = Rect()

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

    /**
     * NOTE: this will trigger JUST BEFORE the items are moved
     */
    var onItemMove: onItemsMoveListener<ITEM>? = null

    var swipeBackgroundLeft: DxSwipeBackground? = null
    var swipeBackgroundRight: DxSwipeBackground? = null

    private var onItemSwiped: Pair<Int, onItemSwipedListener<ITEM>>? = null

    /**
     * @param swipeDirections Int: the direction of allowed swiping. one or more of:
     * [LEFT][ItemTouchHelper.LEFT], [RIGHT][ItemTouchHelper.RIGHT],
     * [START][ItemTouchHelper.START], [END][ItemTouchHelper.END].
     *
     * @param onSwipeListener onItemSwipedListener<ITEM>:
     *     a callback to invoke when an item is swiped.
     */
    fun setItemsSwipeable(swipeDirections: Int, onSwipeListener: onItemSwipedListener<ITEM>)
    {
        onItemSwiped = Pair(swipeDirections, onSwipeListener)
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
            onItemMove?.invoke(mItems[dragPos], mItems[targetPos], dragPos, targetPos)
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
            val itemView = viewHolder.itemView
            val isSwipingLeft = dx < 0 && dx != 0f

            //todo bug!!!!!
            //todo if there is no background, the text appears OVER the item!!!!

            val swipeBackground = when
            {
                //not swiping, or swiping but item is exactly in the middle.
                //in such cases, we don't draw the background
                dx == 0f -> null

                //swiping left
                isSwipingLeft ->
                    swipeBackgroundLeft?.apply {
                        mPaint.textAlign = Paint.Align.LEFT
                        mBackgroundColorDrawable.setBounds(itemView.right + dx.toInt(),
                                                           itemView.top,
                                                           itemView.right,
                                                           itemView.bottom)
                    }

                //swiping right
                else ->
                    swipeBackgroundRight?.apply {
                        mPaint.textAlign = Paint.Align.RIGHT
                        mBackgroundColorDrawable.setBounds(itemView.left,
                                                           itemView.top,
                                                           itemView.left + dx.toInt(),
                                                           itemView.bottom)
                    }
            }

            swipeBackground?.apply {
                //NOTE:
                //drawing background MUST come BEFORE drawing the mText
                mBackgroundColorDrawable.let { backDraw ->
                    backDraw.draw(c)
                    if(mText.isNotEmpty())
                    {
                        mPaint.let { paint ->
                            var halfTextWidth = (mDrawRect.width() / 2f)
                            //if swiping left, make the width negative because we need to SUBTRACT it
                            //when drawing the text
                            if (isSwipingLeft)
                                halfTextWidth *= -1

                            paint.getTextBounds(mText, 0, mText.length, mDrawRect)
                            c.drawText(mText,
                                       backDraw.bounds.exactCenterX() + halfTextWidth,
                                //todo not sure why i need to divide by 4 and not 2...
                                       backDraw.bounds.exactCenterY() + (mDrawRect.height() / 4f),
                                       paint)
                        }
                    }
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

    //todo allow swiping with a handle??? similar to drag and drop with a handle - look online for examples
    override fun isItemViewSwipeEnabled() = onItemSwiped?.first != null
}