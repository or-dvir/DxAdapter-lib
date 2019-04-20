package com.hotmail.or_dvir.dxadapter.interfaces

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.onItemSelectStateChangedListener

interface IAdapterSelectable<ITEM: IItemBase>: IAdapterBase<ITEM>
{
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
    val defaultItemSelectionBehavior: Boolean

    /**
     * if TRUE, clicking or long-clicking an item in "selection mode" (at least one item is selected)
     * would also trigger the click listener and long-click listener.
     *
     * if FALSE, those listeners will NOT be triggered if in "selection mode"
     */
    val triggerClickListenersInSelectionMode: Boolean

    val onItemSelectionChanged: onItemSelectStateChangedListener<ITEM>

    /**
     * setting this to null will use the app's accent color
     */
    @get:ColorInt
    val selectedItemBackgroundColor: Int?

    fun getAllSelectedItems() = mAdapterItems.filter { it is IItemSelectable && it.isSelected }
    fun getNumSelectedItems() = getAllSelectedItems().size
    fun getAllSelectedIndices() = getIndicesForItems(getAllSelectedItems())

    fun selectIndices(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(true, getItemsForIndices(indices), triggerListener)
    fun select(index: Int) = selectIndices(listOf(index))
    fun select(items: List<ITEM>, triggerListener: Boolean = true) =
        selectIndices(getIndicesForItems(items), triggerListener)
    fun select(item: ITEM) = select(listOf(item))

    /**
     * convenience function to select all items.
     *
     * note that this function does NOT trigger [onItemSelectionChanged].
     */
    fun selectAll() = select(mAdapterItems, false)

    fun deselectIndices(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(false, getItemsForIndices(indices), triggerListener)
    fun deselect(index: Int) = deselectIndices(listOf(index))
    fun deselect(items: List<ITEM>, triggerListener: Boolean = true) =
        deselectIndices(getIndicesForItems(items), triggerListener)
    fun deselect(item: ITEM) = deselect(listOf(item))

    fun isInSelectionMode() =
        mAdapterItems.find { it is IItemSelectable && it.isSelected } != null

    /**
     * convenience function to deselect all items.
     *
     * note that this function does NOT trigger [onItemSelectStateChangedListener].
     */
    fun deselectAll() = deselect(mAdapterItems, false)

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
     * @return Boolean whether or not we need to trigger long-click listener
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
     * @return Boolean whether or not we need to trigger click listener
     */
    fun dxSelectableItemClicked(position: Int, wasInSelectionModeBefore: Boolean): Boolean
    {
        //todo when documenting this library, notice the order of the calls
        // first selection listener or first click listener????
        // example: if first selection, then click listener is AFTER the item has been selected/deselected

        val item = mAdapterItems[position]

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

    fun dxOnCreateViewHolder(parent: ViewGroup,
                             viewType: Int,
                             itemView: View)
    {
        //only change the background if user chose default behavior.
        //this is to prevent overriding users' custom background (if set)
        if(defaultItemSelectionBehavior)
        {
            //todo when documenting, mention that the background will be overridden when item is "selected",
            // meaning that it will change the background to the selected color.
            // if user has custom selection background that is NOT a color, he should NOT use defaultItemSelectionBehavior
            // but then must handle other things by himself.
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