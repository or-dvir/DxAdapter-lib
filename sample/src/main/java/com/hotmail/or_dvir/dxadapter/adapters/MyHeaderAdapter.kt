package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hotmail.or_dvir.dxadapter.DxAdapter
import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.RecyclerViewHolder
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.my_header.view.*

//this is essentially the same as MyMultiTypeAdapter where one of the types is a header
class MyHeaderAdapter(mItems: MutableList<DxItem>)
    : DxAdapter<DxItem, RecyclerViewHolder>(mItems)
{
    override fun bindViewHolder(holder: RecyclerViewHolder, position: Int, item: DxItem)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = item.mText

            is MyHeader ->
                do something
        }
    }

    override fun unbindViewHolder(holder: RecyclerViewHolder, position: Int, item: DxItem)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = ""

            is MyHeader ->
                do something
        }
    }

    override fun getLayoutRes(parent: ViewGroup, viewType: Int): Int
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> R.layout.my_item
            R.id.itemType_MyHeader -> R.layout.my_header
            else -> 0 //just for the compiler. handle this however you see fit
        }
    }

    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int): RecyclerViewHolder
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> MyAdapter.ViewHolder(itemView)
            R.id.itemType_MyHeader -> ViewHolderHeader(itemView)
            else -> MyMultiTypeAdapter.DefaultViewHolder(itemView) //just for the compiler. handle this however you see fit
        }
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    class ViewHolderHeader(itemView: View): RecyclerViewHolder(itemView)
    {
        val tv: TextView = itemView.tv
    }
}