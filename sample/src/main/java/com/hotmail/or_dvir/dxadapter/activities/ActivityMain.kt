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
    //todo now that i have separation of features, check in the entire project
    // for nullable variables and see if you can make them non-nullable

    //todo after separating features to interfaces, TEST EVERYTHING AGAIN!!!

    //todo when documenting, note that this library was meant for kotlin and was not tested in java

    //todo test module with leak canary!!!!!!!

    //todo when documenting, add note about SimpleViewHolder - because the way kotlin treats generics,

    //todo make sure that for every object in this library (DxAdapter, DxActionModeHelper, DxItemTouchCallback etc...)
    // you have included ALL POSSIBLE OPTIONS in this sample

    //todo have different activities for different features? if you put all in 1 activity it might confuse the user

    //todo check all documentations (including comments in code!!!) to make sure its accurate
    // for example, click listeners are not needed anymore for default selection behavior

    //todo test the code WITHOUT default click behavior!!!

    //todo consider switching swiping logic to layout behind the item...

    private lateinit var mAdapter: MyAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mActionModeHelper: DxActionModeHelper<MyItem>

    //todo ripple effect is being overridden when applying stateListDrawable to our item

    //todo instead of writing what the default value for everything in the sample is,
    // refer them to the documentation - that way it only has to change in one place

    //todo BUG BUG BUG BUG BUG BUG BUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // selecting an item, swiping it to the left (deleting) while selected
    // -> action mode still active!!! what adapter thinks about number of selected items????

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

        mAdapter =
            MyAdapter(myListItems,
                      onItemSelectionChanged = { adapterPosition, item, isSelected ->
                          //MUST be called in order for DxActionMode to function as intended
                          mActionModeHelper.updateActionMode(this@ActivityMain)

                          val txt =
                              if (isSelected)
                                  "selected"
                              else
                                  "deselected"

                          Log.i("sample", "${item.mText} (position $adapterPosition) $txt")
                      })
                .apply {
                    //todo should i put these listeners in the constructor???
                    // its weird that some listeners are in constructor and some are out of it
                    onItemClick = { view, position, item ->
                        toast("clicked ${item.mText}. position $position")
                    }

                    onItemLongClick = { view, position, item ->
                        toast("long clicked ${item.mText}. position $position")
                        true
                    }
                }

        mActionModeHelper =
            DxActionModeHelper(mAdapter,
                               { "${mAdapter.getNumSelectedItems()}" },
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
            DxItemTouchCallback(mAdapter).apply {

                swipeEscapeVelocity = 200f

                //option to set escape velocity as a multiplier of the device's default value
//                swipeEscapeVelocityMultiplier = 1.5f

                swipeThreshold = 0.7f

                val backgroundRight = DxSwipeBackground("right swipe",
                                                        60, //recommended to use SP from dimen.xml
                                                        Color.BLACK,
                                                        30, //recommended to use DP from dimen.xml
                                                        Color.RED,
                                                        getDrawable(R.drawable.ic_arrow_right))

                val backgroundLeft = DxSwipeBackground("left swipe",
                                                       60, //recommended to use SP from dimen.xml
                                                       Color.BLACK,
                                                       30, //recommended to use DP from dimen.xml
                                                       Color.CYAN,
                                                       getDrawable(R.drawable.ic_arrow_left))

                //IMPORTANT: read the documentation for this function.
                //IMPORTANT: note that calling this function is NOT enough to make your items swipiable.
                //  your items must implement IItemSwipeable.
                setItemsSwipeable(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                                  backgroundRight,
                                  backgroundLeft)
                { item, position, direction ->

                    if (direction == ItemTouchHelper.START)
                    {
                        //this if condition is for explanation purposes:
                        //code will NEVER get here because we did not provide
                        //ItemTouchHelper.START as a valid swipe direction.
                        //START and LEFT are NOT the same, even if your layout is left-to-right
                    }

                    //delete item on left swipe
                    if (direction == ItemTouchHelper.LEFT)
                    {
                        myListItems.removeAt(position)
                        mAdapter.notifyItemRemoved(position)

                        //while DxActionModeHelper and IAdapterSelectable work together,
                        //they are still separate components. as we may have just removed the last
                        //selected item, mActionModeHelper needs to be notified that something has changed
                        //so it may destroy itself if needed, and in this specific case also update the title
                        //(which is the number of selected items).
                        //in this specific case, another approach would be to deselect the item before removing it,
                        //thus triggering the selection state listener which already calls updateActionMode.
                        //however that approach may not be right for you if other things are happening inside
                        //the selection state listener
                        mActionModeHelper.updateActionMode(this@ActivityMain)

                        toast("removed ${item.mText} (position $position)")
                    }

                    //rename item on right swipe:
                    else if (direction == ItemTouchHelper.RIGHT)
                    {
                        item.mText = "new name ${position + 1}"
                        //don't forget to restore the item, or you will be left with empty space
                        mAdapter.notifyItemChanged(position)
                    }
                }

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
            //todo add support for multiple handle ids (for a multi-type adapter)
            setDragHandleId(R.id.myItemDragHandle)
        }

        rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = mAdapter
            mItemTouchHelper.attachToRecyclerView(this)

            onFirstItemVisible = { Log.i("sample", "first item visible") }
            onFirstItemInvisible = { Log.i("sample", "first item not visible") }

            onLastItemVisible = { Log.i("sample", "last item visible") }
            onLastItemInvisible = { Log.i("sample", "last item not visible") }

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
                R.id.expandableFilterableSample -> startActivity<ActivityExpandableFilterable>()
                //todo not currently supported. this is for future use
//                R.id.horizontalSample -> startActivity<ActivityHorizontalRv>()
            }

            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId == android.R.id.home)
        {
            drawer_layout.apply {
                if(isDrawerOpen(GravityCompat.START))
                    closeDrawers()
                else
                    openDrawer(GravityCompat.START)
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
