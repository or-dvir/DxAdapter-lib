package com.hotmail.or_dvir.dxadapter.interfaces

import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.onFilterRequest

/**
 * implement this interface in your ADAPTER if you wish for it to support filtering.
 *
 * IMPORTANT:
 * when the adapter is filtered, its list of items is temporarily replaced with the filtered
 * list (returned by [onFilterRequest]) until the filter is removed.
 * because of this, when the adapter is filtered, any action you wish to perform on an item MUST
 * be performed on BOTH the original list (the one you passed to the adapter) AND the filtered list
 * (obtained with [getDxAdapterItems]).
 *
 * IMPORTANT:
 * when performing an action on filtered lists, if possible, AVOID using index/position as the item
 * at that index/position will be DIFFERENT in the original list than in the filtered list.
 *
 * if you must use index/position, pay very close attention to whether it represents the
 * index/position for the original list or the filtered list.
 */
interface IAdapterFilterable<ITEM: IItemBase>: IAdapterBase<ITEM>, Filterable
{
    /**
     * called when filtering the adapter
     */
    val onFilterRequest: onFilterRequest<ITEM>
    /**
     * convenience function instead of calling [getFilter().filter(constraint)].
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter() = mDxFilter
}