package com.hotmail.or_dvir.dxadapter.interfaces

import android.view.View
import androidx.annotation.LayoutRes

/**
 * implement this interface in your ADAPTER if you wish for it to support sticky headers.
 *
 * note that header items are actual items in the adapter!
 * this means you need to be careful with certain actions taken on the adapter.
 *
 * for example: if you filter the adapter with some constraint, it's up to you whether or not to include
 * header items in the filtered results.
 *
 * another example: sorting your adapter items will also affect headers.
 */
interface IAdapterStickyHeader
{
    /**
     * returns the resource id of the header layout
     */
    //todo add support for multiple types of headers- in order to do this you need to
    // add position parameter!!!
    // note that this might require some changes in the adapter!!!
    @LayoutRes
    fun getHeaderLayoutRes(/*headerPosition: Int*/): Int

    /**
     * binds data to the STICKY header view (NOT the header item in the adapter).
     * @param stickyHeader the sticky header view (the one drawn at the top)
     * @param headerAdapterPosition position of the header item IN THE ADAPTER (use this to get data to bind from the adapter).
     */
    fun bindStickyHeader(stickyHeader: View, headerAdapterPosition: Int)

    /**
     * returns whether or not the item at [adapterPosition] represents a header
     */
    fun isHeader(adapterPosition: Int): Boolean
}