package com.hotmail.or_dvir.dxadapter.interfaces

/**
 * an interface containing some shared behaviour for all items to be used by your adapter.
 * note that other "IItem..." interfaces already extend this one so (usually) there is no need
 * to implement this interface directly.
 *
 * when might you need to directly implement this interface?
 * for example: if we have a multi-type adapter where 1 item type is interactable and the other is not
 * (like in the sticky header sample where the headers should not be interactable)
 */
interface IItemBase
{
    /**
     * returns an identifier for this adapter item. this identifier should be unique.
     *
     * to prevent bugs, this value should be a constant, and it is recommended to use @IdRes
     */
    fun getViewType(): Int
}