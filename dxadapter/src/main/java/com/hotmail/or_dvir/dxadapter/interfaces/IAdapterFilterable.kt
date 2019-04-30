package com.hotmail.or_dvir.dxadapter.interfaces

import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.onFilterRequest

/**
 * implement this interface in your ADAPTER if you wish for IT to support filtering
 */
interface IAdapterFilterable<ITEM: IItemBase>: IAdapterBase<ITEM>, Filterable
{
    /**
     * called when a filtering the adapter
     */
    val onFilterRequest: onFilterRequest<ITEM>
    /**
     * convenience function instead of calling [getFilter().filter(constraint)].
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter() = mDxFilter
}