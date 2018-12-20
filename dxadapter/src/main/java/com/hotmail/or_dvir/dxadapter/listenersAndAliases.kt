package com.hotmail.or_dvir.dxadapter

import android.view.View

/**
 * @return true if the callback consumed the long click, false otherwise
 */
internal typealias onItemLongClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Boolean
internal typealias onItemClickListener<ITEM> = (view: View, position: Int, item: ITEM) -> Unit
internal typealias onItemSelectStateChangedListener<ITEM> = (position: Int, item: ITEM, isSelected: Boolean) -> Unit

internal typealias emptyCallback = () -> Any
/**
 * first: scroll sensitivity to trigger the listener
 * second: the listener itself
 */
internal typealias scrollUpDownPair = Pair<Int, emptyCallback>
internal typealias actionModeTitleProvider = () -> String



