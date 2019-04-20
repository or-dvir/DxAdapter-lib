package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.activity_filter.*

class ActivityFilter : BaseActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val list = mutableListOf<MyItem>()

        for(i in 1..100)
            list.add(MyItem(i.toString()))

        val filterAdapter = MyAdapter(list,
                                      onItemSelectionChanged = { _, _, isSelected -> /*do nothing*/ })
            .apply {
                dxFilter = { constraint ->
                    list.filter { it.mText.startsWith(constraint.trim(), true) }
                }
            }

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityFilter, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityFilter, RecyclerView.VERTICAL, false)
            adapter = filterAdapter
        }

        et.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable)
            {
                //the long way
//                filterAdapter.getFilter().filter(s.toString())

                //the shorter way
                filterAdapter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {}
        })
    }
}
