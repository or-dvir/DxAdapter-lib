package com.hotmail.or_dvir.dxadapter

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

open class DxHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    internal val originalBackground: Drawable? = itemView.background
}
/**
 * a listener called when an item is long-clicked
 * @param view the clicked view
 * @param adapterPosition the adapter position of the clicked item
 * @param item the clicked item
 * @return true if the callback consumed the long click, false otherwise
 */
typealias onItemLongClickListener<ITEM> = (view: View, adapterPosition: Int, item: ITEM) -> Boolean
/**
 * a listener called when an item is clicked
 * @param view the clicked view
 * @param adapterPosition the adapter position of the clicked item
 * @param item the clicked item
 */
typealias onItemClickListener<ITEM> = (view: View, adapterPosition: Int, item: ITEM) -> Unit
/**
 * a listener called when the selected state of an item has changed
 * @param adapterPosition the adapter position of the item
 * @param item the item whose selected state has changed
 * @param isSelected the new selected state of the item
 */
typealias onItemSelectStateChangedListener<ITEM> = (adapterPosition: Int, item: ITEM, isSelected: Boolean) -> Unit
/**
 * a listener called when the expanded state of an item has changed
 * @param adapterPosition the adapter position of the item
 * @param item the item whose expanded state has changed
 * @param isExpanded the new expanded state of the item
 */
typealias onItemExpandStateChangedListener<ITEM> =
            (adapterPosition: Int, item: ITEM, isExpanded: Boolean) -> Unit
/**
 * called when the adapter is being filtered by a constraint
 * @param constraint the constraint used to filter
 * @return a list of items that should REMAIN in the adapter
 */
typealias onFilterRequest<ITEM> = (constraint: CharSequence) -> List<ITEM>
/**
 * a listener called when an item in the adapter is being moved/dragged
 * @param draggedItem the item being dragged
 * @param targetItem the item being replaced
 * @param draggedPosition the position of the item being dragged
 * @param targetPosition the position of the item being replaced
 */
internal typealias onItemsMoveListener<ITEM> = (draggedItem: ITEM,
                                                targetItem: ITEM,
                                                draggedPosition: Int,
                                                targetPosition: Int) -> Unit
/**
 * a listener called when an item in the adapter has been swiped
 * @param item the item that was swiped
 * @param adapterPosition the position of the item that was swiped
 * @param direction the direction of the swipe.
 * see [DxItemTouchCallback.setItemsSwipeable] for further explanation about this parameter
 */
internal typealias onItemSwipedListener<ITEM> = (item: ITEM,
                                                 adapterPosition: Int,
                                                 direction: Int) -> Unit
/**
 * used by the library.
 */
internal typealias startDragListener = (holder: DxHolder) -> Unit
/**
 * a listener that does not take any parameters and does not return any value
 */
internal typealias emptyListener = () -> Unit
/**
 * returns a string that will be the title of [DxActionModeHelper]
 */
internal typealias actionModeTitleProvider = () -> String



