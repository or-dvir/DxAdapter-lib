package com.hotmail.or_dvir.dxadapter.interfaces

import android.support.annotation.IdRes

interface IItemExpandable: IDxItem
{
    /**
     * other then giving an initial value, do NOT change this variable yourself!
     * instead use one of the functions in [IItemExpandable]
     */
    var isExpanded: Boolean

    /**
     * @return Int the resource id of the view (which is part of the list item) that is expandable
     */
    @IdRes
    fun getExpandableViewId(): Int

    /**
     * @return Boolean whether or not clicking the item should trigger expand/collapse.
     * if false, you must trigger expand and collapse yourself using
     * [IAdapterExpandable].
     */
    fun expandCollapseOnItemClick(): Boolean
}