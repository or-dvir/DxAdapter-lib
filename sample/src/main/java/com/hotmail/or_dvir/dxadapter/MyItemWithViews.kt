package com.hotmail.or_dvir.dxadapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.my_item_with_views.view.*

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "")
    : DxItem<MyItemWithViews.ViewHolder>()
{
    override fun getItemType() = R.id.itemType_MyItemWithViews
    override fun createViewHolder(itemView: View) = ViewHolder(/*this, */itemView)
    override fun getLayoutRes() = R.layout.my_item_with_views

    override fun bindViewHolder(holder: ViewHolder)
    {
        holder.apply {
            switch.isChecked = isSwitchOn
            checkBox.isChecked = isBoxChecked
            editText.setText(mText)
        }
    }

    override fun unbindViewHolder(holder: ViewHolder)
    {
        holder.apply {
            switch.isChecked = false
            checkBox.isChecked = false
            editText.setText("")
        }
    }

    //WARNING!!!!
    //do NOT make this an inner class!
    //inner classes store a reference to the outer class and this can cause unexpected behavior
    //when this item is being recycled.
    class ViewHolder(/*val model: MyItemWithViews,*/ itemView: View): RecyclerViewHolder(itemView)
    {
        val button = itemView.myButton
        val switch = itemView.mySwitch
        val checkBox = itemView.myCheckBox
        val editText = itemView.myEditText

        init
        {
            val adapter = itemView.tag as DxAdapter<*, *>

            button.setOnClickListener {
                val item = adapter.mItems[adapterPosition] as MyItemWithViews
                item.mText = "button!"
                adapter.notifyItemChanged(adapterPosition)
            }

            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            switch.setOnClickListener {
                val item = adapter.mItems[adapterPosition] as MyItemWithViews
                item.isSwitchOn = switch.isChecked
            }

            checkBox.setOnClickListener {
                val item = adapter.mItems[adapterPosition] as MyItemWithViews
                item.isBoxChecked = checkBox.isChecked
            }

            editText.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(s: Editable?)
                {
                    if(adapterPosition == 0)
                        Log.i("textwatcher", "after ${s?.toString()}")
                    val item = adapter.mItems[adapterPosition] as MyItemWithViews
//                    s?.apply {
//                        item.mText = toString()
//                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
                    if(adapterPosition == 0)
                        Log.i("textwatcher", "before ${s?.toString()}")
                    val item = adapter.mItems[adapterPosition] as MyItemWithViews
                    s?.apply {
                        item.mText = toString()
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                    if(adapterPosition == 0)
                        Log.i("textwatcher", "on ${s?.toString()}")
                    val item = adapter.mItems[adapterPosition] as MyItemWithViews
//                    s?.apply {
//                        item.mText = toString()
//                    }
                }
            })
        }
    }
}