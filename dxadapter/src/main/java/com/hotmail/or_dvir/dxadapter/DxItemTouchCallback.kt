package com.hotmail.or_dvir.dxadapter

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import java.util.*


class DxItemTouchCallback<ITEM: DxItem/*<VH>, VH: RecyclerViewHolder*/>(private val mAdapter: DxAdapter<ITEM, */*VH*/>)
    : ItemTouchHelper.Callback()
{

    private var mmmmmSwipeBack = false
    private var buttonShowedState = ButtonsState.GONE
    private val mButtonWidth = 300f
    internal enum class ButtonsState
    {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

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

    //todo add function for AFTER move?????????
    /**
     * NOTE: this will trigger JUST BEFORE the items are moved
     */
    var onItemsAboutToMoveListener: onItemsMovedListener<ITEM>? = null

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

    //todo make this a setter like kotlin should be!!!!!!
    private fun setSwipeBackgroundColor()
    {
        mSwipeBackgroundColorDrawable = ColorDrawable()
    }

    override fun getMovementFlags(recycler: RecyclerView, holder: ViewHolder): Int
    {
//        here you should make item not dragable if the user doesnt want it to be (like a header)
//        you can get item: mAdapter[holder.adapterPosition]
//
//        where else is drag affected?????

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
            onItemsAboutToMoveListener?.invoke(mItems[dragPos], mItems[targetPos], dragPos, targetPos)
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
//        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
//        {
//            val itemView = viewHolder.itemView
//            val swipingRight = dX > 0
//
//            if (!swipingRight)
//            {
//                itemView.translationX = dX / 5
//            }
//        }
//        else
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


//        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
//        {
//            setTouchListener(recyclerView, dX)
//        }

        ////////////////////////////////////////////////
        var translationX = dx
        ////////////////////////////////////////////////

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            var backgroundText = ""
            val itemView = viewHolder.itemView
            val resources = itemView.resources
            val swipingRight = dx > 0

            if (swipingRight)
            {
                /////////////////////////////////////////////////////////////////////
                if(translationX >= mButtonWidth)
                {
                    translationX = mButtonWidth
                    itemView.translationX = translationX
                }
                /////////////////////////////////////////////////////////////////////

                swipeBackgroundColorRight?.let {
                    mSwipeBackgroundColorDrawable?.apply {
                        color = resources.getColor(it)
                        setBounds(itemView.left,
                                  itemView.top,
                                  itemView.left + dx.toInt(),
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
                        setBounds(itemView.right + dx.toInt(),
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
                    var halfTextWidth = (mTextRect!!.width() / 2f)
                    //if swiping left, make the width negative because we need to SUBTRACT it
                    //when drawing the text
                    if (!swipingRight)
                        halfTextWidth *= -1

                    it.getTextBounds(backgroundText, 0, backgroundText.length, mTextRect)
                    c.drawText(backgroundText,
                               bounds.exactCenterX() + halfTextWidth,
                        //not sure why i have to divide by 4 and not 2...
                               bounds.exactCenterY() + (mTextRect!!.height() / 4f),
                               it)
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dy, actionState, isCurrentlyActive)
//        super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float
    {
        //todo let user control this!!!!
        //default is 0.5f
        return 0.99f
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
//        c: Canvas,
        recyclerView: RecyclerView,
//        viewHolder: RecyclerViewHolder,
        dX: Float//,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
    )
    {
        recyclerView.setOnTouchListener { v, event ->
            mmmmmSwipeBack = event.action.let {
                it == MotionEvent.ACTION_CANCEL ||
                it == MotionEvent.ACTION_UP
            }

            if (mmmmmSwipeBack)
            {
                if (dX < -mButtonWidth)
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE
                else if (dX > mButtonWidth)
                    buttonShowedState = ButtonsState.LEFT_VISIBLE

                if (buttonShowedState != ButtonsState.GONE)
                {
//                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                    setItemsClickable(recyclerView, false);
                }
            }



            false
        }
    }

//    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int
//    {
//        return if (mmmmmSwipeBack)
//        {
//            mmmmmSwipeBack = false
//            0
//        }
//        else
//            super.convertToAbsoluteDirection(flags, layoutDirection)
//    }

    override fun onSwiped(holder: ViewHolder, direction: Int)
    {
        mAdapter.apply {
            holder.adapterPosition.let {
                onItemSwiped?.second?.invoke(mItems[it], it, direction)
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
    override fun isItemViewSwipeEnabled() = onItemSwiped?.first != null
}