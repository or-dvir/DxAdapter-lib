package com.hotmail.or_dvir.dxadapter

import android.view.View

class MyItemWithViews: DxItem<SimpleViewHolder>()
{
    override fun createViewHolder(itemView: View) = SimpleViewHolder(itemView)
    override fun getLayoutRes() = R.layout.my_item_with_views
    override fun bindViewHolder(holder: SimpleViewHolder) {}
    override fun unbindViewHolder(holder: SimpleViewHolder) {}
}