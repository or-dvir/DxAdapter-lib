package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R

class MyHeader(var mText: String): DxItem/*<MyItem.DefaultViewHolder>*/()
{
    //all of these are TRUE by default.
    //set here to FALSE for demonstration: headers should not be interactable
    override fun isDraggable() = false
    override fun isSelectable() = false
    override fun isSwipeable() = false

    override fun getViewType() = R.id.itemType_MyHeader
//    override fun getItemType() = R.id.itemType_MyItem
//    override fun getItemLayoutRes() = R.layout.my_item
//    override fun createViewHolder(itemView: View) = DefaultViewHolder(itemView)
//    override fun createViewHolder(itemView: View) = SimpleViewHolder(itemView)
//    override fun getItemLayoutRes() = R.layout.my_item
//    override fun bindViewHolder(holder: RecyclerViewHolder) { holder.itemView.tv.mText = mText }
//    override fun bindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.mText = mText }
//    override fun unbindViewHolder(holder: RecyclerViewHolder) { holder.itemView.tv.mText = "" }
//    override fun unbindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.mText = "" }

//    class DefaultViewHolder(itemView: View): RecyclerViewHolder(itemView)
//    {
//        val tv: TextView = itemView.tv
//    }
}