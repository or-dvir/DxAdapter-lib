package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import java.util.*

class DxItemTouchCallback<ITEM: DxItem/*<VH>, VH: RecyclerViewHolder*/>(private val adapter: DxAdapter<ITEM, */*VH*/>)
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
//        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recycler: RecyclerView,
                        dragged: ViewHolder,
                        target: ViewHolder): Boolean
    {
        val dragPos = dragged.adapterPosition
        val targetPos = target.adapterPosition

        //todo add swap listener here

        adapter.apply {
            onItemsMovedListener?.invoke(mItems[dragPos], mItems[targetPos], dragPos, targetPos)
            Collections.swap(mItems, dragPos, targetPos)
            notifyItemMoved(dragPos, targetPos)
        }

        return true
    }

//    //todo check if the background color also works for card view!!!
//    //todo think about how you can add this feature...
//    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int)
//    {
//        movedItemBackgroundColor?.let { color ->
//            if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)
//            {
//                viewHolder?.itemView?.apply {
//                    mSavedMovedItemBackgroundColor = background
//                    setBackgroundColor(color)
//                }
//            }
//        }
//
//        super.onSelectedChanged(viewHolder, actionState)
//    }
//
//    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder)
//    {
//        super.clearView(recyclerView, viewHolder)
//
//        //only change the background color if the user specifically requested it (movedItemBackgroundColor != null)
//        //so you don't override whatever background
//        movedItemBackgroundColor?.let {
//            viewHolder.itemView.setBackgroundColor(0)
//        }
//    }

    override fun onSwiped(holder: ViewHolder, direction: Int)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //todo when adding drag-handle feature, make this function always return false
    //todo i.e. only 1 method of dragging items
    override fun isLongPressDragEnabled() = dragOnLongClick
}