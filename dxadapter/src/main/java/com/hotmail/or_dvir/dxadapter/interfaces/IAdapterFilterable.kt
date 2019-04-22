package com.hotmail.or_dvir.dxadapter.interfaces

import android.widget.Filter
import android.widget.Filterable
import com.hotmail.or_dvir.dxadapter.onFilterRequest

interface IAdapterFilterable<ITEM: IItemBase>: IAdapterBase<ITEM>, Filterable
{
    val onFilterRequest: onFilterRequest<ITEM>

    /**
     * convenience method instead of calling [getFilter().filter(constraint)].
     */
    fun filter(constraint: CharSequence) = filter.filter(constraint)

    override fun getFilter() = object : Filter()
    {
        override fun performFiltering(constraint: CharSequence?): FilterResults?
        {
            //todo how to add animation to filtering????

            val results =
                if (constraint.isNullOrEmpty())
                    mAdapterItems
                else
                //for SURE this is not null because of the "if" condition above
                    onFilterRequest.invoke(constraint)

            return FilterResults().apply {
                values = results
                count = results.size
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults)
        {
            //todo check if this note is still true
            //note:
            //cannot check for generic types in kotlin.
            //but because onFilterRequest is defined with the generic type ITEM,
            //the user will get a compiler error if they return a list of a different type
            @Suppress("UNCHECKED_CAST")
            setItems(results.values as MutableList<ITEM>)
        }
    }
}