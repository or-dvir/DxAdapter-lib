package com.hotmail.or_dvir.dxadapter.adapters

import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.DxAdapter
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.RecyclerViewHolder
import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
import kotlinx.android.synthetic.main.my_item_image_vertical.view.*

class MyAdapterHorizontal(mItems: MutableList<MyItemWithImage>):
    DxAdapter<MyItemWithImage, MyAdapterHorizontal.ViewHolder>(mItems)
{
    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItemWithImage)
    {
        holder.iv.setImageResource(item.imageRes)
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItemWithImage)
    {
        holder.iv.setImageDrawable(null)
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item_image_horizontal
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
        val iv = itemView.iv
    }
}