package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterInnerViews
import com.hotmail.or_dvir.dxadapter.adapters.MyMultiTypeAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItem
import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
import kotlinx.android.synthetic.main.activity_multi_type.*

class ActivityMultiType : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_type)

        //used DxItem for convenience. use any type you want as long as
        //its shared between all list items
        val list = mutableListOf<DxItem>()

        for(i in 1..100)
        {
            if(i % 5 == 0)
                list.add(MyItemWithImage(R.drawable.ic_launcher))
            else
                list.add(MyItem(i.toString()))
        }

        val multiAdapter = MyMultiTypeAdapter(list)

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMultiType, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMultiType, RecyclerView.VERTICAL, false)
            adapter = multiAdapter
        }
    }
}
