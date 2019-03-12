package com.hotmail.or_dvir.dxadapter

import android.support.annotation.LayoutRes
import android.view.View

//internal interface IDxItem/*<VH: RecyclerViewHolder>*/
//{
//    @LayoutRes
//    fun getItemLayoutRes(): Int
//    fun createViewHolder(/*item: ITEM, */itemView: View): VH
//    fun bindViewHolder(holder: VH)
//    /**
//     * create a files called "ids.xml" inside your res/values folder
//     * and use return values from there.
//     * this MUST be a resource id in order prevent accidental bugs with duplicate ids
//     * @return a unique id to identify the type of this item.
//     */
//    @IdRes
//    fun getItemType(): Int
//
//    /**
//     * here you should cancel any long running operations
//     * or expensive resources related to this item and its' views.
//     * e.g. loading images from the internet, performing long calculations, clearing image views
//     */
//    fun unbindViewHolder(holder: RecyclerViewHolder)
////    fun unbindViewHolder(holder: VH)
//}

interface IDxStickyHeader
{
//    /**
//     * @param position the adapter position of the item for which we need to find its' header
//     * @return the adapter position of the header which "hosts" the item at [position].
//     */
//    fun getHeaderPositionFromItemPosition(position: Int): Int

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

interface IOnExpandedStateChanged<ITEM>
{
    fun onExpanded(position: Int, item: ITEM): Any
    fun onCollapsed(position: Int, item: ITEM): Any
}

interface IOnAdapterItemVisibilityChanged
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
