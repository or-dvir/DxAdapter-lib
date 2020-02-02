package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.DxItemTouchCallback
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterMultiType
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.models.MyItem
import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
import kotlinx.android.synthetic.main.activity_multi_type.*

class ActivityMultiType : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_type)

        //used IItemBase for convenience. use any type you want as long as
        //its shared between all list items
        val list = mutableListOf<IItemBase>()

        //note that even though MyItem is draggable, swipeable, and selectable,
        //for drag and swipe to actually work we need to also attach ItemTouchHelper with
        //DxItemTouchCallback to the recycler view.
        //and for selection to actually work, our adapter needs to implement IAdapterSelectable.
        //these features are already demonstrated in other places in this sample app, and therefore
        //are not necessary here.
        //this activity is only for demonstrating how to make an adapter support multiple view types
        for(i in 1..100)
        {
            if(i % 5 == 0)
                list.add(MyItemWithImage(R.drawable.ic_launcher))
            else
                list.add(MyItem(i.toString()))
        }

        val multiAdapter = MyAdapterMultiType(list)

        rv.apply {
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(
                this@ActivityMultiType,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
            layoutManager = androidx.recyclerview.widget
                .LinearLayoutManager(this@ActivityMultiType,
                                     androidx.recyclerview.widget.RecyclerView.VERTICAL,
                                     false)
            adapter = multiAdapter
        }
    }
}
