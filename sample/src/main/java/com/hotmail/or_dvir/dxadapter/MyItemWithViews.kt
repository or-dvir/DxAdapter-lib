package com.hotmail.or_dvir.dxadapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.my_item_with_views.view.*
import org.greenrobot.eventbus.EventBus

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "asdfgsdfg")
    : DxItem<SimpleViewHolder>()
{
//    private var isSwitchOn = false
//    private var isBoxChecked = false
//    private var mText = ""

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)
    override fun getLayoutRes() = R.layout.my_item_with_views

    override fun bindViewHolder(holder: SimpleViewHolder)
    {
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

    /*inner*/ class ViewHolder(itemView: View): SimpleViewHolder(itemView)
    {
        init
        {
            //NOTE:
            //not using onCheckedChanged because it is being triggered also when the item gets out of view
            itemView.apply {
                mySwitch.setOnClickListener {
//                    EventBus.getDefault().post(SwitchEvent(mySwitch.isChecked, adapterPosition))
                    (tag as List<MyItemWithViews>)[adapterPosition]
                    isSwitchOn = mySwitch.isChecked
//                    Log.i("aaaaa", "checkkkkkkkkkkkkkkkkkkkkkkkk $isChecked $adapterPosition")
                }

                myCheckBox.setOnClickListener {
//                    isBoxChecked = isChecked
                }

//                myEditText.addTextChangedListener(object : TextWatcher
//                {
//                    override fun afterTextChanged(s: Editable?)
//                    {
//                        s?.apply {
//                            mText = toString()
//                            Log.i("aaaaa", "texttttttttttttttttttttttt $mText $adapterPosition")
//                        }
//                    }
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//                })
            }
        }
    }
}