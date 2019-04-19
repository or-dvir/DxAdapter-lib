package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IDxItem

class MyHeader(var mText: String): IDxItem
{
    override fun getViewType() = R.id.itemType_MyHeader
}