package com.hotmail.or_dvir.dxadapter

import android.view.View
import kotlinx.android.synthetic.main.my_item.view.*

class MyItem(val mText: String): IDxItem<SimpleViewHolder>
{
    override fun createViewHolder(itemView: View) = SimpleViewHolder(itemView)
    override fun getLayoutRes() = R.layout.my_item

    override fun bindViewHolder(holder: SimpleViewHolder)
    {
        holder.itemView.tv.text = mText
    }

    override fun unbindViewHolder(holder: SimpleViewHolder)
    {
        holder.itemView.tv.text = ""
    }
}