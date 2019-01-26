package com.hotmail.or_dvir.dxadapter.activities

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
import com.hotmail.or_dvir.dxadapter.*
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity()
{
    //todo when documenting, note that this library was meant for kotlin and was not tested in java

    //todo test module with leak canary!!!!!!!

    //todo when documenting, add note about SimpleViewHolder - because the way kotlin treats generics,
    //todo if the user wants their own view holder they should extend SimpleViewHolder and NOT RecyclerView.DefaultViewHolder

    //todo make sure that for every object in this library (DxAdapter, DxActionModeHelper, DxItemTouchCallback etc...)
    //todo you have included ALL POSSIBLE OPTIONS in this sample

    //todo have different activities for different features? if you put all in 1 activity it might confuse the user

//    i stopped here
//    https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd#667e

    private lateinit var mSampleAdapter: MyAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mActionModeHelper: DxActionModeHelper<MyItem>

    //todo ripple effect is being overridden when applying stateListDrawable to our item

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myListItems = mutableListOf<MyItem>()

        for(i in 1..100)
        {
            myListItems.add(MyItem(i.toString()))
        }

        mSampleAdapter = MyAdapter(myListItems).apply {
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

            //todo what if i mix items in the adapter, and each has different handle?!
            //todo make a method "getHandleId()"???? keep it like this and force the user
            //todo so use the same id for all handles????
            dragAndDropWithHandle = Pair(R.id.myItemDragHandle, { holder ->
                mItemTouchHelper.startDrag(holder)
            })

            //default is accent color (if not provided, primary color is used).
            //note: this must be @ColorInt
//            selectedItemBackgroundColor = resources.getColor(android.R.color.holo_blue_dark)

            //default is true.
            //however this requires a long-click listener to work
//            defaultItemSelectionBehavior = false

            //default is false
//            triggerClickListenersInSelectionMode = true
        }

        mActionModeHelper = DxActionModeHelper(mSampleAdapter,
            { "${mSampleAdapter.getNumSelectedItems()}" },
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

                //NOTE:
                //drawing text will only work if there is a background set to the same side!
                //todo is this what i want???? does this make sense???

                swipeBackgroundColorRight = android.R.color.holo_orange_light
                swipeBackgroundTextRight = Triple("right swipe", 60f, android.R.color.white)

                swipeBackgroundColorLeft = android.R.color.holo_red_light
                swipeBackgroundTextLeft = Triple("left swipe", 60f, android.R.color.white)


                //IMPORTANT NOTE:
                //the direction you provide in the first element of the Pair
                //determine the "direction" parameter of the callback. for example:
                //if you provide ItemTouchHelper.LEFT and ItemTouchHelper.RIGHT
                //like below, then the "direction" parameter of the
                //listener will ALSO be either ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT.
                //however if you check for ItemTouchHelper.START in the listener, it will not work
                onItemSwiped = Pair(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                                    { item, position, direction ->

                        //todo do what you want here. for example:

//                        if(direction == ItemTouchHelper.START)
//                        {
//                            //this will NEVER trigger because we did not provide
//                            //ItemTouchHelper.START as a valid swipe direction
//                        }

                        //delete item on left swipe
                        if(direction == ItemTouchHelper.LEFT)
                        {
                            myListItems.removeAt(position)
                            mSampleAdapter.notifyItemRemoved(position)
                            toast("removed ${item.mText} (position $position)")
                        }

                        //rename item on right swipe:
                        if(direction == ItemTouchHelper.RIGHT)
                        {
                            item.mText = "new name ${position + 1}"
                            //don't forget to restore the item, or you will be left with empty space
                            mSampleAdapter.notifyItemChanged(position)
                        }
                    })

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

            firstItemVisibilityListenerI = object : IOnAdapterItemVisibilityChanged
            {
                override fun onVisible() = Log.i("sample", "first item visible")
                override fun onInvisible() = Log.i("sample", "first item not visible")
            }

            lastItemVisibilityListenerI = object : IOnAdapterItemVisibilityChanged
            {
                override fun onVisible() = Log.i("sample", "last item visible")
                override fun onInvisible() = Log.i("sample", "last item not visible")
            }

            onScrollingDownListener = Pair(50, { fab.hide() })
            onScrollingUpListener = Pair(50, { fab.show() })
        }

        button.setOnClickListener {
            //todo do something
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.innerViewsSample -> startActivity<ActivityInnerViews>()
            R.id.multiTypeSample -> startActivity<ActivityMultiType>()
            R.id.stickyHeaderSample -> startActivity<ActivityStickyHeader>()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
