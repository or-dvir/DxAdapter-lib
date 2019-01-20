package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import java.util.*

class DxItemTouchCallback<ITEM: DxItem/*<VH>, VH: RecyclerViewHolder*/>(private val mAdapter: DxAdapter<ITEM, */*VH*/>)
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
     * if your list is actually a grid, set this value to TRUE.
     * otherwise, drag-and-drop will not work as expected
     */
    var isGridLayoutManager = false

    /**
     * NOTE: this will trigger JUST BEFORE the items are moved
     */
    var onItemsMovedListener: onItemsMovedListener<ITEM>? = null

    /**
     * first: the direction of allowed swiping. MUST be [ItemTouchHelper.LEFT],
     * [ItemTouchHelper.RIGHT], or both.
     *
     * second: a callback which will trigger JUST BEFORE the item is dismissed and deleted from the adapter.
     */
    var swipeToDismiss: Pair<Int, onItemDismissedListener<ITEM>>? = null

    private var mTextPaint: Paint? = null
    private var mTextRect: Rect? = null
    private var mSwipeBackgroundColorDrawable: ColorDrawable? = null

    /**
     * sets the text to be displayed when an item is swiped to the right.
     *
     * see [swipeBackgroundText] for details
     */
    var swipeBackgroundTextRight: swipeBackgroundText? = null
        set(value)
        {
            field = value
            setSwipeText()
        }

    /**
     * sets the text to be displayed when an item is swiped to the left.
     *
     * see [swipeBackgroundText] for details
     */
    var swipeBackgroundTextLeft: swipeBackgroundText? = null
        set(value)
        {
            field = value
            setSwipeText()
        }

    /**
     * set this variable if you'd like to color the background of the swiped item when it is swiped
     * to the right.
     */
    @ColorRes
    var swipeBackgroundColorRight: Int? = null
        set(value)
        {
            field = value
            setSwipeBackgroundColor()
        }

    /**
     * set this variable if you'd like to color the background of the swiped item when it is swiped
     * to the left.
     */
    @ColorRes
    var swipeBackgroundColorLeft: Int? = null
        set(value)
        {
            field = value
            setSwipeBackgroundColor()
        }

    private fun setSwipeText()
    {
        mTextPaint = Paint()
        mTextRect = Rect()
    }

    private fun setSwipeBackgroundColor()
    {
        mSwipeBackgroundColorDrawable = ColorDrawable()
    }

    override fun getMovementFlags(recycler: RecyclerView, holder: ViewHolder): Int
    {
        val dragFlags =
            //enable drag in all directions
            if (isGridLayoutManager)
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            //only enable drag UP and DOWN
            else
                ItemTouchHelper.UP or ItemTouchHelper.DOWN

        //todo should i allow swiping when using grid layout??? maybe let the user decide????
        return makeMovementFlags(dragFlags, swipeToDismiss?.first ?: 0)
    }

    override fun onMove(recycler: RecyclerView,
                        dragged: ViewHolder,
                        target: ViewHolder): Boolean
    {
        val dragPos = dragged.adapterPosition
        val targetPos = target.adapterPosition

        mAdapter.apply {
            onItemsMovedListener?.invoke(mItems[dragPos], mItems[targetPos], dragPos, targetPos)
            Collections.swap(mItems, dragPos, targetPos)
            notifyItemMoved(dragPos, targetPos)
        }

        return true
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean)
    {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            var backgroundText = ""
            val itemView = viewHolder.itemView
            val resources = itemView.resources
            val swipingRight = dX > 0

            if (swipingRight)
            {
                    swipeBackgroundColorRight?.let {
                        mSwipeBackgroundColorDrawable?.apply {
                            color = resources.getColor(it)
                            setBounds(itemView.left,
                                      itemView.top,
                                      itemView.left + dX.toInt(),
                                      itemView.bottom)
                        }
                    }

                    swipeBackgroundTextRight?.apply {
                        backgroundText = first
                        mTextPaint?.let {
                            it.textSize = second
                            it.color = resources.getColor(third)
                            it.textAlign = Paint.Align.RIGHT
                        }
                    }
            }

            //Swiping left
            else
            {
                swipeBackgroundColorLeft?.let {
                    mSwipeBackgroundColorDrawable?.apply {
                        //for sure swipeBackgroundColorRight is NOT null because of the if statement above
                        color = resources.getColor(it)
                        setBounds(itemView.right + dX.toInt(),
                                  itemView.top,
                                  itemView.right,
                                  itemView.bottom)
                    }
                }

                swipeBackgroundTextLeft?.apply {
                    backgroundText = first
                    mTextPaint?.let {
                        it.textSize = second
                        it.color = resources.getColor(third)
                        it.textAlign = Paint.Align.LEFT
                    }
                }
            }

            //NOTE:
            //this MUST come AFTER the above "when" statement
            mSwipeBackgroundColorDrawable?.apply {
                //NOTE:
                //drawing background MUST come BEFORE drawing the text
                draw(c)
                mTextPaint?.let {
                    var halfTextWidth = (mTextRect!!.width()/2f)
                    //if swiping left, make the width negative because we need to SUBTRACT it
                    //when drawing the text
                    if (!swipingRight)
                        halfTextWidth *= -1

                    it.getTextBounds(backgroundText, 0, backgroundText.length, mTextRect)
                    c.drawText(backgroundText,
                               bounds.exactCenterX() + halfTextWidth,
                               //not sure why i have to divide by 4 and not 2...
                               bounds.exactCenterY() + (mTextRect!!.height()/4f),
                               it)
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(holder: ViewHolder, direction: Int)
    {
        mAdapter.apply {
            holder.adapterPosition.let {
                swipeToDismiss?.second?.invoke(mItems[it], it)
                mItems.removeAt(it)
                notifyItemRemoved(it)
            }
        }
    }

    override fun isLongPressDragEnabled(): Boolean
    {
        //todo when documenting, make a note of this!!!
        return if (mAdapter.dragAndDropWithHandle != null)
            false
        else
            dragOnLongClick
    }

    //todo allow swiping with a handle!!! similar to drag and drop with a handle - look online for examples
    override fun isItemViewSwipeEnabled() = swipeToDismiss?.first != null
}