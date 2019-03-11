package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxItemExpandable
import com.hotmail.or_dvir.dxadapter.R

class MyItemExpandable(var mText: String,
                       var mSubText: String,
                       var isDone: Boolean = false): DxItemExpandable()
{
    //todo test initial expanded state (constructor of DxItemExpandable)

    override fun getViewType() = R.id.itemType_MyItemExpandable
}