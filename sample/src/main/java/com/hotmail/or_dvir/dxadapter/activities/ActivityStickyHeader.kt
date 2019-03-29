package com.hotmail.or_dvir.dxadapter.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.adapters.MyHeaderAdapter
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.activity_multi_type.*

class ActivityStickyHeader : AppCompatActivity()
{
    private lateinit var mItemTouchHelper: ItemTouchHelper

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

        //todo this is for Testing. decide to change this or leave it when done Testing
        val stickyHeaderAdapter = MyHeaderAdapter(list).apply {
            defaultItemSelectionBehavior = false
        }
        //todo this is for Testing. decide to change this or leave it when done Testing
        mItemTouchHelper = ItemTouchHelper(DxItemTouchCallback(stickyHeaderAdapter).apply {
            dragOnLongClick = true

            val swipeBackgroundLeft = DxSwipeBackground("left swipe",
                                                    60,
                                                    30,
                                                    Color.WHITE,
                                                    Color.GREEN,
                                                    null)

            setItemsSwipeable(ItemTouchHelper.LEFT, null, swipeBackgroundLeft)
            { item, position, direction ->

                if (direction == ItemTouchHelper.LEFT)
                {
                    //don't do anything (but restore the item so we don't have empty line)
                    stickyHeaderAdapter.notifyItemChanged(position)
                }
            }
        })

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityStickyHeader, DividerItemDecoration.VERTICAL))
            addItemDecoration(DxStickyHeaderItemDecoration(stickyHeaderAdapter))

            mItemTouchHelper.attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(this@ActivityStickyHeader, RecyclerView.VERTICAL, false)
            adapter = stickyHeaderAdapter
        }
    }
}