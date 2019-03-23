package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Rect
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import kotlin.math.roundToInt

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

    private val mTextRect = Rect()
    private var mDoesBackFit = false

    private var mIconTop = 0
    private var mIconBottom = 0
    private var mIconLeft = 0
    private var mIconRight = 0
    private var mTextX = 0f
    private var mTextY = 0f
    private var mIsSwipingLeft = false
    private var mSwipeBackground: DxSwipeBackground? = null

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
            notifyItemMoved(dragPos, targetPos)
        }

        return true
    }

    private fun calculateIconLeft(swipeBack: DxSwipeBackground,
                                  isSwipingLeft: Boolean)
            : Int
    {
        mDoesBackFit = swipeBack.doesBackgroundFitInSwipeArea()
        var temp: Int

        swipeBack.apply {
            swipeBack.mBackgroundColorDrawable.bounds.let { bounds ->
                return when
                {
                    mDoesBackFit && isSwipingLeft ->
                        bounds.right - mPaddingPx - mIconWidth

                    //swiping right
                    mDoesBackFit -> bounds.left + mPaddingPx

                    //mDoesBackFit is FALSE
                    isSwipingLeft ->
                    {
                        temp = bounds.left + mPaddingPx
                        if (mTextWidth > 0)
                            temp += mTextWidth + mPaddingPx

                        temp
                    }
                    //swiping right
                    else ->
                    {
                        temp = bounds.right - mPaddingPx - mIconWidth
                        //NOTE:
                        //do NOT do "temp -= <expression>" here because the order of
                        //operations is slightly different and with certain values it will
                        //cause bugs
                        if (mTextWidth > 0)
                            temp = temp - mTextWidth - mPaddingPx

                        temp
                    }
                }
            }
        }
    }

    override fun onChildDraw(canvas: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: ViewHolder,
                             dx: Float,
                             dy: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean)
    {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE)
            return

        //todo should i keep global reference to view???? is it safe in this class??????
        val itemView = viewHolder.itemView
        mIsSwipingLeft = dx < 0 && dx != 0f

        mSwipeBackground = when
        {
            //not swiping, or swiping but item is exactly in the middle.
            //in such cases, we don't draw the background
            dx == 0f -> null

            mIsSwipingLeft ->
                swipeBackgroundLeft?.apply {
                    mBackgroundColorDrawable.setBounds(itemView.right + dx.roundToInt(),
                                                       itemView.top,
                                                       itemView.right,
                                                       itemView.bottom)
                }

            //swiping right
            else ->
                swipeBackgroundRight?.apply {
                    mBackgroundColorDrawable.setBounds(itemView.left,
                                                       itemView.top,
                                                       itemView.left + dx.roundToInt(),
                                                       itemView.bottom)
                }
        }

        mSwipeBackground?.apply {
            //NOTE:
            //drawing background MUST come BEFORE drawing the mText
            mBackgroundColorDrawable.let { backDraw ->
                backDraw.draw(canvas)

                mIconTop = backDraw.bounds.centerY() - mHalfIconHeight
                mIconBottom = backDraw.bounds.centerY() + mHalfIconHeight
                mIconLeft = calculateIconLeft(this, mIsSwipingLeft)
                mIconRight = mIconLeft + (mIcon?.intrinsicWidth ?: 0)

                mIcon?.apply {
                    setBounds(mIconLeft, mIconTop, mIconRight, mIconBottom)
                    draw(canvas)
                }

                BUG!!! if i only have text, it jumps!!! somewhere i count twice the padding!!!!
                if(mText.isNotBlank())
                {
                    mPaint.getTextBounds(mText, 0, mText.length, mTextRect)

                    mTextY = backDraw.bounds.exactCenterY() + (mTextRect.height() / 4f)
                    mTextX =
                        if(mIsSwipingLeft)
                            mIconLeft.toFloat() - mPaddingPx - mTextWidth
                        else
                            mIconRight.toFloat() + mPaddingPx

                    canvas.drawText(mText, mTextX, mTextY, mPaint)
                }
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
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