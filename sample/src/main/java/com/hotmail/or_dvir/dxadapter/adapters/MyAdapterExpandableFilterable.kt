package com.hotmail.or_dvir.dxadapter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterExpandable
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterFilterable
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterSelectable
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.my_item_expandable.view.*

class MyAdapterExpandableFilterable(private val mItems: MutableList<MyItemExpandable>,
                                    override var onItemExpandStateChanged: onItemExpandStateChangedListener<MyItemExpandable>)
    : DxAdapter<MyItemExpandable, MyAdapterExpandableFilterable.ViewHolder>(mItems),
      IAdapterExpandable<MyItemExpandable>,
      IAdapterFilterable<MyItemExpandable>
{
    override var onItemClick: onItemClickListener<MyItemExpandable>? = null
//    override var onItemClick: onItemClickListener<MyItemExpandable> =
//    { view, adapterPosition, item ->
//        mItems.removeAt(adapterPosition)
//        notifyItemRemoved(adapterPosition)
//    }
    override var onItemLongClick: onItemLongClickListener<MyItemExpandable>? = null

    override val onFilterRequest: onFilterRequest<MyItemExpandable> = { constraint ->
        mItems.filter { it.mText.startsWith(constraint.trim(), true) }
    }

    override val onlyOneItemExpanded = false

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        holder.apply {
            tv.text = item.mText
            et.setText(item.mSubText)
            cb.isChecked = item.isDone

            if(item.isExpanded)
                iv.animate().rotation(180f).start()
            else
                iv.animate().rotation(0f).start()
        }
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        //nothing special to do here
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item_expandable
    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int) = ViewHolder(itemView)

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    inner class ViewHolder(itemView: View): DxHolder(itemView)
    {
        val tv = itemView.tv
        val iv = itemView.iv
        val et = itemView.et
        val cb = itemView.cb

        init
        {
            //optionally setting iv as expand/collapse handle.
            //don't forget to make expandCollapseOnItemClick() return false in your expandable item
            //(otherwise there is no point to the handle because any click on the item
            //would collapse/expand)
//            iv.setOnItemClick {
//                if(mItems[adapterPosition].isExpanded)
//                    collapse(adapterPosition)
//                else
//                    expand(adapterPosition)
//            }

            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            cb.setOnClickListener {
                mItems[adapterPosition].isDone = cb.isChecked
            }

            et.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(s: Editable?)
                {
                    //////////////////////////////////////////////////////
                    if(adapterPosition == 0)
                    {
                        Log.i("aaaaa", "filtered items: ${System.identityHashCode(getFilteredItems()[adapterPosition])}")
                        Log.i("aaaaa", "temp: ${System.identityHashCode(mItems[adapterPosition])}")
                    }
                    s?.apply { getFilteredItems()[adapterPosition].mSubText = toString() }
                    //////////////////////////////////////////////////////
//                    s?.apply { mItems[adapterPosition].mSubText = toString() }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                { /*do nothing*/ }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                { /*do nothing*/ }
            })
        }
    }
}