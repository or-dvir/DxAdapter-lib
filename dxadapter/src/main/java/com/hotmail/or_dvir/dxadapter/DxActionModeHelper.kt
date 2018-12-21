package com.hotmail.or_dvir.dxadapter

import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem

//todo add in docs the behavior of this class
//todo destroy - deselecting items (NOT trigger selection listener)
//todo item clicked - if the user handled the event, finish action mode (and deselecting all items)
//todo also add following description
/**
 * if set, selecting an item will start "Action Mode",
 * and deselecting the last item will finish it.
 *
 * if not set: you must handle ActionMode yourself
 *
 * * in order for this to work, you MUST ALSO set [onSelectStateChangedListener],
 * AND call [updateActionMode] inside it.
 *
 * * if the ActionMode is finished, all items will be deselected WITHOUT triggering
 * [onSelectStateChangedListener]
 */


class DxActionModeHelper<ITEM: DxItem<SimpleViewHolder>>(
    private val adapter: DxAdapter<ITEM>,
    private val titleProvider: actionModeTitleProvider,
    private val callback: ActionMode.Callback)
{
    private var actionMode: ActionMode? = null
    private val mMyCallback = object : ActionMode.Callback
    {
        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean
        {
            val handled = callback.onActionItemClicked(mode, menuItem)
            if(handled)
                mode?.finish()

            return handled
        }
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) =
            callback.onCreateActionMode(mode, menu)
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) =
            callback.onPrepareActionMode(mode, menu)
        override fun onDestroyActionMode(mode: ActionMode?)
        {
            //if action mode is destroyed, we need to deselect all the items.
            //NOTE: do NOT call adapter.deselect() here because it will cause an
            //infinite loop:
            //deselect() will trigger the selection listener, which should call updateActionMode()
            //(if user followed instructions), which will eventually call finish() on the actionMode,
            //which will bring us back here.
            adapter.apply {
                mItems.forEach { it.mIsSelected = false }
                notifyDataSetChanged()
            }

            callback.onDestroyActionMode(mode)
        }
    }

    /**
     * this function starts/finishes actionMode, and updates its' title using
     * the provided [titleProvider].
     *
     * it's assumed that this function is called inside [DxAdapter.onSelectStateChangedListener].
     * if it's called from other places, it may cause bugs.
     */
    fun updateActionMode(act: AppCompatActivity)
    {
        //NOTE:
        //at this point we should be AFTER the selected state has already changed.
        //i say "should" because we are assuming that this function is called from inside
        //onSelectStateChangedListener in the adapter

        when (adapter.getNumSelectedItems())
        {
            1 ->
            {
                if(actionMode == null)
                    actionMode = act.startSupportActionMode(mMyCallback)
            }

            0 ->
            {
                actionMode?.finish()
                actionMode = null
            }
        }

        actionMode?.title = titleProvider.invoke()
    }
}