package com.hotmail.or_dvir.dxadapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.my_item_with_views.view.*

class MyAdapter(val mItems: List<MyItemWithViews>): DxAdapter<MyItemWithViews>(mItems)
{
    override fun createAdapterViewHolder(itemView: View,
                                         parent: ViewGroup,
                                         viewType: Int) = MyItemWithViewsViewHolder(itemView)

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int)
    {
        super.onBindViewHolder(holder, position)

        print("")
    }



    inner class MyItemWithViewsViewHolder(itemView: View): SimpleViewHolder(itemView)
    {
        init
        {
            val item = mItems[adapterPosition]

            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            itemView.apply {
                mySwitch.setOnClickListener {
                    item.isSwitchOn = mySwitch.isChecked
                }

                myCheckBox.setOnClickListener {
                    item.isBoxChecked = myCheckBox.isChecked
                }

                myEditText.addTextChangedListener(object : TextWatcher
                {
                    override fun afterTextChanged(s: Editable?)
                    {
                        s?.apply { item.mText = toString() }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            }
        }
    }
}