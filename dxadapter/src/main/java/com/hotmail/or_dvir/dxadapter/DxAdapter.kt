package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.SparseArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

<<<<<<< HEAD
class DxAdapter<ITEM: DxItem<VH>, VH: RecyclerViewHolder>(/*internal*/ val mItems: List<ITEM>)
    : RecyclerView.Adapter<VH>()
=======
open class DxAdapter<ITEM: DxItem/*<SimpleViewHolder>*/>(internal val mItems: List<ITEM>)
    : RecyclerView.Adapter<SimpleViewHolder>()
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
{
    var onClickListener: onItemClickListener<ITEM>? = null
    var onLongClickListener: onItemLongClickListener<ITEM>? = null
    var onSelectStateChangedListener: onItemSelectStateChangedListener<ITEM>? = null

    private val mItemTypes = SparseArray<DxItem<VH>>()

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    @ColorRes
    var selectedItemBackgroundColorRes: Int? = null

    /**
     * default value: TRUE
     *
     * if TRUE, long-clicking an item will select it and any subsequent regular-click on any item
     * will select\deselect the clicked item.
     *
     * NOTE: in order for this to work, you MUST ALSO set [onItemLongClickListener]
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

    init
    {
        mItems.forEach {
            mItemTypes.apply {
                val type = it.getItemType()
                if (get(type) == null)
                    put(type, it)
            }
        }
    }

    override fun getItemCount(): Int = mItems.size
    private fun isInBounds(position: Int) = position in (0 until mItems.size)

    @CallSuper
<<<<<<< HEAD
    override fun onBindViewHolder(holder: VH, position: Int)
=======
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int)
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
    {
        mItems[position].let {
            holder.itemView.isSelected = it.mIsSelected
            it.bindViewHolder(holder)
        }
    }

<<<<<<< HEAD
    @CallSuper
    override fun onViewRecycled(holder: VH)
=======
    override fun onViewRecycled(holder: SimpleViewHolder)
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
    {
        super.onViewRecycled(holder)

        holder.adapterPosition.let {
            if (it != RecyclerView.NO_POSITION)
                mItems[it].unbindViewHolder(holder)
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

<<<<<<< HEAD
    /**
     * @return the LAYOUT ID of the item
     */
    override fun getItemViewType(position: Int) = mItems[position].getItemType()

    /**
     * @param viewType the LAYOUT ID to inflate
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
=======
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder
    {
        //TODO NOTE:
        //TODO THE BUG WHERE ITEMS WILL NOT SAVE STATE HAS SOMETHING TO DO WITH
        //TODO TAKING THE FIRST ITEM IN THE LIST!!!!!
        //todo check how fast adapter does this!!!
        //TODO THEORY:
        //todo could be a COMBINATION of using the first item AND that the viewHolder is
        //todo an inner class which means it holds a reference to the outer class
        //todo so the view holder of the first item holds a reference to the data of the first item in the list!!!

//        if i remove the saving of the data from the view holder (e.g. with eventbus)
//        then the state is saved!!!!!!

        val firstItem = mItems.first()
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
        val context = parent.context

        val itemView = LayoutInflater
                .from(context)
<<<<<<< HEAD
                .inflate(mItemTypes[viewType].getLayoutRes(), parent, false)
=======
                .inflate(firstItem.getLayoutRes(), parent, false)
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter

        StateListDrawable().apply {
            //replacement method requires API 23 (lib min is 21)
            @Suppress("DEPRECATION")
            val selectedColorInt =
                if (selectedItemBackgroundColorRes != null)
                    //for sure selectedItemBackgroundColorRes is NOT null because of the "if"
                    context.resources.getColor(selectedItemBackgroundColorRes!!)
                else
                    getThemeAccentColorInt(context)

            //selected
            addState(intArrayOf(android.R.attr.state_selected),
                     ColorDrawable(selectedColorInt))

            itemView.background = this
        }

<<<<<<< HEAD
        itemView.tag = this
        val holder = mItemTypes[viewType].createViewHolder(itemView)
=======
        val holder = createAdapterViewHolder(itemView, parent, viewType)
            ?: SimpleViewHolder(itemView)//firstItem.createViewHolder(itemView)

//        val holder = firstItem.createViewHolder(itemView)
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter

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
        //we CANNOT have "position" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
        onClickListener?.apply {
            itemView.setOnClickListener {
                val clickedPosition = holder.adapterPosition
                val clickedItem = mItems[clickedPosition]

                //WARNING:
                //do NOT save the state of isInSelectionMode() into a variable here
                //because below this line we are changing the selected state of the clicked item
                //and the variable would not be updates according to the new state

                //todo when documenting this library, notice the order of the calls
                //todo first selection listener or first click listener????

                val selectionBefore = isInSelectionMode()

                if(defaultItemSelectionBehavior && isInSelectionMode())
                {
                    //reverse the selection
                    if(clickedItem.mIsSelected)
                        deselect(clickedPosition)
                    else
                        select(clickedPosition)
                }

                val selectionAfter = isInSelectionMode()
                val deselectedLastItem = selectionBefore && !selectionAfter

                //triggering the click listener
                when
                {
                    !selectionAfter && !deselectedLastItem -> invoke(it, clickedPosition, clickedItem)
                    //if we get here, we ARE in "selection mode".
                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
                }
            }
        }

        onLongClickListener?.apply {
            itemView.setOnLongClickListener {
                val clickedPosition = holder.adapterPosition
                val clickedItem = mItems[clickedPosition]

                //WARNING:
                //do NOT save the state of isInSelectionMode() into a variable here
                //because below this line we are changing the selected state of the clicked item
                //and the variable would not be updates according to the new state

                //todo when documenting this library, notice the order of the calls
                //todo first selection listener or first long-click listener????

                //long-click should NOT select items if:
                //1) defaultItemSelectionBehaviour is false
                //2) the item is already selected (this would trigger unnecessary callbacks and UI updates).
                //3) at least one item is already selected (we are in "selection mode" where regular clicks
                //   should select/deselect an item)
                if (defaultItemSelectionBehavior &&
                    !clickedItem.mIsSelected &&
                    !isInSelectionMode())
                {
                    select(clickedPosition)
                }

                //triggering the long-click listener
                when
                {
                    !isInSelectionMode() -> invoke(it, clickedPosition, clickedItem)
                    //if we get here, we ARE in "selection mode".
                    triggerClickListenersInSelectionMode -> invoke(it, clickedPosition, clickedItem)
                    //consume the event
                    else -> true
                }
            }
        }

        return holder
    }
<<<<<<< HEAD
=======

    /**
     * override this function if you want a custom ViewHolder (for example if you want to attach
     * listeners to the individual views of an item).
     *
     * to get the model object from inside those listeners, use [mItems] and [getAdapterPosition()]
     * [RecyclerView.ViewHolder.getAdapterPosition]
     */
    open fun createAdapterViewHolder(itemView: View, parent: ViewGroup, viewType: Int): SimpleViewHolder? = null
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
}