package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase

class MyHeader(var mText: String): IItemBase
{
    override fun getViewType() = R.id.itemType_MyHeader
}