package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R

class MyHeader(var mText: String): DxItem/*<MyItem.DefaultViewHolder>*/()
{
    override fun isDraggable() = false

    override fun getViewType() = R.id.itemType_MyHeader
//    override fun getItemType() = R.id.itemType_MyItem
//    override fun getItemLayoutRes() = R.layout.my_item
//    override fun createViewHolder(itemView: View) = DefaultViewHolder(itemView)
//    override fun createViewHolder(itemView: View) = SimpleViewHolder(itemView)
//    override fun getItemLayoutRes() = R.layout.my_item
//    override fun bindViewHolder(holder: RecyclerViewHolder) { holder.itemView.tv.text = mText }
//    override fun bindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.text = mText }
//    override fun unbindViewHolder(holder: RecyclerViewHolder) { holder.itemView.tv.text = "" }
//    override fun unbindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.text = "" }

//    class DefaultViewHolder(itemView: View): RecyclerViewHolder(itemView)
//    {
//        val tv: TextView = itemView.tv
//    }
}