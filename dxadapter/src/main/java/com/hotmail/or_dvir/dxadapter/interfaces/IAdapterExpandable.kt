package com.hotmail.or_dvir.dxadapter.interfaces

import com.hotmail.or_dvir.dxadapter.onItemExpandStateChangedListener

interface IAdapterExpandable<ITEM: IDxItem>: IAdapterBase<ITEM>
{
    /**
     * default value: FALSE
     *
     * if TRUE, any call to one of the [expand] methods that accepts a list
     * would only expand the first item of that list.
     * In the case of [expandAll], only the first item in the adapter will be expanded.
     */
    val onlyOneItemExpanded: Boolean

    //expansion listeners
    var onItemExpandStateChanged: onItemExpandStateChangedListener<ITEM>?

    fun getAllExpandedItems() =
        mAdapterItems.filter { it is IItemExpandable && it.isExpanded }
    fun getNumExpandedItems() = getAllExpandedItems().size
    fun getAllExpandedIndices() = getIndicesForItems(getAllExpandedItems())

    /**
     * expands all the items of this adapter at the given indices.
     *
     * NOTE: if [onlyOneItemExpanded] is TRUE, then only the first item in [indices] will be expanded
     */
    fun expandIndices(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(true, indices, triggerListener)
    /**
     * expands all the given items.
     *
     * NOTE: if [onlyOneItemExpanded] is TRUE, then only the first item in [items] will be expanded
     */
    fun expand(items: List<ITEM>, triggerListener: Boolean = true) =
        expandIndices(getIndicesForItems(items), triggerListener)
    fun expand(index: Int) = expandIndices(listOf(index))
    fun expand(item: ITEM) = expand(listOf(item))

    /**
     * convenience function to expand all items.
     *
     * NOTE: that this function does NOT trigger [onItemExpanded].
     *
     * NOTE: if onlyOneItemExpanded is TRUE, only the first item in this adapter
     * will be expanded.
     */
    fun expandAll() = expand(mAdapterItems, false)

    fun collapseIndices(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(false, indices, triggerListener)
    fun collapse(index: Int) = collapseIndices(listOf(index))
    fun collapse(items: List<ITEM>, triggerListener: Boolean = true) =
        collapseIndices(getIndicesForItems(items), triggerListener)
    fun collapse(item: ITEM) = collapse(listOf(item))

    /**
     * convenience function to collapse all items.
     *
     * note that this function does NOT trigger [onItemCollapsed].
     */
    fun collapseAll() = collapse(mAdapterItems, false)

    private fun expandOrCollapse(shouldExpand: Boolean,
                                 indices: List<Int>,
                                 triggerListener: Boolean)
    {
//        //if we are in selection mode and we want default behavior,
//        //do not expand/collapse
//        if(this is IAdapterSelectable<*> && isInSelectionMode() && defaultItemSelectionBehavior)
//            return

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
                mAdapterItems[position].apply {

                    //only expand/collapse if not already expanded/collapsed
                    //so we don't trigger unnecessary listeners and ui updates
                    if(this is IItemExpandable &&
                        shouldExpand != isExpanded)
                    {
                        isExpanded = shouldExpand

                        if (triggerListener)
                            onItemExpandStateChanged?.invoke(position, this, shouldExpand)

                        if (shouldExpand)
                            checkOnlyOneItemExpanded(this)

                        dxNotifyItemChanged(position)
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
            //todo should this be true or false?!?!?!?
            collapse(allExceptNew, true)
        }
    }

    fun dxExpandableItemClicked(position: Int, wasInSelectionModeBefore: Boolean)
    {
        //if we are in selection mode and we want default behavior,
        //and we were in selection mode before getting here (which is AFTER selection has changed),
        //clicks are meant for selection and not expand/collapse
        if(this is IAdapterSelectable<*> && defaultItemSelectionBehavior && wasInSelectionModeBefore)
            return

        val item = mAdapterItems[position]

        if (item is IItemExpandable && item.expandCollapseOnItemClick())
            expandOrCollapse(!item.isExpanded, listOf(position), true)
    }

    fun dxExpandableItemLongClicked(position: Int)
    {
        //if we just selected our first item and we want default behavior,
        //collapse all items
        if (this is IAdapterSelectable<*> && defaultItemSelectionBehavior && getNumSelectedItems() == 1)
            collapseAll()
    }
}