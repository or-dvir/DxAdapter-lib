package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.DxStickyHeaderItemDecoration
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyHeaderAdapter
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.activity_multi_type.*

class ActivityStickyHeader : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_header)

        //used DxItem for convenience. use any type you want as long as
        //its shared between all list items
        val list = mutableListOf<DxItem>()
        var headerCounter = 1

        for(i in 1..100)
        {
            if(i % 5 == 0)
            {
                list.add(MyHeader("Header $headerCounter"))
                headerCounter++
            }
            else
                list.add(MyItem(i.toString()))
        }

        val stickyHeaderAdapter = MyHeaderAdapter(list)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityStickyHeader, DividerItemDecoration.VERTICAL))


            addItemDecoration(DxStickyHeaderItemDecoration(stickyHeaderAdapter))



            layoutManager = LinearLayoutManager(this@ActivityStickyHeader, RecyclerView.VERTICAL, false)
            adapter = stickyHeaderAdapter
        }
    }
}
