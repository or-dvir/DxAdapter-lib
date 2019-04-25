package com.hotmail.or_dvir.dxadapter.models

import android.support.annotation.DrawableRes
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSelectable

class MyItemWithImage(@DrawableRes val imageRes: Int)
    : IItemSelectable,
      IItemDraggable
{
    override var isSelected = false
    override fun getViewType() = R.id.itemType_MyItemWithImage
}