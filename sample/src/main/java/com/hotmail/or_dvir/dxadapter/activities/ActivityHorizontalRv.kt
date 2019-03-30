package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.hotmail.or_dvir.dxadapter.DxItemVisibilityListener
import com.hotmail.or_dvir.dxadapter.DxScrollListener
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterHorizontal
import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
import kotlinx.android.synthetic.main.activity_horizontal_rv.*

class ActivityHorizontalRv : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal_rv)

        val list = mutableListOf<MyItemWithImage>()

        for(i in 1..100)
            list.add(MyItemWithImage(R.drawable.ic_launcher))

        val adapterHorizontal = MyAdapterHorizontal(list)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityHorizontalRv, DividerItemDecoration.HORIZONTAL))
            layoutManager = LinearLayoutManager(this@ActivityHorizontalRv, RecyclerView.HORIZONTAL, false)
            adapter = adapterHorizontal

            firstItemVisibilityListener = DxItemVisibilityListener().apply {
                onItemVisible = { Log.i("sample", "first item visible") }
                onItemInvisible = { Log.i("sample", "first item not visible") }
            }

            lastItemVisibilityListener = DxItemVisibilityListener().apply {
                onItemVisible = { Log.i("sample", "last item visible") }
                onItemInvisible = { Log.i("sample", "last item not visible") }
            }

            onScrollListener = DxScrollListener(50).apply {
                onScrollLeft = { Log.i("sample", "scroll left") }
                onScrollRight = { Log.i("sample", "scroll right") }
            }
        }
    }
}
