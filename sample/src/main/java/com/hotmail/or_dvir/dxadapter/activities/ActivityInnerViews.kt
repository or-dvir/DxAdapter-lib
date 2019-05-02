package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.DxItemTouchCallback
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterInnerViews
import com.hotmail.or_dvir.dxadapter.models.MyItemWithViews
import kotlinx.android.synthetic.main.activity_inner_views.*

class ActivityInnerViews : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inner_views)

        val list = mutableListOf<MyItemWithViews>()

        for(i in 1..100)
            list.add(MyItemWithViews())

        val myAdapter = MyAdapterInnerViews(list)

        val touchCallback = DxItemTouchCallback(myAdapter).apply { dragOnLongClick = true }
        val touchHelper = ItemTouchHelper(touchCallback)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityInnerViews, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityInnerViews, RecyclerView.VERTICAL, false)
            adapter = myAdapter
            touchHelper.attachToRecyclerView(this)
        }
    }
}
