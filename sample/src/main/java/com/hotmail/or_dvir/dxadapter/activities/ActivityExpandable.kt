package com.hotmail.or_dvir.dxadapter.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.DxItemTouchCallback
import com.hotmail.or_dvir.dxadapter.DxItemTouchHelper
import com.hotmail.or_dvir.dxadapter.DxSwipeBackground
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterExpandable
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.activity_filter.*
import or_dvir.hotmail.com.dxutils.setHomeUpEnabled
import org.jetbrains.anko.toast

class ActivityExpandable : BaseActivity()
    {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expandable)

        val list = mutableListOf<MyItemExpandable>()

        for(i in 1..100)
            list.add(MyItemExpandable(i.toString(),"expandable item $i"))

        val expandableAdapter = MyAdapterExpandable(list).apply {

            //please read documentation for special notes on this variable
//            onlyOneItemExpanded = true

            onItemCollapsed = { adapterPosition, item ->
                toast("collapsed item ${item.mText}")
            }

            onItemExpanded = { adapterPosition, item ->
                toast("expanded item ${item.mText}")
            }
        }

        val itemTouchCallback =
            DxItemTouchCallback(expandableAdapter).apply {

                val swipeBackgroundRight = DxSwipeBackground("right swipe",
                                                         60,
                                                         30,
                                                         Color.BLACK,
                                                         Color.RED,
                                                         getDrawable(R.drawable.ic_arrow_right))

                setItemsSwipeable(ItemTouchHelper.RIGHT, swipeBackgroundRight, null)
                { item, position, direction ->
                    //just to prevent item being removed from the screen completely.
                    //you can do whatever you want here
                    expandableAdapter.notifyItemChanged(position)
                }
            }

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityExpandable, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityExpandable, RecyclerView.VERTICAL, false)
            DxItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            adapter = expandableAdapter
        }
    }
}
