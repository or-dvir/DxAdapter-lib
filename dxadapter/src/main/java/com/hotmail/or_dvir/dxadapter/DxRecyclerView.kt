package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import kotlin.math.abs

i stopped here with the documenttaion
class DxRecyclerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0)
    : RecyclerView(context, attrs, defStyle)
{
    //todo test these listeners if the adapter changes during runtime!!!!

    /**
     * * [DxItemVisibilityListener.onItemVisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming the adapter contains at least 1 item).
     * * if the entire list fits in the screen, [DxItemVisibilityListener.onItemInvisible] will NEVER trigger.
     */
    var firstItemVisibilityListener: DxItemVisibilityListener? = null

    /**
     * * [DxItemVisibilityListener.onItemInvisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming it does NOT fit in the screen).
     * * if the entire list fits in the screen, [DxItemVisibilityListener.onItemVisible]
     * will trigger immediately as the [DxRecyclerView] loads,
     * and [IOnItemVisibilityChanged.onInvisible] will NEVER trigger.
     */
    var lastItemVisibilityListener: DxItemVisibilityListener? = null

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

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()

        //no need to waste runtime on ScrollListener if
        //no callbacks have been set
        if (firstItemVisibilityListener != null ||
            lastItemVisibilityListener  != null)
        {
            addOnScrollListener(object : OnScrollListener()
            {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
                {
                    super.onScrolled(recyclerView, dx, dy)
                    triggerScrollListeners(dx, dy)
                    triggerVisibilityListeners()
                }
            })
        }
    }

    private fun triggerVisibilityListeners()
    {
        mLayMan?.apply {

            if (this !is LinearLayoutManager)
                return@apply

            var visiblePos: Int

            firstItemVisibilityListener?.let {
                visiblePos = findFirstVisibleItemPosition()

                //if no items, we can immediately return
                if (visiblePos == NO_POSITION)
                    return@let

                when
                {
                    visiblePos == 0 ->
                    {
                        if (!notifiedFirstVisible)
                        {
                            it.onItemVisible?.invoke()
                            notifiedFirstVisible = true
                            notifiedFirstInvisible = false
                        }
                    }

                    //if we get here, firstPos is NOT 0
                    !notifiedFirstInvisible ->
                    {
                        it.onItemInvisible?.invoke()
                        notifiedFirstVisible = false
                        notifiedFirstInvisible = true
                    }
                }
            }

            lastItemVisibilityListener?.let {
                visiblePos = findLastVisibleItemPosition()
                val numItems = adapter?.itemCount

                //if no items or no adapter is attached, we can immediately return
                if (visiblePos == NO_POSITION || numItems == null)
                    return@let

                when
                {
                    visiblePos == (numItems - 1) ->
                    {
                        if (!notifiedLastVisible)
                        {
                            it.onItemVisible?.invoke()
                            notifiedLastVisible = true
                            notifiedLastInvisible = false
                        }
                    }

                    //if we get here, lastPos is NOT (numItems -1)
                    !notifiedLastInvisible ->
                    {
                        it.onItemInvisible?.invoke()
                        notifiedLastVisible = false
                        notifiedLastInvisible = true
                    }
                }
            }
        }
    }

    private fun triggerScrollListeners(dx: Int, dy: Int)
    {
        when
        {
            dx > 0 -> invokeScrollListener(dx, DxScrollDirection.RIGHT)
            dx < 0 -> invokeScrollListener(dx, DxScrollDirection.LEFT)

            dy > 0 -> invokeScrollListener(dy, DxScrollDirection.DOWN)
            dy < 0 -> invokeScrollListener(dy, DxScrollDirection.UP)
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