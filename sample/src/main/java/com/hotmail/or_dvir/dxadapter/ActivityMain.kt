package com.hotmail.or_dvir.dxadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = mutableListOf<MyItem>()

        for(i in 1..100)
        {
            list.add(MyItem(i.toString()))
        }

        val myAdapter = DxAdapter(list).apply {
            setOnClickListener { view, position, item ->
                toast("clicked ${item.mText}. position $position")
            }

            setOnLongClickListener { view, position, item ->
                this@apply.select(position)

                toast("long clicked ${item.mText}. position $position")
                true
            }

            setSelectedItemBackgroundColor(R.color.colorPrimary)
        }


        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = myAdapter
        }
    }
}
