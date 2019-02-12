package com.hotmail.or_dvir.dxadapter.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.my_header.view.*

//this is essentially the same as MyMultiTypeAdapter where one of the types is a header.
//see notes for item/viewHolder type in MyMultiTypeAdapter class
class MyHeaderAdapter(private val mItems: MutableList<DxItem>)
    : DxAdapter<DxItem, RecyclerViewHolder>(mItems),
    IDxStickyHeader
{
    //convenience method so that the binding logic of a header view
    //is done in a single method (removes duplicate code)
    private fun bindHeader(tv: TextView, myHeader: MyHeader)
    {
        tv.text = myHeader.mText
    }

    override fun bindStickyHeader(stickyHeader: View, headerAdapterPosition: Int) =
        bindHeader(stickyHeader.tv, mItems[headerAdapterPosition] as MyHeader)

    override fun isHeader(adapterPosition: Int) =
        mItems[adapterPosition].getViewType() == R.id.itemType_MyHeader

    override fun getHeaderLayoutRes() = R.layout.my_header

    override fun bindViewHolder(holder: RecyclerViewHolder, position: Int, item: DxItem)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = item.mText

            is MyHeader ->
                bindHeader((holder as ViewHolderHeader).tv, item)
        }
    }

    override fun unbindViewHolder(holder: RecyclerViewHolder, position: Int, item: DxItem)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = ""

            is MyHeader ->
                (holder as ViewHolderHeader).tv.text = ""
        }
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int): Int
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> R.layout.my_item
            R.id.itemType_MyHeader -> getHeaderLayoutRes()
            else -> 0 //just for the compiler. handle this however you see fit
        }
    }

    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int): RecyclerViewHolder
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> MyAdapter.ViewHolder(itemView) //using the same adapter for convenience
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