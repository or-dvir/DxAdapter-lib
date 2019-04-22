package com.hotmail.or_dvir.dxadapter.interfaces

import android.widget.Filter
import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.dxFilter

interface IAdapterFilterable<ITEM: IItemBase>: IAdapterBase<ITEM>, Filterable
{
    var mdxFilter: dxFilter<ITEM>

    /**
     * convenience method instead of calling [getFilter().filter(constraint)].
     *
     * Note: if your adapter doesn't implement IAdapterFilterable, this function does nothing
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter(): Filter
    {

    }
}