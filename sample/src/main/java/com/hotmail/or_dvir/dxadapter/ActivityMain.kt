package com.hotmail.or_dvir.dxadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity()
{
    //todo when documenting, note that this library was meant for kotlin and was not tested in java

    //todo test module with leak canary!!!!!!!

    //todo when documenting, add note about SimpleViewHolder - because the way kotlin treats generics,
    //todo if the user wants their own view holder they should extend SimpleViewHolder and NOT RecyclerView.ViewHolder

    //todo make sure that for every object in this library (DxAdapter, DxActionModeHelper, DxItemTouchCallback etc...)
    //todo you have included ALL POSSIBLE OPTIONS in this sample

//    i stopped here
//    https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd#667e

    private lateinit var mSampleAdapter: DxAdapter<MyItem>
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mActionModeHelper: DxActionModeHelper<MyItem>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = mutableListOf<MyItem>()

        for(i in 1..100)
        {
            list.add(MyItem(i.toString()))
        }

        mSampleAdapter = DxAdapter(list).apply {
            onClickListener = { view, position, item ->
                toast("clicked ${item.mText}. position $position")
            }

            onLongClickListener = { view, position, item ->
                toast("long clicked ${item.mText}. position $position")
                true
            }

            onSelectStateChangedListener = { position, item, isSelected ->

                //MUST be called in order for DxActionMode to function as intended
                mActionModeHelper.updateActionMode(this@ActivityMain)

                val txt =
                    if (isSelected)
                        "selected"
                    else
                        "deselected"

                Log.i("sample", "${item.mText} (position $position) $txt")
            }

            dragAndDropWithHandle = Pair(R.id.myItemDragHandle, { holder ->
                mItemTouchHelper.startDrag(holder)
            })

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

        mActionModeHelper = DxActionModeHelper(mSampleAdapter, { "${mSampleAdapter.getNumSelectedItems()}" },
            object : ActionMode.Callback
            {
                override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean
                {
                    toast("${menuItem?.title}")
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean
                {
                    menuInflater.inflate(R.menu.action_mode, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean
                {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?)
                {
                }
            })

        //if you want to use the drag-and-drop features of this adapter,
        //you must provide DxItemTouchCallback to ItemTouchHelper.
        //don't forget to attach it to your RecyclerView!
        mItemTouchHelper = ItemTouchHelper(
            DxItemTouchCallback(mSampleAdapter).apply {

                //option to initiate drag with long-clicking an item
                //be aware that if long-click also selects item,
                //results may not be as intended (e.g. meant to long-click but initiated drag instead)
//                    dragOnLongClick = true

                //if your list is actually a grid, you need to set this value to TRUE
                //otherwise drag-and-drop will not work as expected
//                isGridLayoutManager = true

                onItemsMovedListener = { draggedItem, targetItem, draggedPosition, targetPosition ->
                    Log.i("sample",
                        "about to switch ${draggedItem.mText} (position $draggedPosition) " +
                                "with ${targetItem.mText} (position $targetPosition)"
                    )
                }
            })

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = mSampleAdapter
            mItemTouchHelper.attachToRecyclerView(this)

            firstItemVisibilityListener = object : OnAdapterItemVisibilityChanged
            {
                override fun onVisible() = Log.i("sample", "first item visible")
                override fun onInvisible() = Log.i("sample", "first item not visible")
            }

            lastItemVisibilityListener = object : OnAdapterItemVisibilityChanged
            {
                override fun onVisible() = Log.i("sample", "last item visible")
                override fun onInvisible() = Log.i("sample", "last item not visible")
            }

            onScrollingDownListener = Pair(50, { fab.hide() })
            onScrollingUpListener = Pair(50, { fab.show() })
        }

        button.setOnClickListener {
            //do something
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        return if(item != null && item.itemId == R.id.sampleWithViews)
        {
            startActivity<ActivityWithViews>()
            true
        }

        else
            super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        return if (menu != null)
        {
            menuInflater.inflate(R.menu.main_menu, menu)
            true
        }

        else
            super.onCreateOptionsMenu(menu)
    }
}
