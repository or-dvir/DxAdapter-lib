package com.hotmail.or_dvir.dxadapter.interfaces

import android.content.Context
import android.support.annotation.ColorInt
import android.util.TypedValue

interface IAdapterBase<ITEM: IItemBase>
{
    val mAdapterItems: List<ITEM>
    //todo note that this list could have variables that are -1 (result of indexOf() function)
    fun getIndicesForItems(items: List<ITEM>) = items.map { mAdapterItems.indexOf(it) }

    fun isInBounds(position: Int) = position in (0 until mAdapterItems.size)

    fun dxNotifyItemChanged(position: Int)

    @ColorInt
    fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }
}
