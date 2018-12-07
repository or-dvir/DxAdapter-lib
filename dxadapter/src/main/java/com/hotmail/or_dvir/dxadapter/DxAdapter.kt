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

class DxAdapter<VH : RecyclerView.ViewHolder, ITEM: DxItem<VH>>(private val mItems: List<ITEM>)
    : RecyclerView.Adapter<VH>()
{
    private var mOnClickListener: onItemClickListener<ITEM>? = null
    private var mOnLongClickListener: onItemLongClickListener<ITEM>? = null
    private var mOnSelectStateChangedListener: onItemSelectStateChangedListener<ITEM>? = null

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    @ColorRes
    var selectedItemBackgroundColorRes: Int? = null

    /**
     * default value: TRUE
     *
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on any item
     * will select\deselect the clicked item.
     *
     * NOTE: in order for this to work, you must ALSO provide a long-click listener using
     * [setOnItemLongClickListener].
     *
     * if FALSE, you must manage item selection yourself using [select] and [deselect].
     *
     * ***also see [triggerClickListenersInSelectionMode]
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

    fun select(position: Int)
    {
        mItems[position].let {
            it.mIsSelected = true
            mOnSelectStateChangedListener?.apply {
                invoke(position, it, true)
            }
        }

        notifyItemChanged(position)
    }

    fun deselect(position: Int)
    {
        mItems[position].let {
            it.mIsSelected = false
            mOnSelectStateChangedListener?.apply {
                invoke(position, it, false)
            }
        }

        notifyItemChanged(position)
    }

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

    /**
     * "selection mode" means at least one item is selected
     */
    private fun isInSelectionMode(): Boolean
    {
        return mItems.find { it.mIsSelected } != null
    }

    fun setOnSelectedStateChangedListener(listener: onItemSelectStateChangedListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnSelectStateChangedListener = listener
        return this
    }

    fun setOnItemClickListener(listener: onItemClickListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: onItemLongClickListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnLongClickListener = listener
        return this
    }

    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)
        mItems[holder.adapterPosition].unbindViewHolder(holder)
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
        mOnClickListener?.apply {
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

                when
                {
                    !selectionAfter && !deselectedLastItem -> invoke(it, clickedPosition, clickedItem)
                    //if we get here, we ARE in "selection mode".
                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
                }
            }
        }

        mOnLongClickListener?.apply {
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