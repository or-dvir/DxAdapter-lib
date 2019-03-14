package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView
import android.view.View


typealias RecyclerViewHolder = RecyclerView.ViewHolder

/**
 * @return true if the callback consumed the long click, false otherwise
 */
internal typealias onItemLongClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Boolean
internal typealias onItemClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Unit

internal typealias  positionAndItemListener<ITEM> = (position: Int, item: ITEM) -> Unit

internal typealias onItemSelectStateChangedListener<ITEM> =
        (position: Int, item: ITEM, isSelected: Boolean) -> Unit

internal typealias dxFilter<ITEM> = (constraint: CharSequence) -> List<ITEM>




internal typealias onItemsMovedListener<ITEM> = (draggedItem: ITEM,
                                                 targetItem: ITEM,
                                                 draggedPosition: Int,
                                                 targetPosition: Int) -> Unit

internal typealias onItemDismissedListener<ITEM> = (item: ITEM,
                                                    position: Int,
                                                    direction: Int) -> Unit

internal typealias startDragListener = (holder: RecyclerViewHolder) -> Unit

/**
 * first: scroll sensitivity to trigger the listener
 *
 * second: the listener itself
 */
internal typealias scrollUpDownPair = Pair<Int, () -> Any>

/**
 * first: the text to display
 *
 * second: the text size in pixels
 *
 * third: the resource id of the color of the text (MUST be @ColorRes)
 */
internal typealias swipeBackgroundText = Triple<String, Float, Int>
internal typealias actionModeTitleProvider = () -> String



