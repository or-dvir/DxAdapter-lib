package com.hotmail.or_dvir.dxadapter

import android.support.annotation.IdRes
import android.support.v7.widget.helper.ItemTouchHelper

class DxItemTouchHelper<ITEM: DxItem>(private val itemTouchCallback: DxItemTouchCallback<ITEM>)
    : ItemTouchHelper(itemTouchCallback)
{
    fun setDragHandleId(@IdRes handleId: Int) =
        itemTouchCallback.setDragHandle(handleId, this)
}