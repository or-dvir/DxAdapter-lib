package com.hotmail.or_dvir.dxadapter.interfaces

import android.support.annotation.IdRes

/**
 * implement this interface in your ITEM if you wish for it to be expandable
 */
interface IItemExpandable: IItemBase
{
    /**
     * holds the current expanded state of this item.
     *
     * other then providing an initial value, do NOT change this variable yourself!
     * to expand or collapse this item, use functions in [IAdapterExpandable]
     */
    var isExpanded: Boolean

    /**
     * returns the resource id of the container for the expandable part of your item
     */
    @IdRes
    fun getExpandableViewId(): Int

    /**
     * whether or not clicking this item should expand/collapse it.
     * if false, you must trigger expand and collapse yourself using functions in [IAdapterExpandable].
     */
    fun expandCollapseOnItemClick(): Boolean
}