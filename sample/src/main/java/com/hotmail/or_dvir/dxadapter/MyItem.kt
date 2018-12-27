package com.hotmail.or_dvir.dxadapter

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.my_item.view.*

class MyItem(val mText: String): DxItem<MyItem.ViewHolder>()
//class MyItem(val mText: String): DxItem<SimpleViewHolder>()
{
    override fun getLayoutRes() = R.layout.my_item
    override fun createViewHolder(itemView: View) = ViewHolder(itemView)
//    override fun createViewHolder(itemView: View) = SimpleViewHolder(itemView)

    override fun bindViewHolder(holder: ViewHolder) { holder.tv.text = mText }
    override fun unbindViewHolder(holder: ViewHolder) { holder.itemView.tv.text = "" }
//    override fun bindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.text = mText }
//    override fun unbindViewHolder(holder: SimpleViewHolder) { holder.itemView.tv.text = "" }

    class ViewHolder(itemView: View): RecyclerViewHolder(itemView)
    {
        val tv: TextView = itemView.tv
    }
}