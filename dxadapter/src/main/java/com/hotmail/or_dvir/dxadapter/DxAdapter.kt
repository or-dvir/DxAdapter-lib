package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

abstract class DxAdapter<ITEM: DxItem, VH: RecyclerViewHolder>(internal val mItems: MutableList<ITEM>)
    : RecyclerView.Adapter<VH/*RecyclerViewHolder*/>()
{

    //todo do i really need to restrict the adapter to DxItem?!?!?!??!?!
    //todo if all i need is the function "getViewType()" then there is no reason
    //todo to limit the user to extend from DxItem!!!!!!!!!!!!!!!!!!!!!

    var onClickListener: onItemClickListener<ITEM>? = null
    var onLongClickListener: onItemLongClickListener<ITEM>? = null
    var onSelectStateChangedListener: onItemSelectStateChangedListener<ITEM>? = null

//    private val mItemTypes = SparseArray<DxItem<VH>>()

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    @ColorInt
    var selectedItemBackgroundColor: Int? = null

    /**
     * default value: TRUE
     *
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on any item
     * will select\deselect the clicked item.
     *
     * if FALSE, you must manage item selection yourself using [select] and [deselect].
     *
     * @see [triggerClickListenersInSelectionMode]
     */
    var defaultItemSelectionBehavior = true

    /**
     * default value: FALSE
     *
     * if TRUE, clicking or long-clicking an item in "selection mode" (at least one item is selected)
     * would also trigger the click listener and long-click listener.
     *
     * if FALSE, those listeners would NOT be triggered in "selection mode".
     */
    var triggerClickListenersInSelectionMode = false

    /**
     * if you want to use drag and drop using a drag handle,
     * you MUST set this variable, and inside [startDragListener] call the method [ItemTouchHelper.startDrag]
     * with the given ViewHolder.
     *
     * first: the resource id of the drag handle
     *
     * second: a callback to initiate the drag event (must be done by YOU as described above)
     */
    var dragAndDropWithHandle: Pair<Int, startDragListener>? = null

//    init
//    {
//        mItems.forEach {
//            mItemTypes.apply {
//                val type = it.getItemType()
//                if (get(type) == null)
//                    put(type, it)
//            }
//        }
//    }

    override fun getItemViewType(position: Int) = mItems[position].getViewType()
    override fun getItemCount(): Int = mItems.size
    private fun isInBounds(position: Int) = position in (0 until mItems.size)

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int)
    {
        mItems[position].let { item ->
            holder.itemView.let {
                it.isSelected = item.mIsSelected
                /*item.*/bindViewHolder(holder, position, item)
            }
        }
    }

    @CallSuper
    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)

        holder.adapterPosition.let {
            if (it != RecyclerView.NO_POSITION)
            {
                /*mItems[it].*/unbindViewHolder(holder, it, mItems[it])
            }
        }
    }

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    //todo what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    //todo any other important methods i should override??????

    fun select(vararg items: ITEM) = items.forEach { select(mItems.indexOf(it)) }
    /**
     * for indices which are out of bounds - nothing happens
     */
    fun select(vararg indices: Int)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {
                    //only select if previously not selected
                    //so we don't trigger onSelectStateChangedListener unnecessarily
                    if(!mIsSelected)
                    {
                        mIsSelected = true
                        onSelectStateChangedListener?.invoke(position, this, true)
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    fun deselect(vararg items: ITEM) = items.forEach { deselect(mItems.indexOf(it)) }
    /**
     * for indices which are out of bounds - nothing happens
     */
    fun deselect(vararg indices: Int)
    {
        indices.forEach { position ->
            if (isInBounds(position))
            {
                mItems[position].apply {
                    //only deselect if previously selected
                    //so we don't trigger onSelectStateChangedListener unnecessarily
                    if(mIsSelected)
                    {
                        mIsSelected = false
                        onSelectStateChangedListener?.invoke(position, this, false)
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    /**
     * "selection mode" means at least one item is selected
     */
    private fun isInSelectionMode() = mItems.find { it.mIsSelected } != null

    fun getAllSelectedItems() = mItems.filter { it.mIsSelected }
    fun getNumSelectedItems() = getAllSelectedItems().size
    fun getAllSelectedIndices(): List<Int>
    {
        return mItems.mapIndexed { index, item ->
            if (item.mIsSelected)
                index
            else
                null
        }.filterNotNull()
    }

    @ColorInt
    private fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val context = parent.context

        val itemView = LayoutInflater
                .from(context)
                .inflate(getItemLayoutRes(parent, viewType), parent, false)

        //only change the background if user chose default behavior.
        //this is to prevent overriding users' custom background (if set)
        if(defaultItemSelectionBehavior)
        {
            //todo when documenting, mention that the background will be overridden when item is "selected",
            //todo meaning that it will change the background to the selected color.
            //todo if user has custom selection background that is NOT a color, he should NOT use defaultItemSelectionBehavior
            //todo but then must handle other things by himself.
            StateListDrawable().apply {
                //selected
                addState(intArrayOf(android.R.attr.state_selected),
                         ColorDrawable(selectedItemBackgroundColor ?: getThemeAccentColorInt(context)))
                //not selected
                addState(intArrayOf(-android.R.attr.state_selected), itemView.background)
                itemView.background = this
            }
        }

        val holder = createAdapterViewHolder(itemView, parent, viewType)

        dragAndDropWithHandle?.let {
            //this line is needed for the compiler
            itemView.findViewById<View>(it.first).setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN)
                    it.second.invoke(holder)

                //allow normal processing to continue
                false
            }
        }

        //NOTE:
        //we CANNOT have "clickedPosition" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
        itemView.setOnClickListener { view ->
            val clickedPosition = holder.adapterPosition
            val clickedItem = mItems[clickedPosition]

            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first click listener????
            //todo example: if first selection, then click listener is AFTER the item has been selected/deselected

            val selectionModeBefore = isInSelectionMode()

            //change selection state only if user asked for default behavior AND
            //we are already in selection mode (because default selection mode start with a LONG-click)
            if(defaultItemSelectionBehavior && selectionModeBefore)
            {
                //reverse the selection
                if(clickedItem.mIsSelected)
                    deselect(clickedPosition)
                else
                    select(clickedPosition)
            }

            //if we were NOT in selection mode before -> regular click -> trigger the listener.
            //if we WERE in selection mode before and the user
            //requested it (right operand of the "OR" condition) -> trigger the listener.
            if(!selectionModeBefore || triggerClickListenersInSelectionMode)
                onClickListener?.invoke(view, clickedPosition, clickedItem)

        }

/////////////////////////////////ORIGINAL REGULAR CLICK LISTENER CODE START/////////////////////////////////
        //NOTE:
        //we CANNOT have "clickedPosition" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
//        onClickListener?.apply {
//            itemView.setOnClickListener {
//                val clickedPosition = holder.adapterPosition
//                val clickedItem = mItems[clickedPosition]
//
//                //WARNING:
//                //do NOT save the state of isInSelectionMode() into a variable here
//                //because below this line we are changing the selected state of the clicked item
//                //and the variable would not be updates according to the new state
//
//                //todo when documenting this library, notice the order of the calls
//                //todo first selection listener or first click listener????
//
//                val selectionBefore = isInSelectionMode()
//
//                if(defaultItemSelectionBehavior && selectionBefore)
//                {
//                    //reverse the selection
//                    if(clickedItem.mIsSelected)
//                        deselect(clickedPosition)
//                    else
//                        select(clickedPosition)
//                }
//
//                val selectionAfter = isInSelectionMode()
//                val deselectedLastItem = selectionBefore && !selectionAfter
//
//                //triggering the click listener
//                when
//                {
//                    !selectionAfter && !deselectedLastItem -> invoke(it, clickedPosition, clickedItem)
//                    //if we get here, we ARE in "selection mode".
//                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
//                }
//            }
//        }
/////////////////////////////////ORIGINAL REGULAR CLICK LISTENER CODE END/////////////////////////////////



        itemView.setOnLongClickListener { view ->
            val clickedPosition = holder.adapterPosition
            val clickedItem = mItems[clickedPosition]

            //WARNING:
            //do NOT save the state of isInSelectionMode() into a variable here
            //because below we are changing the selected state of the clicked item
            //and the variable would not be updates according to the new state

            //todo when documenting this library, notice the order of the calls
            //todo first selection listener or first long-click listener????

            //only select an item on long-click if defaultItemSelectionBehavior AND
            //we are not already in selection mode (if we ARE already in selection mode,
            //selection is handled by REGULAR clicks)
            if (defaultItemSelectionBehavior &&
//                !clickedItem.mIsSelected &&
                !isInSelectionMode())
            {
                select(clickedPosition)
            }

            onLongClickListener?.let {
                //if we are NOT in selection mode -> regular long-click -> trigger the listener.
                //if we ARE in selection mode and the user
                //requested it (right operand of the "OR" condition) -> trigger the listener.
                if (!isInSelectionMode() || triggerClickListenersInSelectionMode)
                    it.invoke(view, clickedPosition, clickedItem)

                //in any other case we need a boolean return value
                else
                    true
            } ?: true
        }




/////////////////////////////////ORIGINAL LONG CLICK LISTENER CODE START/////////////////////////////////
//        onLongClickListener?.apply {
//            itemView.setOnLongClickListener {
//                val clickedPosition = holder.adapterPosition
//                val clickedItem = mItems[clickedPosition]
//
//                //WARNING:
//                //do NOT save the state of isInSelectionMode() into a variable here
//                //because below this line we are changing the selected state of the clicked item
//                //and the variable would not be updates according to the new state
//
//                //todo when documenting this library, notice the order of the calls
//                //todo first selection listener or first long-click listener????
//
//                //long-click should NOT select items if:
//                //1) defaultItemSelectionBehavior is false
//                //2) the item is already selected (this would trigger unnecessary callbacks and UI updates).
//                //3) at least one item is already selected (we are in "selection mode" where regular clicks
//                //   should select/deselect an item)
//                if (defaultItemSelectionBehavior &&
//                    !clickedItem.mIsSelected &&
//                    !isInSelectionMode())
//                {
//                    select(clickedPosition)
//                }
//
//                //triggering the long-click listener
//                when
//                {
//                    !isInSelectionMode() -> invoke(it, clickedPosition, clickedItem)
//                    //if we get here, we ARE in "selection mode".
//                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
//                    //consume the event
//                    else -> true
//                }
//            }
//        }
/////////////////////////////////ORIGINAL LONG CLICK LISTENER CODE END/////////////////////////////////

        return holder
    }

//    /**
//     * override this function if you want a custom ViewHolder (for example if you want to attach
//     * listeners to the individual views of an item).
//     *
//     * to get the model object from inside those listeners, use [mItems] and [getAdapterPosition()]
//     * [RecyclerView.ViewHolder.getAdapterPosition]
//     */
    abstract fun createAdapterViewHolder(itemView: View, parent: ViewGroup, viewType: Int): VH
    @LayoutRes
    abstract fun getItemLayoutRes(parent: ViewGroup, viewType: Int): Int
    abstract fun bindViewHolder(holder: VH, position: Int, item: ITEM)
    abstract fun unbindViewHolder(holder: VH, position: Int, item: ITEM)
}