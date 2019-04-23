package com.hotmail.or_dvir.dxadapter.interfaces

import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.onFilterRequest

interface IAdapterFilterable<ITEM: IItemBase>: IAdapterBase<ITEM>, Filterable
{
    val onFilterRequest: onFilterRequest<ITEM>

    /**
     * convenience method instead of calling [getFilter().filter(constraint)].
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter() = mDxFilter
}