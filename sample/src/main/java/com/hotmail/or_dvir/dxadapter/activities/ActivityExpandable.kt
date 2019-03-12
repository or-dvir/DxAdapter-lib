package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hotmail.or_dvir.dxadapter.IOnExpandedStateChanged
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterExpandable
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.activity_filter.*
import org.jetbrains.anko.toast

class ActivityExpandable : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expandable)

        val list = mutableListOf<MyItemExpandable>()

        for(i in 1..100)
            list.add(MyItemExpandable(i.toString(),"expandable text $i"))

        val expandableAdapter = MyAdapterExpandable(list).apply {

            expandAndCollapseItemsInSelectionMode = true

            onExpandedStateChangedListener = object : IOnExpandedStateChanged<MyItemExpandable>
            {
                override fun onExpanded(position: Int, item: MyItemExpandable) =
                    toast("expanded item ${item.mText}")

                override fun onCollapsed(position: Int, item: MyItemExpandable) =
                    toast("collapsed item ${item.mText}")
            }
        }

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityExpandable, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityExpandable, RecyclerView.VERTICAL, false)
            adapter = expandableAdapter
        }
    }
}
