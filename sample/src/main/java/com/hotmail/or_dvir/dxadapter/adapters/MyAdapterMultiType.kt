package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.models.MyItem
import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
import kotlinx.android.synthetic.main.my_item_image_vertical.view.*

//for multi-type adapter, the item type and view holder type need to be shared
//between all supported items of this adapter. i use IItemBase and DxHolder
//for convenience, but it can be any type that the items share
class MyAdapterMultiType(mItems: MutableList<IItemBase>)
    : DxAdapter<IItemBase, DxHolder>(mItems)
{
    override val onItemClick: onItemClickListener<IItemBase>? = null
    override val onItemLongClick: onItemLongClickListener<IItemBase>? = null

    override fun bindViewHolder(holder: DxHolder, position: Int, item: IItemBase)
    {
        when (item)
        {
            is MyItem ->
                (holder as MyAdapter.ViewHolder).tv.text = item.mText

            is MyItemWithImage ->
                (holder as ViewHolderWithImage).iv.setImageResource(item.imageRes)
        }
    }

    override fun unbindViewHolder(holder: DxHolder, position: Int, item: IItemBase)
    {
        if(item is MyItemWithImage)
            (holder as ViewHolderWithImage).iv.setImageDrawable(null)
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int): Int
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> R.layout.my_item
            R.id.itemType_MyItemWithImage -> R.layout.my_item_image_vertical
            else -> 0 //just for the compiler. handle this however you see fit
        }
    }

    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int): DxHolder
    {
        return when (viewType)
        {
            R.id.itemType_MyItem -> MyAdapter.ViewHolder(itemView)
            R.id.itemType_MyItemWithImage -> ViewHolderWithImage(itemView)
            else -> DefaultViewHolder(itemView) //just for the compiler. handle this however you see fit
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

    class ViewHolderWithImage(itemView: View): DxHolder(itemView)
    {
        val iv: ImageView = itemView.iv
    }

    //only need this for returning a default view holder from createAdapterViewHolder()
    class DefaultViewHolder(itemView: View): DxHolder(itemView)
}