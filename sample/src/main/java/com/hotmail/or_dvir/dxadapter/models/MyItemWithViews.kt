package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "")
    : IItemBase
{
    override fun getViewType() = R.id.itemType_MyItemWithViews
}