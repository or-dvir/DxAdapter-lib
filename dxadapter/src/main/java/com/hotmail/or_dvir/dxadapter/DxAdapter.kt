package com.hotmail.or_dvir.dxadapter

import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.interfaces.IDxBase
import com.hotmail.or_dvir.dxadapter.interfaces.IDxExpandable
import com.hotmail.or_dvir.dxadapter.interfaces.IDxSelectable

abstract class DxAdapter<ITEM : DxItem, VH : RecyclerViewHolder>(internal var mItems: MutableList<ITEM>)
    : RecyclerView.Adapter<VH>(),
      Filterable,
      IDxBase<ITEM>
{
    //todo find a way so that the user does NOT have to extend DxItem

    override val mAdapterItems = mItems

    //click listeners
    var onItemClick: onItemClickListener<ITEM>? = null
    var onItemLongClick: onItemLongClickListener<ITEM>? = null

    var dxFilter: dxFilter<ITEM>? = null

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)

    internal var dragAndDropWithHandle: Pair<Int, startDragListener>? = null

    private val mOriginalList = mItems
    private val privateFilter = object : Filter()
    {
        override fun performFiltering(constraint: CharSequence): FilterResults?
        {
            //todo how to add animation to filtering????

            //todo exception is thrown as a warning???? colored in yellow!!!
            if (dxFilter == null)
                throw UninitializedPropertyAccessException("you must initialize the field \"dxFilter\" before filtering")

            val results =
                if (constraint.isEmpty())
                    mOriginalList
                else
                //for SURE this is not null because of the "if" condition above
                    dxFilter!!.invoke(constraint)

            return FilterResults().apply {
                values = results
                count = results.size
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults)
        {
            //note:
            //cannot check for generic types in kotlin.
            //but because dxFilter is defined with the generic type ITEM,
            //the user will get a compiler error if they return a list of a different type
            @Suppress("UNCHECKED_CAST")
            mItems = results.values as MutableList<ITEM>
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int) = mItems[position].getViewType()
    override fun getItemCount(): Int = mItems.size

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int)
    {
        mItems[position].let { item ->
            holder.itemView.let {
                it.isSelected = item.mIsSelected

                if (item is DxItemExpandable)
                {
                    it.findViewById<View>(item.getExpandableViewId()).visibility =
                        if (item.mIsExpanded)
                            View.VISIBLE
                        else
                            View.GONE
                }

                bindViewHolder(holder, position, item)
            }
        }
    }

    @CallSuper
    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)

        holder.adapterPosition.let {
            if (it != RecyclerView.NO_POSITION)
                unbindViewHolder(holder, it, mItems[it])
        }
    }

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    //todo what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    //todo any other important methods i should override??????

    //todo make sure every function has good documentation!!!

    override fun dxNotifyItemChanged(position: Int) = notifyItemChanged(position)

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val context = parent.context

        val itemView = LayoutInflater
                .from(context)
                .inflate(getItemLayoutRes(parent, viewType), parent, false)

        if(this is IDxSelectable<*>)
            dxOnCreateViewHolder(context, parent, viewType, itemView)
        //todo do i need rest of the interfaces here!!!!

        val holder = createAdapterViewHolder(itemView, parent, viewType)

        dragAndDropWithHandle?.let {
            //this line is needed for the compiler
            itemView.findViewById<View>(it.first).setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN)
                    it.second.invoke(holder)

                //allow normal processing to continue
                false
            }
        }

        //NOTE:
        //we CANNOT have "clickedPosition" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
        itemView.setOnClickListener { view ->

            val clickedPosition = holder.adapterPosition
            val clickedItem = mItems[clickedPosition]

            //handle selection

            var selectionBefore = false
            var triggerListener = true
            if(this@DxAdapter is IDxSelectable<*>)
            {
                selectionBefore = isInSelectionMode()
                triggerListener = dxSelectableItemClicked(clickedPosition, selectionBefore)
            }

            if(triggerListener)
                onItemClick?.invoke(view, clickedPosition, clickedItem)

            //handle expand/collapse

            if (this@DxAdapter is IDxExpandable<*>)
                dxExpandableItemClicked(clickedPosition, selectionBefore)

            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first click listener????
            //todo example: if first selection, then click listener is AFTER the item has been selected/deselected
        }

        itemView.setOnLongClickListener { view ->
            val clickedPosition = holder.adapterPosition
            val clickedItem = mItems[clickedPosition]

            //WARNING:
            //do NOT save the state of isInSelectionMode() into a variable here
            //because below we are changing the selected state of the clicked item
            //and the variable would not be updated according to the new state

            var triggerListener = true
            if(this@DxAdapter is IDxSelectable<*>)
                triggerListener = dxSelectableItemLongClicked(clickedPosition)

            if(this@DxAdapter is IDxExpandable<*>)
                dxExpandableItemLongClicked(clickedPosition)

            //todo dont forget the rest of the interfaces!!!

            if (triggerListener)
                onItemLongClick?.invoke(view, clickedPosition, clickedItem) ?: true
            else
                true

            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first long-click listener????
        }

        return holder
    }

    /**
     * convenience method instead of calling [getFilter().filter(constraint)].
     *
     * Note: you MUST initialize [dxFilter] or an exception will be thrown.
     * @param constraint to get the original list, set this to an empty string
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter() = privateFilter

    abstract fun createAdapterViewHolder(itemView: View, parent: ViewGroup, viewType: Int): VH
    @LayoutRes
    abstract fun getItemLayoutRes(parent: ViewGroup, viewType: Int): Int
    abstract fun bindViewHolder(holder: VH, position: Int, item: ITEM)
    abstract fun unbindViewHolder(holder: VH, position: Int, item: ITEM)
}