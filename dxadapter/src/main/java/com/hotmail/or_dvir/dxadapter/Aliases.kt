package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @return true if the callback consumed the long click, false otherwise
 */
internal typealias onItemLongClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Boolean
internal typealias onItemClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Unit
internal typealias onItemSelectStateChangedListener<ITEM> = (position: Int, item: ITEM, isSelected: Boolean) -> Unit

internal typealias onItemsMovedListener<ITEM> = (draggedItem: ITEM,
                                                 targetItem: ITEM,
                                                 draggedPosition: Int,
                                                 targetPosition: Int) -> Unit

internal typealias emptyCallback = () -> Any

internal typealias RecyclerViewHolder = RecyclerView.ViewHolder

/**
 * first: scroll sensitivity to trigger the listener
 * second: the listener itself
 */
internal typealias scrollUpDownPair = Pair<Int, emptyCallback>
internal typealias actionModeTitleProvider = () -> String



