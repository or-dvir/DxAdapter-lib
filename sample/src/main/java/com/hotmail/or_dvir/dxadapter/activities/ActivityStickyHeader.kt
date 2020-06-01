package com.hotmail.or_dvir.dxadapter.activities

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterHeader
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.models.MyHeader
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.activity_multi_type.*

class ActivityStickyHeader : BaseActivity() {
    private lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_header)

        //used IItemBase for convenience. use any type you want as long as
        //its shared between all list items
        val list = mutableListOf<IItemBase>()
        var headerCounter = 1

        for (i in 1..100) {
            if (i % 5 == 0) {
                list.add(MyHeader("Header $headerCounter"))
                headerCounter++
            } else
                list.add(MyItem(i.toString()))
        }

        val stickyHeaderAdapter = MyAdapterHeader(list)

        mItemTouchHelper = ItemTouchHelper(DxItemTouchCallback(stickyHeaderAdapter).apply {

            //note that MyHeader does NOT implement IItemSwipeable or IItemSelectable and therefore cannot be swiped
            //or selected. this makes sense because MyHeader is meant to divide our list into sections and not be interactable.

            swipeBackgroundLeft = DxSwipeBackground(
                30,
                Color.GREEN,
                DxSwipeText(
                    "left swipe",
                    60f,
                    Color.WHITE
                ),
                null
            )

            enableSwiping(ItemTouchHelper.LEFT)
            { item, position, direction ->

                if (direction == ItemTouchHelper.LEFT) {
                    //don't do anything (but restore the item so we don't have empty line)
                    stickyHeaderAdapter.notifyItemChanged(position)
                }
            }
        })

        rv.apply {
            addItemDecoration(
                DividerItemDecoration(
                    this@ActivityStickyHeader,
                    DividerItemDecoration.VERTICAL
                )
            )

            addItemDecoration(DxStickyHeaderItemDecoration(stickyHeaderAdapter))

            mItemTouchHelper.attachToRecyclerView(this)
            layoutManager =
                LinearLayoutManager(this@ActivityStickyHeader, RecyclerView.VERTICAL, false)
            adapter = stickyHeaderAdapter
        }
    }
}
