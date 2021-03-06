package com.hotmail.or_dvir.dxadapter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.models.MyItemWithViews
import kotlinx.android.synthetic.main.my_item_with_views.view.*

class MyAdapterInnerViews(val mItems: MutableList<MyItemWithViews>)
    : DxAdapter<MyItemWithViews, MyAdapterInnerViews.ViewHolder>()
{
    override val onItemClick: onItemClickListener<MyItemWithViews>? = null
    override val onItemLongClick: onItemLongClickListener<MyItemWithViews>? = null

    override fun getOriginalAdapterItems() = mItems

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: MyItemWithViews)
    {
        holder.apply {
            switch.isChecked = item.isSwitchOn
            checkBox.isChecked = item.isBoxChecked
            editText.setText(item.mText)
        }
    }

    override fun unbindViewHolder(holder: ViewHolder, position: Int, item: MyItemWithViews)
    {
        //if we don't do this here, recycling of the the edit text will not
        //work properly
        holder.editText.setText("")
    }

    override fun getItemLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item_with_views

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

    //made the class inner so i can have easy access to item properties
    //to be used inside the listeners
    inner class ViewHolder(itemView: View) : DxHolder(itemView)
    {
        val switch: Switch = itemView.mySwitch
        val checkBox: CheckBox = itemView.myCheckBox
        val editText: EditText = itemView.myEditText
        private val button: Button = itemView.myButton

        init
        {
            button.setOnClickListener {
                mItems[adapterPosition].mText = "button!"
                notifyItemChanged(adapterPosition)
            }

            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            switch.setOnClickListener {
                mItems[adapterPosition].isSwitchOn = switch.isChecked
            }
            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            checkBox.setOnClickListener {
                mItems[adapterPosition].isBoxChecked = checkBox.isChecked
            }

            editText.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(s: Editable?)
                { /*do nothing*/ }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
                    s?.apply { mItems[adapterPosition].mText = toString() }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                { /*do nothing*/ }
            })
        }
    }
}
