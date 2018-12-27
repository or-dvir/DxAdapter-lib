package com.hotmail.or_dvir.dxadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.activity_with_views.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ActivityWithViews : AppCompatActivity()
{
    lateinit var mAdapter: DxAdapter<MyItemWithViews>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_views)

        val list = mutableListOf<MyItemWithViews>()

        for(i in 1..100)
        {
            list.add(MyItemWithViews())
        }

        mAdapter = MyAdapter(list)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityWithViews, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityWithViews, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    @Subscribe
    fun test(event: SwitchEvent)
    {
        Log.i("aaaaa", "got event set to ${event.isOn}")
//        mAdapter.mItems[event.position].isSwitchOn = event.isOn
    }

    override fun onStart()
    {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop()
    {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
