package com.hotmail.or_dvir.dxadapter.interfaces

/**
 * implement this interface in your ITEM if you wish for it to be selectable
 */
interface IItemSelectable: IItemBase
{
    /**
     * holds the current selected state of this item.
     *
     * other then providing an initial value, do NOT change this variable yourself!
     * to select or deselect this item, use functions in [IAdapterSelectable]
     */
    var isSelected: Boolean
}