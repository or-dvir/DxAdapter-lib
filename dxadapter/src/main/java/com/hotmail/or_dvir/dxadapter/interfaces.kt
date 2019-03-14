package com.hotmail.or_dvir.dxadapter

import android.support.annotation.LayoutRes
import android.view.View

interface IDxStickyHeader
{
    /**
     * @return the resource id of the header layout for the given position
     */
    //todo in order to allow multiple types of headers, add position parameter!!!
    //todo note that this might require some changes in the adapter!!!
    @LayoutRes
    fun getHeaderLayoutRes(/*headerPosition: Int*/): Int

    /**
     * binds data to the STICKY header view (NOT the header item in the adapter).
     * @param headerAdapterPosition position of the header item IN THE ADAPTER (use this to get data to bind from the adapter).
     */
    fun bindStickyHeader(stickyHeader: View, headerAdapterPosition: Int)

    /**
     * @return whether or not the item [adapterPosition] represents a header
     */
    fun isHeader(adapterPosition: Int): Boolean
}

interface IOnItemVisibilityChanged
{
    /**
     * triggers when this item becomes PARTIALLY visible
     */
    fun onVisible(): Any
    /**
     * triggers when this item becomes COMPLETELY invisible
     */
    fun onInvisible(): Any
}
