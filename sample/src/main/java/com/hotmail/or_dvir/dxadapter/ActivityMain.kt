package com.hotmail.or_dvir.dxadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity()
{
    //todo when documenting, note that this library was meant for kotlin and was not tested in java

    //todo test module with leak canary!!!!!!!

    lateinit var actionModeHelper: DxActionModeHelper<MyItem>


    BUG:
    selecting an item (starts action mode)
    press back (finishes action mode)
    select item -> action mode does not start!!!


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

            onClickListener = { view, position, item ->
                toast("clicked ${item.mText}. position $position")
            }

            onLongClickListener = { view, position, item ->
                toast("long clicked ${item.mText}. position $position")
                true
            }

            onSelectStateChangedListener = { position, item, isSelected ->

                //MUST be called in order for DxActionMode to function as intended
                actionModeHelper.updateActionMode(this@ActivityMain)

                val txt =
                    if (isSelected)
                        "selected"
                    else
                        "deselected"
                toast("${item.mText} $txt")
            }

            //default is colorAccent
            //if colorAccent is not provided in the style "AppTheme",
            //the primary color is used
//            selectedItemBackgroundColorRes = R.color.colorPrimary

            //default is true.
            //however this requires a long-click listener to work
//            defaultItemSelectionBehavior = false

            //default is false
//            triggerClickListenersInSelectionMode = true
        }

        actionModeHelper = DxActionModeHelper(myAdapter, { "${myAdapter.getNumSelectedItems()}" },
            object : ActionMode.Callback
            {
                override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean
                {
                    return true
                }

                override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean
                {
                    return true
                }

                override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean
                {
                    return false
                }

                override fun onDestroyActionMode(p0: ActionMode?)
                {
                }
            })

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = myAdapter

            firstItemVisibilityListener = object : OnAdapterItemVisibilityChanged
            {
                override fun onVisible() = toast("first yes")
                override fun onInvisible() = toast("first no")
            }

            lastItemVisibilityListener = object : OnAdapterItemVisibilityChanged
            {
                override fun onVisible() = toast("last yes")
                override fun onInvisible() = toast("last no")
            }

            onScrollingDownListener = Pair(50, { fab.hide() })
            onScrollingUpListener = Pair(50, { fab.show() })
        }

        button.setOnClickListener {
            //do something
        }
    }
}
