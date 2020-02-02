package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * a wrapper for RecyclerView with scroll listeners and first/last item visibility listeners.
 */
class DxRecyclerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0)
    : RecyclerView(context, attrs, defStyle)
{
    /**
     * a listener to be invoked when the FIRST item on your list is VISIBLE.
     *
     * note that this will trigger immediately (assuming the adapter contains at least 1 item).
     *
     * also note that this will NOT trigger when filtering.
     */
    var onFirstItemVisible: emptyListener? = null
    /**
     * a listener to be invoked when the FIRST item on your list is INVISIBLE.
     *
     * note that if the entire list fits in the [DxRecyclerView], this will NEVER trigger.
     *
     * also note that this will NOT trigger when filtering.
     */
    var onFirstItemInvisible: emptyListener? = null
    /**
     * a listener to be invoked when the LAST item on your list is VISIBLE.
     *
     * note that if the entire list fits in the [DxRecyclerView], this will trigger immediately
     * (assuming the adapter contains at least 1 item).
     *
     * also note that this will NOT trigger when filtering.
     */
    var onLastItemVisible: emptyListener? = null
    /**
     * a listener to be invoked when the LAST item on your list is INVISIBLE.
     *
     * * note that if the entire list does NOT fit in the [DxRecyclerView], this will trigger immediately
     * (assuming the adapter contains at least 1 item).
     *
     * * note that if the entire list DOES fit in the [DxRecyclerView], this will NEVER trigger.
     *
     * * note that this will NOT trigger when filtering.
     */
    var onLastItemInvisible: emptyListener? = null
    /**
     * a listener to be invoked when this [DxRecyclerView] is scrolled.
     * see [DxScrollListener] for further details
     */
    var onScrollListener: DxScrollListener? = null

    private var notifiedFirstVisible = false
    private var notifiedFirstInvisible = false
    private var notifiedLastVisible = false
    private var notifiedLastInvisible = false

    //todo add support for other types of layout managers
    // note that grid layout manager extends linear layout manager.
    // staggered grid though is separate
    private var mLayMan: LayoutManager? = null

    override fun setLayoutManager(layout: LayoutManager?)
    {
        super.setLayoutManager(layout)
        mLayMan = layout
    }

    private fun atLeastOneNotNull(vararg objects: Any?) = objects.any { it != null }

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()

        addOnScrollListener(
            object : OnScrollListener()
            {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
                {
                    super.onScrolled(recyclerView, dx, dy)

                    onScrollListener?.apply {
                        when
                        {
                            dx > 0 -> invokeScrollListener(dx, DxScrollDirection.RIGHT)
                            dx < 0 -> invokeScrollListener(dx, DxScrollDirection.LEFT)

                            dy > 0 -> invokeScrollListener(dy, DxScrollDirection.DOWN)
                            dy < 0 -> invokeScrollListener(dy, DxScrollDirection.UP)
                        }
                    }

                    if (atLeastOneNotNull(onFirstItemVisible, onFirstItemInvisible,
                                          onLastItemVisible, onLastItemInvisible))
                    {
                        triggerVisibilityListeners()
                    }
                }
            })
    }

    private fun triggerVisibilityListeners()
    {
        mLayMan?.apply {

            if (this !is LinearLayoutManager)
                return@apply

            var visiblePos: Int

            if(atLeastOneNotNull(onFirstItemVisible, onFirstItemInvisible))
            {
                visiblePos = findFirstVisibleItemPosition()

                when
                {
                    visiblePos == NO_POSITION -> { /*do nothing*/ }
                    visiblePos == 0 ->
                    {
                        if (!notifiedFirstVisible)
                        {
                            onFirstItemVisible?.invoke()
                            notifiedFirstVisible = true
                            notifiedFirstInvisible = false
                        }
                    }

                    //if we get here, firstPos is NOT 0
                    !notifiedFirstInvisible ->
                    {
                        onFirstItemInvisible?.invoke()
                        notifiedFirstVisible = false
                        notifiedFirstInvisible = true
                    }
                }
            }

            if(atLeastOneNotNull(onLastItemVisible, onLastItemInvisible))
            {
                visiblePos = findLastVisibleItemPosition()
                val numItems = adapter?.itemCount

                when
                {
                    visiblePos == NO_POSITION || numItems == null -> { /*do nothing*/ }
                    visiblePos == (numItems - 1) ->
                    {
                        if (!notifiedLastVisible)
                        {
                            onLastItemVisible?.invoke()
                            notifiedLastVisible = true
                            notifiedLastInvisible = false
                        }
                    }

                    //if we get here, lastPos is NOT (numItems -1)
                    !notifiedLastInvisible ->
                    {
                        onLastItemInvisible?.invoke()
                        notifiedLastVisible = false
                        notifiedLastInvisible = true
                    }
                }
            }
        }
    }

    private fun invokeScrollListener(scrollValue: Int,
                                     direction: DxScrollDirection)
    {
        val absVal = abs(scrollValue)

        onScrollListener?.apply {
            when (direction)
            {
                DxScrollDirection.UP -> if (absVal > sensitivityUp) onScrollUp?.invoke()
                DxScrollDirection.DOWN -> if (absVal > sensitivityDown) onScrollDown?.invoke()
                DxScrollDirection.LEFT -> if (absVal > sensitivityLeft) onScrollLeft?.invoke()
                DxScrollDirection.RIGHT -> if (absVal > sensitivityRight) onScrollRight?.invoke()
            }
        }
    }

    override fun onDetachedFromWindow()
    {
        clearOnScrollListeners()
        super.onDetachedFromWindow()
    }
}