package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable

abstract class DxAdapter<ITEM : DxItem, VH : RecyclerViewHolder>(internal var mItems: MutableList<ITEM>)
    : RecyclerView.Adapter<VH>(),
      Filterable
{
    //todo find a way so that the user does NOT have to extend DxItem

    //todo this class is getting too big.
    //todo separate adapter features (select/expand/etc) to separate classes

    //click listeners
    var onItemClick: onItemClickListener<ITEM>? = null
    var onItemLongClick: onItemLongClickListener<ITEM>? = null

    /**
     * default value: FALSE
     *
     * if TRUE, any call to one of the [expand] methods that accepts a list
     * would only expand the first item of that list.
     * In the case of [expandAll], only the first item in the adapter will be expanded.
     */
    var onlyOneItemExpanded = false

    /**
     * default value: FALSE
     *
     * if TRUE, clicking or long-clicking an item in "selection mode" (at least one item is selected)
     * would also trigger the click listener and long-click listener.
     */
    var triggerClickListenersInSelectionMode = false

    //selection listener
    var onItemSelectionChanged: onItemSelectStateChangedListener<ITEM>? = null

    @ColorInt
    var selectedItemBackgroundColor: Int? = null

    /**
     * default value: TRUE
     *
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on any item
     * will select\deselect the clicked item.
     *
     * if FALSE, you must manage item selection yourself using [select] and [deselect].
     *
     * @see [triggerClickListenersInSelectionMode]
     */
    var defaultItemSelectionBehavior = true

    //expansion listeners
    var onItemExpanded: positionAndItemListener<ITEM>? = null
    var onItemCollapsed: positionAndItemListener<ITEM>? = null

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
    private fun isInBounds(position: Int) = position in (0 until mItems.size)

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

    fun getAllSelectedItems() = mItems.filter { it.mIsSelected }
    fun getNumSelectedItems() = getAllSelectedItems().size
    fun getAllSelectedIndices() = getIndicesForItems(getAllSelectedItems())

    @JvmName("selectListIndices")
    fun select(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(true, indices, triggerListener)
    fun select(index: Int) = select(listOf(index))
    fun select(items: List<ITEM>, triggerListener: Boolean = true) =
        select(getIndicesForItems(items), triggerListener)
    fun select(item: ITEM) = select(listOf(item))

    /**
     * convenience function to select all items.
     *
     * note that this function does NOT trigger [onItemSelectStateChangedListener].
     */
    fun selectAll() = select(mItems, false)

    //todo add documentation that when selecting/deselecting all items listeners will NOT be triggered!!!
    @JvmName("deselectListIndices")
    fun deselect(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(false, indices, triggerListener)
    fun deselect(index: Int) = deselect(listOf(index))
    fun deselect(items: List<ITEM>, triggerListener: Boolean = true) =
        deselect(getIndicesForItems(items), triggerListener)
    fun deselect(item: ITEM) = deselect(listOf(item))

    /**
     * convenience function to deselect all items.
     *
     * note that this function does NOT trigger [onItemSelectStateChangedListener].
     */
    fun deselectAll() = deselect(mItems, false)

    private fun selectOrDeselect(shouldSelect: Boolean,
                                 indices: List<Int>,
                                 triggerListener: Boolean)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {
                    //only select/deselect if actually needed
                    //to avoid triggering listener multiple times
                    if(isSelectable() && shouldSelect != mIsSelected)
                    {
                        mIsSelected = shouldSelect
                        if(triggerListener)
                            onItemSelectionChanged?.invoke(position, this, shouldSelect)

                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    //todo make sure every function has good documentation!!!

    fun getAllExpandedItems() = mItems.filter { it is DxItemExpandable && it.mIsExpanded }
    fun getNumExpandedItems() = getAllExpandedItems().size
    fun getAllExpandedIndices() = getIndicesForItems(getAllExpandedItems())

    /**
     * expands all the items of this adapter at the given indices.
     *
     * NOTE: if [onlyOneItemExpanded] is TRUE, then only the first item in [indices] will be expanded
     */
    @JvmName("expandListIndices")
    fun expand(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(true, indices, triggerListener)
    /**
     * expands all the given items.
     *
     * NOTE: if [onlyOneItemExpanded] is TRUE, then only the first item in [items] will be expanded
     */
    fun expand(items: List<ITEM>, triggerListener: Boolean = true) =
        expand(getIndicesForItems(items), triggerListener)
    fun expand(index: Int) = expand(listOf(index))
    fun expand(item: ITEM) = expand(listOf(item))

    /**
     * convenience function to expand all items.
     *
     * NOTE: that this function does NOT trigger [onItemExpanded].
     *
     * NOTE: if onlyOneItemExpanded is TRUE, only the first item in this adapter
     * will be expanded.
     */
    fun expandAll() = expand(mItems, false)

    @JvmName("collapseListIndices")
    fun collapse(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(false, indices, triggerListener)
    fun collapse(index: Int) = collapse(listOf(index))
    fun collapse(items: List<ITEM>, triggerListener: Boolean = true) =
        collapse(getIndicesForItems(items), triggerListener)
    fun collapse(item: ITEM) = collapse(listOf(item))

    /**
     * convenience function to collapse all items.
     *
     * note that this function does NOT trigger [onItemCollapsed].
     */
    fun collapseAll() = collapse(mItems, false)

    private fun expandOrCollapse(shouldExpand: Boolean,
                                 indices: List<Int>,
                                 triggerListener: Boolean)
    {
        if(isInSelectionMode())
            return

        val tempIndices =
            when
            {
                indices.isEmpty() -> null
                shouldExpand && onlyOneItemExpanded -> listOf(indices[0])
                else -> indices
            }

        //todo this is getting too nested... see if you can improve it
        tempIndices?.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {

                    //only expand/collapse if not already expanded/collapsed
                    //so we don't trigger unnecessary listeners and ui updates
                    if(this is DxItemExpandable &&
                        shouldExpand != mIsExpanded)
                    {
                        mIsExpanded = shouldExpand

                        if (triggerListener)
                        {
                            if (shouldExpand)
                                onItemExpanded?.invoke(position, this)
                            else
                                onItemCollapsed?.invoke(position, this)
                        }

                        if (shouldExpand)
                            checkOnlyOneItemExpanded(this)
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun checkOnlyOneItemExpanded(newExpandedItem: ITEM)
    {
        if(onlyOneItemExpanded)
        {
            val allExceptNew =
                getAllExpandedItems().toMutableList().apply { remove(newExpandedItem) }

            //list could be long... so prevent a lot of calls to the listener
            //with the optional variable
            collapse(allExceptNew, true)
        }
    }

    private fun getIndicesForItems(items: List<ITEM>) = items.map { mItems.indexOf(it) }

    /**
     * "selection mode" means at least one item is selected
     */
    private fun isInSelectionMode() = mItems.find { it.mIsSelected } != null

    @ColorInt
    private fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val context = parent.context

        val itemView = LayoutInflater
                .from(context)
                .inflate(getItemLayoutRes(parent, viewType), parent, false)

        //only change the background if user chose default behavior.
        //this is to prevent overriding users' custom background (if set)
        if(defaultItemSelectionBehavior)
        {
            //todo when documenting, mention that the background will be overridden when item is "selected",
            //todo meaning that it will change the background to the selected color.
            //todo if user has custom selection background that is NOT a color, he should NOT use defaultItemSelectionBehavior
            //todo but then must handle other things by himself.
            StateListDrawable().apply {
                //selected
                addState(intArrayOf(android.R.attr.state_selected),
                         ColorDrawable(selectedItemBackgroundColor ?: getThemeAccentColorInt(context)))
                //not selected
                addState(intArrayOf(-android.R.attr.state_selected), itemView.background)
                itemView.background = this
            }
        }

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

            if(clickedItem is DxItemExpandable && clickedItem.expandAndCollapseOnItemClick())
                expandOrCollapse(!clickedItem.mIsExpanded, listOf(clickedPosition), true)


            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first click listener????
            //todo example: if first selection, then click listener is AFTER the item has been selected/deselected

            val selectionModeBefore = isInSelectionMode()

            //change selection state only if user asked for default behavior AND
            //we are already in selection mode (because default selection mode start with a LONG-click)
            if(defaultItemSelectionBehavior && selectionModeBefore)
            {
                //reverse the selection
                selectOrDeselect(!clickedItem.isSelectable(),
                                 listOf(clickedPosition),
                                 true)
            }

            //if we were NOT in selection mode before -> regular click -> trigger the listener.
            //if we WERE in selection mode before and the user
            //requested it (right operand of the "OR" condition) -> trigger the listener.
            if(!selectionModeBefore || triggerClickListenersInSelectionMode)
                onItemClick?.invoke(view, clickedPosition, clickedItem)
        }

        itemView.setOnLongClickListener { view ->
            val clickedPosition = holder.adapterPosition
            val clickedItem = mItems[clickedPosition]

            //WARNING:
            //do NOT save the state of isInSelectionMode() into a variable here
            //because below we are changing the selected state of the clicked item
            //and the variable would not be updated according to the new state

            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first long-click listener????

            //only select an item on long-click if defaultItemSelectionBehavior AND
            //we are not already in selection mode (if we ARE already in selection mode,
            //selection is handled by REGULAR clicks)
            if (defaultItemSelectionBehavior &&
                !isInSelectionMode())
            {
                collapseAll()
                select(clickedPosition)
            }

            onItemLongClick?.let {
                //if we are NOT in selection mode -> regular long-click -> trigger the listener.
                //if we ARE in selection mode and the user
                //requested it (right operand of the "OR" condition) -> trigger the listener.
                if (!isInSelectionMode() || triggerClickListenersInSelectionMode)
                    it.invoke(view, clickedPosition, clickedItem)

                //in any other case we need a boolean return value
                else
                    true
            } ?: true
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