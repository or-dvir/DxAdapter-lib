package com.hotmail.or_dvir.dxadapter.interfaces

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.onItemSelectStateChangedListener

/**
 * implement this interface in your ADAPTER if you wish for it to support selection of items.
 */
interface IAdapterSelectable<ITEM: IItemBase>: IAdapterBase<ITEM>
{
    //todo add support for selecting cards. REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)

    /**
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on
     * any other item will select\deselect it.
     *
     * if FALSE, you must manage item selection yourself using the variants of [select] and [deselect]
     *
     * note that when TRUE, when an item is selected, its background will be overridden.
     * if you have a custom background you should set this value to FALSE but then you must
     * handle selection behaviour by yourself (using [select] and [deselect] variants of this interface)
     * @see [triggerClickListenersInSelectionMode]
     */
    val defaultItemSelectionBehavior: Boolean
    /**
     * if TRUE, clicking or long-clicking an item in "selection mode" (at least one item is selected)
     * would also trigger the click listener and long-click listener.
     *
     * if FALSE, those listeners will NOT be triggered when in "selection mode"
     */
    val triggerClickListenersInSelectionMode: Boolean
    /**
     * a listener for when an item is selected or deselected
     */
    val onItemSelectionChanged: onItemSelectStateChangedListener<ITEM>
    /**
     * the background color of a selected item.
     * setting this to null will use the app's accent color
     */
    @get:ColorInt
    val selectedItemBackgroundColor: Int?

    /**
     * returns a list of all currently selected items
     */
    fun getAllSelectedItems() = getAdapterItems().filter { it is IItemSelectable && it.isSelected }
//    fun getAllSelectedItems() = mAdapterItems.filter { it is IItemSelectable && it.isSelected }
    /**
     * returns the number of currently selected items
     */
    fun getNumSelectedItems() = getAllSelectedItems().size
    /**
     * returns a list of all currently selected indices
     */
    fun getAllSelectedIndices() = getIndicesForItems(getAllSelectedItems())
    /**
     * selects all the items in the given [indices]
     * @param indices the indices to select
     * @param triggerListener optional parameter to whether or not trigger [onItemSelectStateChangedListener].
     * defaults to TRUE
     */
    fun selectIndices(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(true, getItemsForIndices(indices), triggerListener)
    /**
     * selects the item at the given [index]
     */
    fun select(index: Int) = selectIndices(listOf(index))
    /**
     * selects all the given [items]
     * @param items the items to select
     * @param triggerListener optional parameter to whether or not trigger [onItemSelectStateChangedListener].
     * defaults to TRUE
     */
    fun select(items: List<ITEM>, triggerListener: Boolean = true) =
        selectIndices(getIndicesForItems(items), triggerListener)
    /**
     * selects the given [item]
     */
    fun select(item: ITEM) = select(listOf(item))
    /**
     * convenience function to select all items.
     *
     * note that this function does NOT trigger [onItemSelectStateChangedListener]
     * (because the adapter may contain a lot of items and this will cause a lot of calls to the listener).
     * if you DO wish to trigger the listener, use [select] (List variant) or [selectIndices] and pass all your items/indices.
     */
    fun selectAll() = select(getAdapterItems(), false)
//    fun selectAll() = select(mAdapterItems, false)
    /**
     * deselects all the items in the given [indices]
     * @param indices the indices to deselect
     * @param triggerListener optional parameter to whether or not trigger [onItemSelectStateChangedListener].
     * defaults to TRUE
     */
    fun deselectIndices(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(false, getItemsForIndices(indices), triggerListener)
    /**
     * deselects the item at the given [index]
     */
    fun deselect(index: Int) = deselectIndices(listOf(index))
    /**
     * deselects all the given [items]
     * @param items the items to deselect
     * @param triggerListener optional parameter to whether or not trigger [onItemSelectStateChangedListener].
     * defaults to TRUE
     */
    fun deselect(items: List<ITEM>, triggerListener: Boolean = true) =
        deselectIndices(getIndicesForItems(items), triggerListener)
    /**
     * deselects the given [item]
     */
    fun deselect(item: ITEM) = deselect(listOf(item))
    /**
     * returns whether or not the adapter is currently in "selection mode" (at least one item is selected)
     */
    fun isInSelectionMode() = getAdapterItems().find { it is IItemSelectable && it.isSelected } != null
//    fun isInSelectionMode() = mAdapterItems.find { it is IItemSelectable && it.isSelected } != null
    /**
     * convenience function to deselect all items.
     *
     * note that this function does NOT trigger [onItemSelectStateChangedListener]
     * (because the adapter may contain a lot of items and this will cause a lot of calls to the listener).
     * if you DO wish to trigger the listener, use [deselect] (List variant) or [deselectIndices] and pass all your items/indices.
     */
    fun deselectAll() = deselect(getAdapterItems(), false)
//    fun deselectAll() = deselect(mAdapterItems, false)

    private fun selectOrDeselect(shouldSelect: Boolean,
                                 items: List<ITEM>,
                                 triggerListener: Boolean)
    {
        var tempPosition: Int
        items.forEach {
            //only select/deselect if actually needed
            //to avoid triggering listener multiple times
            if (it is IItemSelectable && shouldSelect != it.isSelected)
            {
                it.isSelected = shouldSelect
                tempPosition = getIndexForItem(it)

                if (triggerListener)
                    onItemSelectionChanged.invoke(tempPosition, it, shouldSelect)

                dxNotifyItemChanged(tempPosition)
            }
        }
    }
    /**
     * used by the library
     */
    fun dxSelectableItemLongClicked(position: Int): Boolean
    {
        //only select an item on long-click if defaultItemSelectionBehavior AND
        //we are not already in selection mode (if we ARE already in selection mode,
        //selection is handled by REGULAR clicks)
        if (defaultItemSelectionBehavior && !isInSelectionMode())
            select(position)

        return if (isInSelectionMode())
            triggerClickListenersInSelectionMode
        else
            true
    }
    /**
     * used by the library.
     * @return Boolean whether or not we need to trigger click listener
     */
    fun dxSelectableItemClicked(position: Int, wasInSelectionModeBefore: Boolean): Boolean
    {
        val item = getAdapterItems()[position]
//        val item = mAdapterItems[position]

        //reverse selection state only if the item is selectable (could be multi-type adapter!),
        //AND user asked for default behavior,
        //AND we are already in selection mode (because default selection mode start with a LONG-click)
        if(item is IItemSelectable && defaultItemSelectionBehavior && isInSelectionMode())
            selectOrDeselect(!item.isSelected, listOf(item), true)

        return if (wasInSelectionModeBefore)
            triggerClickListenersInSelectionMode
        else
            true
    }
    /**
     * used by the library
     */
    fun dxOnCreateViewHolder(parent: ViewGroup,
                             viewType: Int,
                             itemView: View)
    {
        //only change the background if user chose default behavior.
        //this is to prevent overriding users' custom background (if set)
        if(defaultItemSelectionBehavior)
        {
            StateListDrawable().apply {
                //selected
                addState(intArrayOf(android.R.attr.state_selected),
                         ColorDrawable(selectedItemBackgroundColor ?: getThemeAccentColorInt(parent.context)))
                //not selected
                addState(intArrayOf(-android.R.attr.state_selected), itemView.background)
                itemView.background = this
            }
        }
    }
}