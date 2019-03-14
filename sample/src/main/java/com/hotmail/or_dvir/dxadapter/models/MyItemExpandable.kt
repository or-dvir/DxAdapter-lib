package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxItemExpandable
import com.hotmail.or_dvir.dxadapter.R

class MyItemExpandable(var mText: String,
                       var mSubText: String,
                       var isDone: Boolean = false)
    //optionally set initial expanded state in this constructor
    : DxItemExpandable(/*true*/)
{
    override fun getExpandableViewId() = R.id.expandableGroup

    //if you'd like to use a handle for expanding/collapsing, set this to false
    //and see MyAdapterExpandable.ViewHolder for setting the handle
    override fun expandAndCollapseOnItemClick() = true
    override fun getViewType() = R.id.itemType_MyItemExpandable
}