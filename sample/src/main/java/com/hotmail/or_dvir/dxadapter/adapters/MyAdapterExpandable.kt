package com.hotmail.or_dvir.dxadapter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.hotmail.or_dvir.dxadapter.DxAdapter
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.RecyclerViewHolder
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.my_item_expandable.view.*

//this is essentially the same as MyMultiTypeAdapter where one of the types is a header.
//see notes for item/viewHolder type in MyMultiTypeAdapter class
class MyAdapterExpandable(private val mItems: MutableList<MyItemExpandable>)
    : DxAdapter<MyItemExpandable, MyAdapterExpandable.ViewHolder>(mItems)
{
    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        holder.apply {
            tv.text = item.mText
            et.setText(item.mSubText)
            cb.isChecked = item.isDone
            group.visibility =
                    if(item.mIsExpanded)
                        View.VISIBLE
                    else
                        View.GONE
        }
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItemExpandable)
    {
        holder.apply {
            tv.text = ""
            et.setText("")
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
        val group = itemView.expandableGroup

        //todo flip the handle 180 degrees when expanded/collapsed

        init
        {
            itemView.setOnClickListener {
                mItems[adapterPosition].apply {
                    isExpanded = !isExpanded

                    group.visibility =
                        if(isExpanded)
                            View.VISIBLE
                        else
                            View.GONE

                    notifyItemChanged(adapterPosition)
                }
            }

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

                //todo figure this out before releasing!!!!!
                //for some reason this only works when put inside this function
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
//                    s?.apply { mItems[adapterPosition].mText = toString() }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                }
            })

        }
    }
}