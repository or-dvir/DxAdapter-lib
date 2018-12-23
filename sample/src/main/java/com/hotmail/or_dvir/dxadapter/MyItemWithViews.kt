package com.hotmail.or_dvir.dxadapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.my_item_with_views.view.*

class MyItemWithViews: DxItem<SimpleViewHolder>()
{
    private var isSwitchOn = false
    private var isBoxChecked = false
    private var mText = ""

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)
    override fun getLayoutRes() = R.layout.my_item_with_views

    override fun bindViewHolder(holder: SimpleViewHolder)
    {
        BUG BUG BUG BUG
                recycler view does not save previous state!!!! (text/isChecked etc...)


        holder.itemView.apply {
            mySwitch.isChecked = isSwitchOn
            myCheckBox.isChecked = isBoxChecked
            myEditText.setText(mText)
        }
    }

    override fun unbindViewHolder(holder: SimpleViewHolder)
    {
        holder.itemView.apply {
            mySwitch.isChecked = false
            myCheckBox.isChecked = false
            myEditText.setText("")
        }
    }

    inner class ViewHolder(itemView: View): SimpleViewHolder(itemView)
    {
        init
        {
            itemView.apply {
                mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    isSwitchOn = isChecked
                }

                myCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    isBoxChecked = isChecked
                }

                myEditText.addTextChangedListener(object : TextWatcher
                {
                    override fun afterTextChanged(s: Editable?)
                    {
                        s?.apply { mText = toString() }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            }
        }
    }
}