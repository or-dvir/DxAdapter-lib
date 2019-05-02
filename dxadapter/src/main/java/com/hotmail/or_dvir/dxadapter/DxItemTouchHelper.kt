package com.hotmail.or_dvir.dxadapter

import android.support.annotation.IdRes
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase

/**
 * a convenience wrapper for [ItemTouchHelper] what allows dragging with a handle
 *
 * @param ITEM the item type of your adapter
 * @param itemTouchCallback
 */
class DxItemTouchHelper<ITEM: IItemBase>(private val itemTouchCallback: DxItemTouchCallback<ITEM>)
    : ItemTouchHelper(itemTouchCallback)
{
    /**
     * enabled dragging items with a handle
     * @param handleId the resource id of the handle used to initiate the drag
     */
    fun setDragHandleId(@IdRes handleId: Int) =
        itemTouchCallback.setDragHandle(handleId, this)
}