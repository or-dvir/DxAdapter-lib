package com.hotmail.or_dvir.dxadapter

import android.support.annotation.IdRes
import android.support.v7.widget.helper.ItemTouchHelper
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase

class DxItemTouchHelper<ITEM: IItemBase>(private val itemTouchCallback: DxItemTouchCallback<ITEM>)
    : ItemTouchHelper(itemTouchCallback)
{
    fun setDragHandleId(@IdRes handleId: Int) =
        itemTouchCallback.setDragHandle(handleId, this)
}