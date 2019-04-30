package com.hotmail.or_dvir.dxadapter.interfaces

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.widget.Filter

interface IAdapterBase<ITEM: IItemBase>
{
    /**
     * the items held by the adapter
     */
    val mAdapterItems: List<ITEM>
    /**
     * the filter used by the adapter, if it implements [IAdapterFilterable]
     */
    val mDxFilter: Filter

    /**
     * returns a list of indices for the given [items].
     *
     * note that the returned list may contain -1 as it uses [List.indexOf]
     */
    fun getIndicesForItems(items: List<ITEM>) = items.map { getIndexForItem(it) }
    /**
     * returns the index of the given [item]
     */
    fun getIndexForItem(item: ITEM) = mAdapterItems.indexOf(item)
    /**
     * returns a list of [ITEM] at the given [indices]
     */
    fun getItemsForIndices(indices: List<Int>) = indices.map { getItemForIndex(it) }
    /**
     * returns the [ITEM] at the given [index]
     */
    fun getItemForIndex(index: Int) = mAdapterItems[index]
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
