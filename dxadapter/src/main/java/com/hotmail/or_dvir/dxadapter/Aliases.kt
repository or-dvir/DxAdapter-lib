package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView
import android.view.View


typealias RecyclerViewHolder = RecyclerView.ViewHolder

/**
 * @return true if the callback consumed the long click, false otherwise
 */
internal typealias onItemLongClickListener<ITEM> = (view: View, adapterPosition: Int, item: ITEM) -> Boolean
internal typealias onItemClickListener<ITEM> = (view: View, adapterPosition: Int, item: ITEM) -> Unit

internal typealias  positionAndItemListener<ITEM> = (adapterPosition: Int, item: ITEM) -> Unit

internal typealias onItemSelectStateChangedListener<ITEM> =
        (adapterPosition: Int, item: ITEM, isSelected: Boolean) -> Unit

internal typealias dxFilter<ITEM> = (constraint: CharSequence) -> List<ITEM>

internal typealias onItemsMoveListener<ITEM> = (draggedItem: ITEM,
                                                targetItem: ITEM,
                                                draggedPosition: Int,
                                                targetPosition: Int) -> Unit

internal typealias onItemSwipedListener<ITEM> = (item: ITEM,
                                                 adapterPosition: Int,
                                                 direction: Int) -> Unit

internal typealias startDragListener = (holder: RecyclerViewHolder) -> Unit

/**
 * first: scroll sensitivity to trigger the listener
 *
 * second: the listener itself
 */
internal typealias scrollUpDownPair = Pair<Int, () -> Any>

internal typealias actionModeTitleProvider = () -> String



