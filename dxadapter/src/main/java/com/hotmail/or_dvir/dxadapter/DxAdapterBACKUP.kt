package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup

class DxAdapterBACKUP<VH : RecyclerViewHolder, ITEM: DxItem<VH>>(internal val mItems: List<ITEM>)
    : RecyclerView.Adapter<VH>()
{
    //todo make these public and remove setter methods - this library is meant for kotlin
    var onClickListener: onItemClickListener<ITEM>? = null
    var onLongClickListener: onItemLongClickListener<ITEM>? = null
    var onSelectStateChangedListener: onItemSelectStateChangedListener<ITEM>? = null

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    @ColorRes
    var selectedItemBackgroundColorRes: Int? = null

    /**
     * if set, selecting an item will start "Action Mode",
     * and deselecting the last item will finish it.
     *
     * if not set: you must handle ActionMode yourself
     *
     * * in order for this to work, you MUST ALSO set [onSelectStateChangedListener],
     * AND call [updateActionMode] inside it.
     *
     * * if the ActionMode is finished, all items will be deselected WITHOUT triggering
     * [onSelectStateChangedListener]
     */
//    var actionModeHelper: DxActionModeHelper? = null

//    /**
//     * this function starts/finishes actionMode, and updates its' title.
//     * it's assumed that this function is called inside [onSelectStateChangedListener].
//     * if it's called from other places, it may cause bugs.
//     */
//    fun updateActionMode(act: AppCompatActivity)
//    {
//        actionModeHelper?.apply {
//
//            //NOTE:
//            //at this point we should be AFTER the selected state has already changed.
//            //i say "should" because we are assuming that this function is called from inside
//            //onSelectStateChangedListener
//
//            when (getNumSelectedItems())
//            {
//                1 -> start(act)
//                0 -> finish()
//            }
//
//            updateTitle()
//        }
//    }

    /**
     * default value: TRUE
     *
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on any item
     * will select\deselect the clicked item.
     *
     * NOTE: in order for this to work, you MUST ALSO set [onItemLongClickListener]
     *
     * if FALSE, you must manage item selection yourself using [select] and [deselect].
     *
     * @see [triggerClickListenersInSelectionMode]
     */
    var defaultItemSelectionBehavior = true

    /**
     * default value: FALSE
     *
     * if TRUE, clicking or long-clicking an item in "selection mode" (at least one item is selected)
     * would also trigger the click listener and long-click listener.
     *
     * if FALSE, those listeners would NOT be triggered in "selection mode".
     */
    var triggerClickListenersInSelectionMode = false

    override fun getItemCount(): Int = mItems.size
    private fun isInBounds(position: Int) = position in (0 until mItems.size)

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int)
    {
        mItems[position].let { item ->
            holder.itemView.isSelected = item.mIsSelected
            item.bindViewHolder(holder)
        }
    }

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    //todo what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    //todo any other important methods i should override??????

    fun select(vararg items: ITEM) = items.forEach { select(mItems.indexOf(it)) }
    /**
     * for indices which are out of bounds - nothing happens
     */
    fun select(vararg indices: Int)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {
                    //only select if previously not selected
                    //so we don't trigger onSelectStateChangedListener unnecessarily
                    if(!mIsSelected)
                    {
                        mIsSelected = true
                        onSelectStateChangedListener?.invoke(position, this, true)
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }
//    /**
//     * does NOT trigger selectedStateChangedListener.
//     * if you DO want to trigger the listener,
//     * use [select] and pass the entire list
//     */
//    fun selectAll()
//    {
//        mItems.forEach {
//            it.mIsSelected = true
//        }
//
//        notifyDataSetChanged()
//    }

    fun deselect(vararg items: ITEM) = items.forEach { deselect(mItems.indexOf(it)) }
    /**
     * for indices which are out of bounds - nothing happens
     */
    fun deselect(vararg indices: Int)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {
                    //only deselect if previously selected
                    //so we don't trigger onSelectStateChangedListener unnecessarily
                    if(mIsSelected)
                    {
                        mIsSelected = false
                        onSelectStateChangedListener?.invoke(position, this, false)
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }
//    /**
//     * does NOT trigger selectedStateChangedListener.
//     * if you DO want to trigger the listener,
//     * use [deselect] and pass the entire list
//     */
//    private fun deselectAll()
//    {
//        mItems.forEach { it.mIsSelected = false }
//        notifyDataSetChanged()
//    }

    /**
     * "selection mode" means at least one item is selected
     */
    private fun isInSelectionMode() = mItems.find { it.mIsSelected } != null

    fun getAllSelectedItems() = mItems.filter { it.mIsSelected }
    fun getNumSelectedItems() = getAllSelectedItems().size
    fun getAllSelectedIndices(): List<Int>
    {
        return mItems.mapIndexed { index, item ->
            if (item.mIsSelected)
                index
            else
                null
        }.filterNotNull()
    }

    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)

        val position = holder.adapterPosition
        if(position != RecyclerView.NO_POSITION)
            mItems[position].unbindViewHolder(holder)
    }

    @ColorInt
    private fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val first = mItems.first()
        val context = parent.context

        val itemView = LayoutInflater
                .from(context)
                .inflate(first.getLayoutRes(), parent, false)

        StateListDrawable().apply {
            //replacement method requires API 23 (lib min is 21)
            @Suppress("DEPRECATION")
            val selectedColorInt =
                if (selectedItemBackgroundColorRes != null)
                    //for sure selectedItemBackgroundColorRes is NOT null because of the "if"
                    context.resources.getColor(selectedItemBackgroundColorRes!!)
                else
                    getThemeAccentColorInt(context)

            //selected
            addState(intArrayOf(android.R.attr.state_selected),
                     ColorDrawable(selectedColorInt))

            itemView.background = this
        }

        val holder = first.createViewHolder(itemView)

        //NOTE:
        //we CANNOT have "position" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
        onClickListener?.apply {
            itemView.setOnClickListener {
                val clickedPosition = holder.adapterPosition
                val clickedItem = mItems[clickedPosition]

                //WARNING:
                //do NOT save the state of isInSelectionMode() into a variable here
                //because below this line we are changing the selected state of the clicked item
                //and the variable would not be updates according to the new state

                //todo when documenting this library, notice the order of the calls
                //todo first selection listener or first click listener????

                val selectionBefore = isInSelectionMode()

                if(defaultItemSelectionBehavior && isInSelectionMode())
                {
                    //reverse the selection
                    if(clickedItem.mIsSelected)
                        deselect(clickedPosition)
                    else
                        select(clickedPosition)
                }

                val selectionAfter = isInSelectionMode()
                val deselectedLastItem = selectionBefore && !selectionAfter

                //triggering the click listener
                when
                {
                    !selectionAfter && !deselectedLastItem -> invoke(it, clickedPosition, clickedItem)
                    //if we get here, we ARE in "selection mode".
                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
                }
            }
        }

        onLongClickListener?.apply {
            itemView.setOnLongClickListener {
                val clickedPosition = holder.adapterPosition
                val clickedItem = mItems[clickedPosition]

                //WARNING:
                //do NOT save the state of isInSelectionMode() into a variable here
                //because below this line we are changing the selected state of the clicked item
                //and the variable would not be updates according to the new state

                //todo when documenting this library, notice the order of the calls
                //todo first selection listener or first long-click listener????

                //long-click should NOT select items if:
                //1) defaultItemSelectionBehaviour is false
                //2) the item is already selected (this would trigger unnecessary callbacks and UI updates).
                //3) at least one item is already selected (we are in "selection mode" where regular clicks
                //   should select/deselect an item)
                if (defaultItemSelectionBehavior &&
                    !clickedItem.mIsSelected &&
                    !isInSelectionMode())
                {
                    select(clickedPosition)
                }

                //triggering the long-click listener
                when
                {
                    !isInSelectionMode() -> invoke(it, clickedPosition, clickedItem)
                    //if we get here, we ARE in "selection mode".
                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
                    //consume the event
                    else -> true
                }
            }
        }

        return holder
    }
}