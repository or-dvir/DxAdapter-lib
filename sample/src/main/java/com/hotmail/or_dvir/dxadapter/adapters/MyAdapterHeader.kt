package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterStickyHeader
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.my_header.view.*

//this is essentially the same as MyAdapterMultiType where one of the types is a header.
//see notes for item/viewHolder type in MyAdapterMultiType class
class MyAdapterHeader(private val mItems: MutableList<IItemBase>)
    : DxAdapter<IItemBase, DxHolder>(),
      IAdapterStickyHeader,
      IAdapterSelectable<IItemBase>
{
    override val onItemClick: onItemClickListener<IItemBase>? = null
    override val onItemLongClick: onItemLongClickListener<IItemBase>? = null
    override val defaultItemSelectionBehavior = true
    override val triggerClickListenersInSelectionMode = false
    override val onItemSelectionChanged: onItemSelectStateChangedListener<IItemBase> = { _, _, _ -> /*do nothing*/ }
    override val selectedItemBackgroundColor: Int? = null

    override fun getOriginalAdapterItems() = mItems

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

    override fun bindViewHolder(holder: DxHolder, position: Int, item: IItemBase)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = item.mText

            is MyHeader ->
                bindHeader((holder as ViewHolderHeader).tv, item)
        }
    }

    override fun unbindViewHolder(holder: DxHolder, position: Int, item: IItemBase)
    {
        //no operations to stop here
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
                                         viewType: Int): DxHolder
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> MyAdapter.ViewHolder(itemView) //using the same adapter for convenience
            R.id.itemType_MyHeader -> ViewHolderHeader(itemView)
            else -> MyAdapterMultiType.DefaultViewHolder(itemView) //just for the compiler. handle this however you see fit
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

    class ViewHolderHeader(itemView: View): DxHolder(itemView)
    {
        val tv: TextView = itemView.tv
    }
}