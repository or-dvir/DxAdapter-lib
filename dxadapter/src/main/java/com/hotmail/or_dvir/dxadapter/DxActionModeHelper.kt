package com.hotmail.or_dvir.dxadapter

import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.hotmail.or_dvir.dxadapter.interfaces.IAdapterSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSelectable

/**
 * A helper class that provides default behavior for [ActionMode].
 * * IMPORTANT: in order for this to work as intended, you MUST set [onItemSelectionChanged][DxAdapter.onItemSelectionChanged]
 * in your [DxAdapter] AND inside that listener, you MUST call [updateActionMode]
 *
 * Behavior:
 * * selecting the first item will start ActionMode.
 * * deselecting the last item will finish ActionMode.
 * * clicking a menu item will also finish ActionMode, but only if you have consumed/handled the event
 * in [onActionItemClicked][ActionMode.Callback.onActionItemClicked] (TRUE was returned)
 * * when ActionMode is finished (for example after pressing the "back" button),
 * all items will be deselected. IMPORTANT: this does NOT trigger the selection
 * listener given to [DxAdapter]
 */
class DxActionModeHelper<ITEM : IItemBase>(
    private val adapter: DxAdapter<ITEM, *>,
    private val titleProvider: actionModeTitleProvider,
    private val callback: ActionMode.Callback)
{
    //todo when documenting add a comment that says this is intended to work with IAdapterSelectable
    // and will not work as intended otherwise

    //make this public in case the user wants access to it (for example to call finish())
    var actionMode: ActionMode? = null
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
            //NOTE: do NOT call adapter.deselect() because it will cause an
            //infinite loop:
            //deselect() will trigger the selection listener, which should call updateActionMode()
            //(if user followed instructions), which will eventually call finish() on the actionMode,
            //which will bring us back here.

            if (adapter is IAdapterSelectable<*>)
            {
                (adapter as IAdapterSelectable<*>).apply {
                    getAllSelectedIndices().forEach {
                        //all indices here must be IItemSelectable
                        (mAdapterItems[it] as IItemSelectable).isSelected = false
                        dxNotifyItemChanged(it)
                    }
                }
            }

            callback.onDestroyActionMode(mode)
            //this line should come AFTER calling the users' onDestroyActionMode()
            //because he might need the actionMode reference for some reason so only after
            //we make it null
            actionMode = null
        }
    }

    /**
     * this function starts/finishes actionMode, and updates its' title using
     * [titleProvider].
     *
     * it's assumed that this function is called inside [DxAdapter.onItemSelectionChanged].
     * if it's called from other places, it may cause bugs.
     */
    fun updateActionMode(act: AppCompatActivity)
    {
        //NOTE:
        //at this point we should be AFTER the selected state has already changed.
        //i say "should" because we are assuming that this function is called from inside
        //onItemSelectionChanged in the adapter

        if (adapter is IAdapterSelectable<*>)
        {
            when (adapter.getNumSelectedItems())
            {
                1 -> actionMode = actionMode ?: act.startSupportActionMode(mMyCallback)
                0 -> actionMode?.finish()
            }
        }

        actionMode?.title = titleProvider.invoke()
    }
}