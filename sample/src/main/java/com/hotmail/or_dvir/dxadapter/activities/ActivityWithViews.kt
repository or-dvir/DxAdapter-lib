package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.DxItemTouchCallback
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterWithViews
import com.hotmail.or_dvir.dxadapter.models.MyItemWithViews
import kotlinx.android.synthetic.main.activity_with_views.*

class ActivityWithViews : AppCompatActivity()
{
    lateinit var mAdapter: MyAdapterWithViews
    lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_views)

        val list = mutableListOf<MyItemWithViews>()

        for(i in 1..100)
        {
            list.add(MyItemWithViews())
        }

        mAdapter = MyAdapterWithViews(list).apply {
            dragAndDropWithHandle = Pair(R.id.myItemDragHandle, { holder ->
                mItemTouchHelper.startDrag(holder)
            })
        }

        mItemTouchHelper = ItemTouchHelper(DxItemTouchCallback(mAdapter))

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityWithViews, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityWithViews, RecyclerView.VERTICAL, false)
            adapter = mAdapter
            mItemTouchHelper.attachToRecyclerView(this)
        }
    }
}
