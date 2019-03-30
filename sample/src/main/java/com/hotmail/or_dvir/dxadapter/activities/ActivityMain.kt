package com.hotmail.or_dvir.dxadapter.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
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

    //todo make sure that for every object in this library (DxAdapter, DxActionModeHelper, DxItemTouchCallback etc...)
    //todo you have included ALL POSSIBLE OPTIONS in this sample

    //todo have different activities for different features? if you put all in 1 activity it might confuse the user

    //todo check all documentations (including comments in code!!!) to make sure its accurate
    //todo for example, click listeners are not needed anymore for default selection behavior

    //todo test the code WITHOUT default click behavior!!!

//    i stopped here
//    https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd#667e

    private lateinit var mSampleAdapter: MyAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mActionModeHelper: DxActionModeHelper<MyItem>

    //todo ripple effect is being overridden when applying stateListDrawable to our item

    //todo instead of writing what the default value for everything in the sample is,
    //todo refer them to the documentation - that way it only has to change in one place

    //todo BUG BUG BUG BUG BUG BUG BUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //todo selecting an item, swiping it to the left (deleting) while selected
    //todo -> action mode still active!!! what adapter thinks about number of selected items????

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val myListItems = mutableListOf<MyItem>()

        for (i in 1..100)
            myListItems.add(MyItem(i.toString()))

        mSampleAdapter = MyAdapter(myListItems).apply {
            onItemClick = { view, position, item ->
                toast("clicked ${item.mText}. position $position")
            }

            onItemLongClick = { view, position, item ->
                toast("long clicked ${item.mText}. position $position")
                true
            }

            onItemSelectionChanged = { position, item, isSelected ->

                //MUST be called in order for DxActionMode to function as intended
                mActionModeHelper.updateActionMode(this@ActivityMain)

                val txt =
                    if (isSelected)
                        "selected"
                    else
                        "deselected"

                Log.i("sample", "${item.mText} (position $position) $txt")
            }

            //default is accent color (if not provided, primary color is used).
            //note: this must be @ColorInt
//            selectedItemBackgroundColor = resources.getColor(android.R.color.holo_blue_dark)

            //default is true.
//            defaultItemSelectionBehavior = false

            //default is false
//            triggerClickListenersInSelectionMode = true
        }

        mActionModeHelper =
            DxActionModeHelper(mSampleAdapter,
                               { "${mSampleAdapter.getNumSelectedItems()}" },
                               object : ActionMode.Callback
                               {
                                   override fun onActionItemClicked(mode: ActionMode?,
                                                                    menuItem: MenuItem?): Boolean
                                   {
                                       toast("${menuItem?.title}")
                                       return true
                                   }

                                   override fun onCreateActionMode(mode: ActionMode?,
                                                                   menu: Menu?): Boolean
                                   {
                                       menuInflater.inflate(R.menu.action_mode, menu)
                                       return true
                                   }

                                   override fun onPrepareActionMode(mode: ActionMode?,
                                                                    menu: Menu?): Boolean
                                   {
                                       return false
                                   }

                                   override fun onDestroyActionMode(mode: ActionMode?)
                                   {
                                   }
                               })

        val itemTouchCallback =
            DxItemTouchCallback(mSampleAdapter).apply {

                //in order for the swipe to "count", the user needs to swipe with
                //a speed of 200 pixels per second (or far enough as defined below).
                //note that this value is overridden by swipeEscapeVelocityMultiplier (if set)
//                swipeEscapeVelocity = 200f

                //in order for the swipe to "count", the user needs to swipe 1.5 times faster
                //then the device's default value (or far enough as defined below).
                //note that this overrides swipeEscapeVelocity (if set)
                swipeEscapeVelocityMultiplier = 1.5f

                //in order for the swipe to "count", the user needs to swipe away 70% of the item
                //(or fast enough as defined above)
                swipeThreshold = 0.7f


                //if you don't want text, pass empty string.
                //if you don't want background color, pass null.
                //if you don't want icon, pass null
                //todo if i dont want text, no point in passing text color!!!
                val backgroundRight = DxSwipeBackground("right swipe",
                                                        60, //todo before release, change this to SP from dimen!!!
                                                        30, //todo before release, change this to DP from dimen!!!
                                                        Color.BLACK,
                                                        Color.RED,
                                                        getDrawable(R.drawable.ic_arrow_right))

                val backgroundLeft = DxSwipeBackground("left swipe",
                                                       60, //todo before release, change this to SP from dimen!!!
                                                       30, //todo before release, change this to DP from dimen!!!
                                                       Color.BLACK,
                                                       Color.CYAN,
                                                       getDrawable(R.drawable.ic_arrow_left))

                //todo when documenting add a note that initializing swipeBackgroundLeft/right
                //todo does not automatically mean that swipe is enabled.
                //todo the user MUST call setItemsSwipeable() to enable swiping
                //todo maybe i can make it such that swipeBackgroundLeft/right is part of the function?
                //todo that way it would be less confusing to the user

                //IMPORTANT NOTE:
                //the directions you provide in the first parameter
                //determine the "direction" parameter of the callback.
                //in the example below, the "direction" parameter of the
                //callback will ALSO be either ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT.
                //however if you check for ItemTouchHelper.START in the listener, it will not work
                setItemsSwipeable(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                                  backgroundRight,
                                  backgroundLeft)
                { item, position, direction ->

                    if (direction == ItemTouchHelper.START)
                    {
                        //code will NEVER get here because we did not provide
                        //ItemTouchHelper.START as a valid swipe direction
                    }

                    //delete item on left swipe
                    if (direction == ItemTouchHelper.LEFT)
                    {
                        myListItems.removeAt(position)
                        mSampleAdapter.notifyItemRemoved(position)
                        toast("removed ${item.mText} (position $position)")
                    }

                    //rename item on right swipe:
                    else if (direction == ItemTouchHelper.RIGHT)
                    {
                        item.mText = "new name ${position + 1}"
                        //don't forget to restore the item, or you will be left with empty space
                        mSampleAdapter.notifyItemChanged(position)
                    }
                }

                //option to initiate drag with long-clicking an item.
                //be aware that if long-click also selects items,
                //results may not be as intended (e.g. meant to long-click but started drag instead)
//                dragOnLongClick = true

                //if your list is actually a grid, you need to set this value to TRUE
                //otherwise drag-and-drop will not work as expected
//                isGridLayoutManager = true

                onItemMove = { draggedItem, targetItem, draggedPosition, targetPosition ->
                    Log.i("sample",
                          "about to switch ${draggedItem.mText} (position $draggedPosition) " +
                                  "with ${targetItem.mText} (position $targetPosition)"
                    )
                }
            }

        //if you want to use the drag-and-drop features of this adapter,
        //you must provide DxItemTouchCallback to ItemTouchHelper.
        //don't forget to attach it to your RecyclerView!
        mItemTouchHelper = DxItemTouchHelper(itemTouchCallback).apply {
            //todo what if i mix items in the adapter, and each has different handle?!
            //todo make a method "getHandleId()"???? keep it like this and force the user
            //todo so use the same id for all handles????
            setDragHandleId(R.id.myItemDragHandle)
        }

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain,
                                                    DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = mSampleAdapter
            mItemTouchHelper.attachToRecyclerView(this)

            firstItemVisibilityListener = DxItemVisibilityListener().apply {
                onItemVisible = { Log.i("sample", "first item visible") }
                onItemInvisible = { Log.i("sample", "first item not visible") }
            }

            lastItemVisibilityListener = DxItemVisibilityListener().apply {
                onItemVisible = { Log.i("sample", "last item visible") }
                onItemInvisible = { Log.i("sample", "last item not visible") }
            }

            onScrollListener = DxScrollListener(50).apply {
                onScrollDown = { fab.hide() }
                onScrollUp = { fab.show() }
            }
        }

        nav_view.setNavigationItemSelectedListener {

            when (it.itemId)
            {
                R.id.innerViewsSample -> startActivity<ActivityInnerViews>()
                R.id.multiTypeSample -> startActivity<ActivityMultiType>()
                R.id.stickyHeaderSample -> startActivity<ActivityStickyHeader>()
                R.id.filterSample -> startActivity<ActivityFilter>()
                R.id.expandableSample -> startActivity<ActivityExpandable>()
                R.id.horizontalSample -> startActivity<ActivityHorizontalRv>()
            }

            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            android.R.id.home -> {
                drawer_layout.apply {
                    if(isDrawerOpen(GravityCompat.START))
                        closeDrawers()
                    else
                        openDrawer(GravityCompat.START)
                }
            }
            R.id.innerViewsSample -> startActivity<ActivityInnerViews>()
            R.id.multiTypeSample -> startActivity<ActivityMultiType>()
            R.id.stickyHeaderSample -> startActivity<ActivityStickyHeader>()
            R.id.filterSample -> startActivity<ActivityFilter>()
            R.id.expandableSample -> startActivity<ActivityExpandable>()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }
}
