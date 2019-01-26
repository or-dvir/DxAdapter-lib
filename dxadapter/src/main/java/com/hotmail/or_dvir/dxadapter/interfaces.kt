package com.hotmail.or_dvir.dxadapter

import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View

//internal interface IDxItem/*<VH: RecyclerViewHolder>*/
//{
//    @LayoutRes
//    fun getLayoutRes(): Int
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
    /**
     * @param position the adapter position of the item for which we need to find its' header
     * @return the adapter position of the header which "hosts" the item at [position].
     */
    fun getHeaderPositionFromItemPosition(position: Int): Int

    /**
     * @return the resource id of the header layout
     */
    @LayoutRes
    fun getHeaderLayout(): Int

    /**
     * @param header Header to set the data on.
     * @param headerPosition Position of the header item in the adapter.
     */
    //todo change this documentation!!!
    fun bindHeaderData(header: View, headerPosition: Int)

    /**
     * This method gets called by [DxStickyHeaderItemDecoration] to verify whether the item represents a header.
     * @param itemPosition int.
     * @return true, if item at the specified adapter's position represents a header.
     */
    //todo change this documentation!!!
    fun isHeader(itemPosition: Int): Boolean
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
