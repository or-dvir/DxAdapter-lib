package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IDxItem

data class MyItemWithViews(var isSwitchOn: Boolean = false,
                           var isBoxChecked: Boolean = false,
                           var mText: String = "")
    : IDxItem
{
    override fun getViewType() = R.id.itemType_MyItemWithViews
}