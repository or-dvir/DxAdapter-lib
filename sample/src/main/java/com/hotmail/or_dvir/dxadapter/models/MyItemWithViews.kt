package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "")
    : DxItem/*<MyItemWithViews.DefaultViewHolder>*/()
{
    override fun getViewType() = R.id.itemType_MyItemWithViews
//    override fun getItemType() = R.id.itemType_MyItemWithViews
//    override fun createViewHolder(itemView: View) = DefaultViewHolder(/*this, */itemView)
//    override fun getLayoutRes() = R.layout.my_item_with_views

//    override fun bindViewHolder(holder: RecyclerViewHolder)
//    {
//        holder.itemView.apply {
//            mySwitch.isChecked = isSwitchOn
//            myCheckBox.isChecked = isBoxChecked
//            myEditText.setText(mText)
//        }
//    }

//    override fun unbindViewHolder(holder: RecyclerViewHolder)
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
//    //when this DefaultViewHolder is being recycled.
//    //if you need to communicate some changes back to the outer object,
//    //ljhgvkgtgvjlkdsfgblkgsdflngsdlfnsgdlfnkljsgdfnjklgsfnkljnkjnkj
//    class DefaultViewHolder(itemView: View,
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