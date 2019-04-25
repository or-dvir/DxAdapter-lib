package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "")
    : IItemDraggable
{
    override fun getViewType() = R.id.itemType_MyItemWithViews
}