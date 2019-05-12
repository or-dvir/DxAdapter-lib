package com.hotmail.or_dvir.dxadapter.interfaces

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.widget.Filter
import com.hotmail.or_dvir.dxadapter.onItemClickListener
import com.hotmail.or_dvir.dxadapter.onItemLongClickListener

/**
 * an interface containing some shared behaviour for all adapters to be used by the library.
 * no need for you to implement this interface yourself
 */
interface IAdapterBase<ITEM: IItemBase>
{
    /**
     * the filter used by the adapter, if it implements [IAdapterFilterable]
     */
    val mDxFilter: Filter
    /**
     * a listener to be invoked whenever an item is clicked
     */
    val onItemClick: onItemClickListener<ITEM>?
    /**
     * a listener to be invoked whenever an item is long-clicked
     */
    val onItemLongClick: onItemLongClickListener<ITEM>?

    /**
     * returns the list of items CURRENTLY held by this adapter. note that the list is NOT
     * necessarily the original list passed to the adapter (for example: if the
     * adapter is filtered, the filtered list will be returned).
     *
     * WARNING!
     *
     * do NOT override this method yourself!
     */
    fun getFilteredAdapterItems(): MutableList<ITEM>
    /**
     * should return the original list of items passed to the adapter
     */
    fun getOriginalAdapterItems(): MutableList<ITEM>
    /**
     * returns a list of indices for the given [items].
     *
     * note that the returned list may contain -1 as it uses [List.indexOf]
     */
    fun getIndicesForItems(items: List<ITEM>) = items.map { getIndexForItem(it) }
    /**
     * returns the index of the given [item]
     */
    fun getIndexForItem(item: ITEM) = getFilteredAdapterItems().indexOf(item)
    /**
     * returns a list of [ITEM] at the given [indices]
     */
    fun getItemsForIndices(indices: List<Int>) = indices.map { getItemForIndex(it) }
    /**
     * returns the [ITEM] at the given [index]
     */
    fun getItemForIndex(index: Int) = getFilteredAdapterItems()[index]
    /**
     * wrapper for [RecyclerView.Adapter.notifyItemChanged]
     */
    fun dxNotifyItemChanged(position: Int)
    /**
     * gets the accent color of your app
     */
    @ColorInt
    fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }
}
