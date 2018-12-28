//package com.hotmail.or_dvir.dxadapter
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.EditText
//import android.widget.Switch
//import kotlinx.android.synthetic.main.my_item_with_views.view.*
//
//class MyAdapter(val mItems: List<MyItemWithViews>): DxAdapter<MyItemWithViews>(mItems)
//{
//    //in this case we always use the same layout.
//    //for a RecyclerView with multiple item types, return the proper layout
//    //according to the given parameters
//    override fun getLayoutRes(parent: ViewGroup, viewType: Int) = R.layout.my_item_with_views
//
//    //in this case we always use the same ViewHolder.
//    //for a RecyclerView with multiple item types, return the proper ViewHolder
//    //according to the given parameters
//    override fun createAdapterViewHolder(itemView: View,
//                                         parent: ViewGroup,
//                                         viewType: Int) = MyItemWithViewsViewHolder(itemView)
//
//
//    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int)
//    {
//        super.onBindViewHolder(holder, position)
//    }
//    /*override fun onBindViewHolder(holder: MyItemWithViewsViewHolder, position: Int)
//    {
//        super.onBindViewHolder(holder, position)
//
//        mItems[position].let { item ->
//            holder.apply {
//                switch.isChecked = item.isSwitchOn
//                checkBox.isChecked = item.isBoxChecked
//                editText.setText(item.mText)
//            }
//        }
//    }*/
//
//    override fun onViewRecycled(holder: RecyclerViewHolder)
//    {
//        super.onViewRecycled(holder)
////        cancel long running operations
//    }
//
//    inner class MyItemWithViewsViewHolder(itemView: View): RecyclerViewHolder(itemView)
//    {
//        val button: Button
//        val switch: Switch
//        val checkBox: CheckBox
//        val editText: EditText
//
//        init
//        {
//            itemView.apply {
//                button = myButton
//                switch = mySwitch
//                checkBox = myCheckBox
//                editText = myEditText
//            }
//
//            val item = mItems[adapterPosition]
//
//            button.setOnClickListener {
//                item.mText = "BUTTON!"
//            }
//
//            //NOTE:
//            //not using onCheckedChanged because it is being triggered also when the item gets out of view
//            switch.setOnClickListener {
//                item.isSwitchOn = switch.isChecked
//            }
//
//            checkBox.setOnClickListener {
//                item.isBoxChecked = checkBox.isChecked
//            }
//
//            editText.addTextChangedListener(object : TextWatcher
//            {
//                override fun afterTextChanged(s: Editable?)
//                {
//                    s?.apply { item.mText = toString() }
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//        }
//    }
//}