package com.hotmail.or_dvir.dxadapter.interfaces

import android.view.View
import com.hotmail.or_dvir.dxadapter.DxHolder
import com.hotmail.or_dvir.dxadapter.onItemExpandStateChangedListener

/**
 * implement this interface in your ADAPTER if you wish for it to support expandable items.
 */
interface IAdapterExpandable<ITEM: IItemBase>: IAdapterBase<ITEM>
{
    /**
     * if TRUE, any call to one of the [expand] methods that accepts a list
     * would only expand the first item of that list.
     * in the case of [expandAll], only the first item in the adapter will be expanded
     */
    val onlyOneItemExpanded: Boolean
    /**
     * a listener for when an item is expanded/collapsed
     */
    var onItemExpandStateChanged: onItemExpandStateChangedListener<ITEM>

    /**
     * return a list of all currently expanded items
     */
    fun getAllExpandedItems() = getFilteredAdapterItems().filter { it is IItemExpandable && it.isExpanded }
//    fun getAllExpandedItems() = mAdapterItems.filter { it is IItemExpandable && it.isExpanded }
    /**
     * returns the number of currently expanded items
     */
    fun getNumExpandedItems() = getAllExpandedItems().size
    /**
     * returns a list of indices for all currently expanded items
     */
    fun getAllExpandedIndices() = getIndicesForItems(getAllExpandedItems())
    /**
     * expands all the items at the given [indices].
     *
     * note that if [onlyOneItemExpanded] is TRUE, then only the first item in [indices] will be expanded
     * @param indices the indices to expand
     * @param triggerListener optional parameter to whether or not trigger [onItemExpandStateChangedListener].
     * defaults to TRUE
     */
    fun expandIndices(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(true, getItemsForIndices(indices), triggerListener)
    /**
     * expands all the given [items]
     *
     * note that if [onlyOneItemExpanded] is TRUE, then only the first item in [items] will be expanded
     * @param items the items to expand
     * @param triggerListener optional parameter to whether or not trigger [onItemExpandStateChangedListener].
     * defaults to TRUE
     */
    fun expand(items: List<ITEM>, triggerListener: Boolean = true) =
        expandIndices(getIndicesForItems(items), triggerListener)
    /**
     * expands the item at the given [index]
     */
    fun expand(index: Int) = expandIndices(listOf(index))
    /**
     * expands the given [item]
     */
    fun expand(item: ITEM) = expand(listOf(item))
    /**
     * convenience function to expand all items.
     *
     * note that this function does NOT trigger [onItemExpandStateChanged]
     * (because the adapter may contain a lot of items and this will cause a lot of calls to the listener).
     * if you DO wish to trigger the listener, use [expand] (List variant) or [expandIndices] and pass all your items/indices.
     *
     * note that if [onlyOneItemExpanded] is TRUE, only the first item in this adapter
     * will be expanded.
     */
    fun expandAll() = expand(getFilteredAdapterItems(), false)
//    fun expandAll() = expand(mAdapterItems, false)
    /**
     * collapses all the items in the given [indices].
     *
     * @param indices the indices to collapse
     * @param triggerListener optional parameter to whether or not trigger [onItemExpandStateChangedListener].
     * defaults to TRUE
     */
    fun collapseIndices(indices: List<Int>, triggerListener: Boolean = true) =
        expandOrCollapse(false, getItemsForIndices(indices), triggerListener)
    /**
     * collapses the item at the given [index]
     */
    fun collapse(index: Int) = collapseIndices(listOf(index))
    /**
     * collapses all the given [items]
     *
     * @param items the items to collapse
     * @param triggerListener optional parameter to whether or not trigger [onItemExpandStateChangedListener].
     * defaults to TRUE
     */
    fun collapse(items: List<ITEM>, triggerListener: Boolean = true) =
        collapseIndices(getIndicesForItems(items), triggerListener)
    /**
     * collapses the given [item]
     */
    fun collapse(item: ITEM) = collapse(listOf(item))
    /**
     * convenience function to collapse all items.
     *
     * note that this function does NOT trigger [onItemExpandStateChanged]
     * (because the adapter may contain a lot of items and this will cause a lot of calls to the listener).
     * if you DO wish to trigger the listener, use [collapse] (List variant) or [collapseIndices] and pass all your items/indices.
     */
    fun collapseAll() = collapse(getFilteredAdapterItems(), false)
//    fun collapseAll() = collapse(mAdapterItems, false)

    private fun expandOrCollapse(shouldExpand: Boolean,
                                 items: List<ITEM>,
                                 triggerListener: Boolean)
    {
//        //if we are in selection mode and we want default behavior,
//        //do not expand/collapse
//        if(this is IAdapterSelectable<*> && isInSelectionMode() && defaultItemSelectionBehavior)
//            return

        val tempItems =
            when
            {
                items.isEmpty() -> null
                shouldExpand && onlyOneItemExpanded -> listOf(items[0])
                else -> items
            }

        var tempPosition: Int
        tempItems?.forEach {
            //only expand/collapse if not already expanded/collapsed
            //so we don't trigger unnecessary listeners and ui updates
            if (it is IItemExpandable && shouldExpand != it.isExpanded)
            {
                it.isExpanded = shouldExpand
                tempPosition = getIndexForItem(it)

                if (triggerListener)
                    onItemExpandStateChanged.invoke(tempPosition, it, shouldExpand)

                if (shouldExpand)
                    checkOnlyOneItemExpanded(it)

                dxNotifyItemChanged(tempPosition)
            }
        }
    }

    private fun checkOnlyOneItemExpanded(newExpandedItem: ITEM)
    {
        if(onlyOneItemExpanded)
        {
            val allExceptNew =
                getAllExpandedItems().toMutableList().apply { remove(newExpandedItem) }

            collapse(allExceptNew)
        }
    }
    /**
     * used by the library. do not override
     */
    fun dxExpandableItemClicked(position: Int, wasInSelectionModeBefore: Boolean)
    {
        //if we are in selection mode and we want default behavior,
        //and we were in selection mode before getting here (which is AFTER selection has changed),
        //clicks are meant for selection and not expand/collapse
        if(this is IAdapterSelectable<*> && defaultItemSelectionBehavior && wasInSelectionModeBefore)
            return

        val item = getFilteredAdapterItems()[position]
//        val item = mAdapterItems[position]

        if (item is IItemExpandable && item.expandCollapseOnItemClick())
            expandOrCollapse(!item.isExpanded, listOf(item), true)
    }
    /**
     * used by the library. do not override
     */
    fun dxExpandableItemLongClicked()
    {
        //if we just selected our first item and we want default behavior,
        //collapse all items
        if (this is IAdapterSelectable<*> && defaultItemSelectionBehavior && getNumSelectedItems() == 1)
            collapseAll()
    }
    /**
     * used by the library. do not override
     */
    fun dxOnBindViewHolder(item: IItemBase, position: Int, holder: DxHolder)
    {
        if(item is IItemExpandable)
        {
            holder.itemView.findViewById<View>(item.getExpandableViewId()).visibility =
                if (item.isExpanded)
                    View.VISIBLE
                else
                    View.GONE
        }
    }
}