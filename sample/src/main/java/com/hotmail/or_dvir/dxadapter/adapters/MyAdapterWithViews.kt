package com.hotmail.or_dvir.dxadapter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import com.hotmail.or_dvir.dxadapter.DxAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItemWithViews
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.RecyclerViewHolder
import kotlinx.android.synthetic.main.my_item_with_views.view.*

class MyAdapterWithViews(val mItems: List<MyItemWithViews>): DxAdapter<MyItemWithViews, MyAdapterWithViews.ViewHolder>(mItems)
{
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
        holder.apply {
            switch.isChecked = item.isSwitchOn
            checkBox.isChecked = item.isBoxChecked
            editText.setText(item.mText)
        }
    }

    override fun getLayoutRes(parent: ViewGroup, viewType: Int) =
        R.layout.my_item_with_views

    override fun getItemViewType(position: Int): Int
    {
        return super.getItemViewType(position)
    }

    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int) = ViewHolder(itemView)

    inner class ViewHolder(itemView: View) : RecyclerViewHolder(itemView)
    {
        val switch: Switch = itemView.mySwitch
        val checkBox: CheckBox = itemView.myCheckBox
        val editText: EditText = itemView.myEditText
        val button: Button = itemView.myButton

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

            checkBox.setOnClickListener {
                mItems[adapterPosition].isBoxChecked = checkBox.isChecked
            }

            editText.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(s: Editable?)
                {
                }

                //todo figure this out before releasing!!!!!
                //for some reason this only works when put inside this function
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
                    s?.apply { mItems[adapterPosition].mText = toString() }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                }
            })
        }
    }
}
