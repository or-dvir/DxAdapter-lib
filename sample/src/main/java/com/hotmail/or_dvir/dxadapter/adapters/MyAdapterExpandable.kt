package com.hotmail.or_dvir.dxadapter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.interfaces.IDxExpandable
import com.hotmail.or_dvir.dxadapter.interfaces.IDxSelectable
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.my_item_expandable.view.*

//this is essentially the same as MyMultiTypeAdapter where one of the types is a header.
//see notes for item/viewHolder type in MyMultiTypeAdapter class
class MyAdapterExpandable(private val mItems: MutableList<MyItemExpandable>,
                          override var onItemExpandStateChanged: onItemExpandStateChangedListener<MyItemExpandable>? = null)
    : DxAdapter<MyItemExpandable, MyAdapterExpandable.ViewHolder>(mItems),
      IDxExpandable<MyItemExpandable>,
      IDxSelectable<MyItemExpandable>
{
    override val defaultItemSelectionBehavior = true
    override val triggerClickListenersInSelectionMode = false
    override val onItemSelectionChanged: onItemSelectStateChangedListener<MyItemExpandable>? = null
    //setting this to null means accent color will be used
    override val selectedItemBackgroundColor: Int? = null
    override val onlyOneItemExpanded = false

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        holder.apply {
            tv.text = item.mText
            et.setText(item.mSubText)
            cb.isChecked = item.isDone

            if(item.mIsExpanded)
                iv.animate().rotation(180f).start()
            else
                iv.animate().rotation(0f).start()
        }
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        holder.apply {
            tv.text = ""
            //note:
            //do NOT set mText for the edit mText here because it would trigger the mText changed listener
            cb.isChecked = false
        }
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item_expandable
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

    inner class ViewHolder(itemView: View): RecyclerViewHolder(itemView)
    {
        val tv = itemView.tv
        val iv = itemView.iv
        val et = itemView.et
        val cb = itemView.cb

        init
        {
            //optionally setting iv as expand/collapse handle.
            //don't forget to set expandAndCollapseOnItemClick to false in your expandable item
            //(otherwise there is no point to the handle because any click on the item
            //would collapse/expand)

//            iv.setOnItemClick {
//                if(mItems[adapterPosition].mIsExpanded)
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
                    s?.apply { mItems[adapterPosition].mSubText = toString() }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                }
            })
        }
    }
}