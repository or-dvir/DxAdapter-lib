package com.hotmail.or_dvir.dxadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_with_views.*

class ActivityWithViews : AppCompatActivity()
{
    lateinit var mAdapter: DxAdapter<MyItemWithViews, *>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_views)

        val list = mutableListOf<MyItemWithViews>()

        for(i in 1..100)
        {
            list.add(MyItemWithViews())
        }

        mAdapter = DxAdapter(list)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityWithViews, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityWithViews, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }
}
