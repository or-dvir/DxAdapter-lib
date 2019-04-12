package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hotmail.or_dvir.dxadapter.DxAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItem
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.RecyclerViewHolder
import com.hotmail.or_dvir.dxadapter.interfaces.IDxSelectable
import com.hotmail.or_dvir.dxadapter.onItemSelectStateChangedListener
import kotlinx.android.synthetic.main.my_item.view.*

class MyAdapter(mItems: MutableList<MyItem>,
                override val onItemSelectionChanged: onItemSelectStateChangedListener<MyItem>? = null)
    : DxAdapter<MyItem, MyAdapter.ViewHolder>(mItems),
      IDxSelectable<MyItem>
{
    override val defaultItemSelectionBehavior = true
    override val triggerClickListenersInSelectionMode = true
    //setting this to null means accent color will be used
    override val selectedItemBackgroundColor: Int? = null

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItem)
    {
        holder.tv.text = item.mText
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItem)
    {
        holder.tv.text = ""
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item
    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int) =
        ViewHolder(itemView)

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    class ViewHolder(itemView: View): RecyclerViewHolder(itemView)
    {
        val tv: TextView = itemView.tv
    }
}