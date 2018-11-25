package com.hotmail.or_dvir.dxadapter

import android.view.View

/**
 * @return true if the callback consumed the long click, false otherwise
 */
typealias onItemLongClickListener<ITEM> = ((view: View, position: Int, item: ITEM) -> Boolean)
typealias onItemClickListener<ITEM> = ((view: View, position: Int, item: ITEM) -> Unit)
typealias onItemSelectStateChangedListener<ITEM> = ((position: Int, item: ITEM, isSelected: Boolean) -> Unit)