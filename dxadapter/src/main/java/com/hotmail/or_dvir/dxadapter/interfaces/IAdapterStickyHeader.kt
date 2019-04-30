package com.hotmail.or_dvir.dxadapter.interfaces

import android.support.annotation.LayoutRes
import android.view.View

/**
 * implement this interface if you wish for your adapter to support sticky headers.
 *
 * note that header items are actual items in the adapter!
 * this means you need to be careful with certain actions taken on the adapter.
 *
 * for example: if you filter the adapter with some constraint, it's up to you whether to include
 * header items or not in the filtered results.
 */
interface IDxStickyHeader
{
    /**
     * returns the resource id of the header layout
     */
    //todo in order to allow multiple types of headers, add position parameter!!!
    // note that this might require some changes in the adapter!!!
    @LayoutRes
    fun getHeaderLayoutRes(/*headerPosition: Int*/): Int

    /**
     * binds data to the STICKY header view (NOT the header item in the adapter).
     * @param headerAdapterPosition position of the header item IN THE ADAPTER (use this to get data to bind from the adapter).
     */
    fun bindStickyHeader(stickyHeader: View, headerAdapterPosition: Int)

    /**
     * returns whether or not the item [adapterPosition] represents a header
     */
    fun isHeader(adapterPosition: Int): Boolean
}