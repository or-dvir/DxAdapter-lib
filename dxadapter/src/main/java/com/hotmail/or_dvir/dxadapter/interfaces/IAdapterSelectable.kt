package com.hotmail.or_dvir.dxadapter.interfaces

import android.content.Context
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

    //todo should i have the listeners inside the adapter for convenience?
    //selection listener
    val onItemSelectionChanged: onItemSelectStateChangedListener<ITEM>?

    //todo is there a point in making this nullable???? dont the user has to override this anyway?
    @get:ColorInt
    val selectedItemBackgroundColor: Int?

    fun getAllSelectedItems() = mAdapterItems.filter { it is IItemSelectable && it.isSelected }
    fun getNumSelectedItems() = getAllSelectedItems().size
    fun getAllSelectedIndices() = getIndicesForItems(getAllSelectedItems())

    fun selectIndices(indices: List<Int>, triggerListener: Boolean = true) =
        selectOrDeselect(true, indices, triggerListener)
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
        selectOrDeselect(false, indices, triggerListener)
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
                                 indices: List<Int>,
                                 triggerListener: Boolean)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mAdapterItems[position].apply {
                    //only select/deselect if actually needed
                    //to avoid triggering listener multiple times
                    if(this is IItemSelectable && shouldSelect != isSelected)
                    {
                        isSelected = shouldSelect
                        if(triggerListener)
                            onItemSelectionChanged?.invoke(position, this, shouldSelect)

                        dxNotifyItemChanged(position)
                    }
                }
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
        val item = mAdapterItems[position]

        //todo when documenting this library, notice the order of the calls
        //todo first selection listener or first click listener????
        //todo example: if first selection, then click listener is AFTER the item has been selected/deselected

        //change selection state only if the item is selectable (could be multi-type adapter!),
        //AND user asked for default behavior,
        //AND we are already in selection mode (because default selection mode start with a LONG-click)
        if(item is IItemSelectable &&
            defaultItemSelectionBehavior &&
            isInSelectionMode())
        {
            //reverse the selection
            selectOrDeselect(!item.isSelected,
                             listOf(position),
                             true)
        }

        return if (wasInSelectionModeBefore)
            triggerClickListenersInSelectionMode
        else
            true
    }

    fun dxOnCreateViewHolder(context: Context,
                             parent: ViewGroup,
                             viewType: Int,
                             itemView: View)
    {
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
    }
}