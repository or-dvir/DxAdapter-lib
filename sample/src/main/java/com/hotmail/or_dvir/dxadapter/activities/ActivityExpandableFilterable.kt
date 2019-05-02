package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterExpandableFilterable
import com.hotmail.or_dvir.dxadapter.models.MyItemExpandable
import kotlinx.android.synthetic.main.activity_expandable_filterable.*
import org.jetbrains.anko.toast

class ActivityExpandableFilterable : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expandable_filterable)

        //todo bug bug bug bug bug bug bug bug bug
        // when expanding a filtered list, item does not actually expand and the position is relative
        // to the FILTERED list

        val list = mutableListOf<MyItemExpandable>()

        for(i in 1..100)
            list.add(MyItemExpandable(i.toString(),"expandable item $i"))

        val myAdapter =
            MyAdapterExpandableFilterable(list,
                                          onItemExpandStateChanged = { adapterPosition, item, isExpanded ->
                                              val txt =
                                                  if (isExpanded)
                                                      "expanded"
                                                  else
                                                      "collapsed"

                                              toast("$txt item ${item.mText}")
                                          })

        et.addTextChangedListener(object : TextWatcher
                                  {
                                      override fun afterTextChanged(s: Editable)
                                      {
                                          myAdapter.filter(s.toString())
                                      }

                                      override fun beforeTextChanged(s: CharSequence?,
                                                                     start: Int,
                                                                     count: Int,
                                                                     after: Int)
                                      { /*do nothing*/ }

                                      override fun onTextChanged(s: CharSequence?,
                                                                 start: Int,
                                                                 before: Int,
                                                                 count: Int)
                                      { /*do nothing*/ }
                                  })

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityExpandableFilterable, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityExpandableFilterable, RecyclerView.VERTICAL, false)
            adapter = myAdapter
        }
    }
}
