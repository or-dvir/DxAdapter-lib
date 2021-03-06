package com.hotmail.or_dvir.dxadapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hotmail.or_dvir.dxadapter.interfaces.*

/**
 * The main class of this library. your adapter must extend this class and implement the
 * wanted behaviours ([IAdapterSelectable], [IAdapterExpandable], [IAdapterFilterable], [IAdapterStickyHeader]).
 *
 * @param ITEM the type of object this adapter will hold
 * @param VH the [DxHolder] (RecyclerView.ViewHolder) this adapter will use
 */
abstract class DxAdapter<ITEM : IItemBase, VH : DxHolder>/*(internal var mItems: MutableList<ITEM>)*/
    : RecyclerView.Adapter<VH>(),
      IAdapterBase<ITEM>
{
    private val mBackgroundColorDrawable = ColorDrawable()
    private var mIsFiltered = false

    //initialization needed for compiler - only the get function actually matters
    private var mFilteredItems = mutableListOf<ITEM>()
    get()
    {
        return if (mIsFiltered)
            field
        else
            getOriginalAdapterItems()
    }

//    private val mOriginalList = mItems

    //todo move this to draggable interface or wherever you handle dragging
    internal var dragAndDropWithHandle: Pair<Int, startDragListener>? = null


    override val mDxFilter = object : Filter()
    {
        //so we don't have to create a new object every time
        //performFiltering is called
        private val mResults = FilterResults()

        override fun performFiltering(constraint: CharSequence?): FilterResults?
        {
            //todo how to add animation to filtering????

            //NOTE:
            //no need to set mIsFiltered here because if the adapter is not IAdapterFilterable,
            //mIsFiltered will ALWAYS be false
            if(this@DxAdapter !is IAdapterFilterable<*>)
                return null

            val results =
                if (constraint.isNullOrEmpty())
                {
                    mIsFiltered = false
                    getOriginalAdapterItems()
//                    mOriginalList
//                    mAdapterItems
                }
                else
                {
                    mIsFiltered = true
                    onFilterRequest.invoke(constraint)
                }

            return mResults.apply {
                values = results
                count = results.size
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults?)
        {
            results?.apply {
                //note:
                //cannot check for generic types in kotlin.
                //but because onFilterRequest is defined with the generic type ITEM,
                //the user will get a compiler error if they return a list of a different type
                @Suppress("UNCHECKED_CAST")
                mFilteredItems = values as MutableList<ITEM>
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilteredAdapterItems() = mFilteredItems
    override fun getItemCount(): Int = mFilteredItems.size
    override fun getItemViewType(position: Int) = mFilteredItems[position].getViewType()

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int)
    {
        //position parameter may not be accurate
        val adapterPosition = holder.adapterPosition

        mFilteredItems[adapterPosition].let { item ->
                if(this is IAdapterSelectable<*>)
                    dxOnBindViewHolder(item, adapterPosition, holder, mBackgroundColorDrawable)

                if(this is IAdapterExpandable<*>)
                    dxOnBindViewHolder(item, adapterPosition, holder)

                bindViewHolder(holder, adapterPosition, item)
        }
    }

    @CallSuper
    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)

        holder.adapterPosition.let {
            if (it != RecyclerView.NO_POSITION)
                unbindViewHolder(holder, it, mFilteredItems[it])
        }
    }

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    // what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    // any other methods i should override??????

    override fun dxNotifyItemChanged(position: Int) = notifyItemChanged(position)

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(getItemLayoutRes(parent, viewType), parent, false)

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
            val clickedItem = mFilteredItems[clickedPosition]

            //handle selection
            var selectionBefore = false
            var triggerListener = true
            if(this@DxAdapter is IAdapterSelectable<*>)
            {
                selectionBefore = isInSelectionMode()
                triggerListener = dxSelectableItemClicked(clickedPosition, selectionBefore)
            }

            //handle expand/collapse
            if (this@DxAdapter is IAdapterExpandable<*>)
                dxExpandableItemClicked(clickedPosition, selectionBefore)

            //handle click listener
            if(triggerListener)
                onItemClick?.invoke(view, clickedPosition, clickedItem)
        }

        itemView.setOnLongClickListener { view ->
            val clickedPosition = holder.adapterPosition
            val clickedItem = mFilteredItems[clickedPosition]

            //WARNING:
            //do NOT save the state of isInSelectionMode() into a variable here
            //because below we are changing the selected state of the clicked item
            //and the variable would not be updated according to the new state

            var triggerListener = true
            if(this@DxAdapter is IAdapterSelectable<*>)
                triggerListener = dxSelectableItemLongClicked(clickedPosition)

            if(this@DxAdapter is IAdapterExpandable<*>)
                dxExpandableItemLongClicked()

            if (triggerListener)
                onItemLongClick?.invoke(view, clickedPosition, clickedItem) ?: true
            else
                true
        }

        return holder
    }

    /**
     * wrapper for [onCreateViewHolder][RecyclerView.Adapter.onCreateViewHolder]
     * with the addition of [itemView].
     *
     * use this function only. do NOT override
     * [onCreateViewHolder][RecyclerView.Adapter.onCreateViewHolder] directly
     * @param itemView the inflated view returned from [getItemLayoutRes]
     */
    abstract fun createAdapterViewHolder(itemView: View, parent: ViewGroup, viewType: Int): VH
    /**
     * returns the layout resource id for the view to to inflate in [createAdapterViewHolder]
     * @param parent the same as in [createAdapterViewHolder]
     * @param viewType the same as in [createAdapterViewHolder]
     */
    @LayoutRes
    abstract fun getItemLayoutRes(parent: ViewGroup, viewType: Int): Int
    /**
     * wrapper for [onBindViewHolder][RecyclerView.Adapter.onBindViewHolder]
     * with the addition of [item].
     *
     * use this function only. do NOT override
     * [onBindViewHolder][RecyclerView.Adapter.onBindViewHolder] directly
     * @param item the item at [position]
     */
    abstract fun bindViewHolder(holder: VH, position: Int, item: ITEM)
    /**
     * wrapper for [onViewRecycled][RecyclerView.Adapter.onViewRecycled]
     * with the addition of [position] and [item].
     *
     * use this function only. do NOT override
     * [onViewRecycled][RecyclerView.Adapter.onViewRecycled] directly
     * @param position the adapter position being recycled
     * @param item the item associated with [position]
     */
    abstract fun unbindViewHolder(holder: VH, position: Int, item: ITEM)
}