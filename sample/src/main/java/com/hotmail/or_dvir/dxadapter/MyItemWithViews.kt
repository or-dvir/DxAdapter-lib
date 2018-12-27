package com.hotmail.or_dvir.dxadapter

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import kotlinx.android.synthetic.main.my_item_with_views.view.*

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "asdfgsdfg")
    : DxItem()/*<MyItemWithViews.MyItemWithViewsViewHolder>()*/
{
//    override fun createViewHolder(itemView: View): RecyclerViewHolder
//    {
//        itemView.apply {
//
//            myButton.setOnClickListener {
//                mText
//            }
//
//
//            return ViewHolder(
//                this,
//                myButton,
//                mySwitch,
//                myCheckBox,
//                myEditText
//            )
//        }
//    }

//    override fun getLayoutRes() = R.layout.my_item_with_views

//    override fun bindViewHolder(holder: RecyclerViewHolder)
////    override fun bindViewHolder(holder: MyItemWithViews.MyItemWithViewsViewHolder)
//    {
//        holder.itemView.apply {
//            mySwitch.isChecked = isSwitchOn
//            myCheckBox.isChecked = isBoxChecked
//            myEditText.setText(mText)
//        }
//    }
//
//    override fun unbindViewHolder(holder: RecyclerViewHolder)
////    override fun unbindViewHolder(holder: MyItemWithViews.MyItemWithViewsViewHolder)
//    {
//        holder.itemView.apply {
//            mySwitch.isChecked = false
//            myCheckBox.isChecked = false
//            myEditText.setText("")
//        }
//    }

//    //WARNING!!!!
//    //do NOT make this an inner class!
//    //inner classes store a reference to the outer class and this can cause unexpected behavior
//    //when this MyItemWithViewsViewHolder is being recycled.
//    //if you need to communicate some changes back to the outer object,
//    //ljhgvkgtgvjlkdsfgblkgsdflngsdlfnsgdlfnkljsgdfnjklgsfnkljnkjnkj
//    class ViewHolder(itemView: View,
//                     button: Button,
//                     switch: Switch,
//                     checkBox: CheckBox,
//                     editText: EditText): RecyclerViewHolder(itemView)
//    {
//
//
//
////        init
////        {
////
////            //NOTE:
////            //not using onCheckedChanged because it is being triggered also when the item gets out of view
////            itemView.apply {
////                mySwitch.setOnClickListener {
//////                    EventBus.getDefault().post(SwitchEvent(mySwitch.isChecked, adapterPosition))
//////                    isSwitchOn = mySwitch.isChecked
//////                    Log.i("aaaaa", "checkkkkkkkkkkkkkkkkkkkkkkkk $isChecked $adapterPosition")
////                }
////
////                myCheckBox.setOnClickListener {
//////                    isBoxChecked = isChecked
////                }
////
//////                myEditText.addTextChangedListener(object : TextWatcher
//////                {
//////                    override fun afterTextChanged(s: Editable?)
//////                    {
//////                        s?.apply {
//////                            mText = toString()
//////                            Log.i("aaaaa", "texttttttttttttttttttttttt $mText $adapterPosition")
//////                        }
//////                    }
//////
//////                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//////                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//////                })
////            }
////        }
//    }
}