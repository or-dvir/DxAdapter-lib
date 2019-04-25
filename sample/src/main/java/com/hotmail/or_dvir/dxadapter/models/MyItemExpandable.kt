package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemExpandable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSwipeable

class MyItemExpandable(var mText: String,
                       var mSubText: String,
                       var isDone: Boolean = false)
    : IItemExpandable, IItemSwipeable
{
    //this is just the initial value. you should not change this variable yourself
    override var isExpanded = false

    override fun getExpandableViewId() = R.id.expandableGroup

    //if you'd like to use a handle for expanding/collapsing, set this to false
    //and see MyAdapterExpandable.ViewHolder for setting the handle
    override fun expandCollapseOnItemClick() = true
    override fun getViewType() = R.id.itemType_MyItemExpandable
}