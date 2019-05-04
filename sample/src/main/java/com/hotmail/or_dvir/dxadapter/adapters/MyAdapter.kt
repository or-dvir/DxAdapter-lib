package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterFilterable
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterSelectable
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.my_item.view.*

class MyAdapter(mItems: MutableList<MyItem>,
                override val onItemSelectionChanged: onItemSelectStateChangedListener<MyItem>,
                override var onItemClick: onItemClickListener<MyItem>?,
                override var onItemLongClick: onItemLongClickListener<MyItem>?)
    : DxAdapter<MyItem, MyAdapter.ViewHolder>(mItems),
      IAdapterSelectable<MyItem>,
      IAdapterFilterable<MyItem>
{
    override val onFilterRequest: onFilterRequest<MyItem> = { constraint ->
        mItems.filter { it.mText.startsWith(constraint.trim(), true) }
    }

    override val defaultItemSelectionBehavior = true
    override val triggerClickListenersInSelectionMode = false
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

    class ViewHolder(itemView: View): DxHolder(itemView)
    {
        val tv: TextView = itemView.tv
    }
}