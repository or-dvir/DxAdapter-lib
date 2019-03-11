package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import kotlin.math.abs

class DxRecyclerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0)
    : RecyclerView(context, attrs, defStyle)
{
    //todo test these listeners if the adapter changes during runtime!!!!

    /**
     * * [IOnAdapterItemVisibilityChanged.onVisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming the adapter contains at least 1 item).
     * * if your entire list fits inside the screen, [IOnAdapterItemVisibilityChanged.onInvisible] will NEVER trigger.
     */
    var firstItemVisibilityListener: IOnAdapterItemVisibilityChanged? = null
    /**
     * * [IOnAdapterItemVisibilityChanged.onInvisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming it does NOT fit inside the screen).
     * * if your entire list fits inside the screen, [IOnAdapterItemVisibilityChanged.onVisible]
     * will trigger immediately as the [DxRecyclerView] loads,
     * and [IOnAdapterItemVisibilityChanged.onInvisible] will NEVER trigger.
     */
    var lastItemVisibilityListener: IOnAdapterItemVisibilityChanged? = null

    /**
     * NOTE: depending on the sensitivity,
     * this will trigger MANY times while scrolling.
     *
     * [Pair.first] = speed sensitivity of the listener.
     * the larger the number, the faster the user has to scroll for the
     * listener to trigger
     *
     * [Pair.second] = the listener itself
     */
    var onScrollingDownListener: scrollUpDownPair? = null
    /**
     * NOTE: depending on the sensitivity,
     * this will trigger MANY times while scrolling.
     *
     * [Pair.first] = speed sensitivity of the listener.
     * the larger the number, the faster the user has to scroll for the
     * listener to trigger
     *
     * [Pair.second] = the listener itself
     */
    var onScrollingUpListener: scrollUpDownPair? = null

    private var notifiedFirstVisible = false
    private var notifiedFirstInvisible = false
    private var notifiedLastVisible = false
    private var notifiedLastInvisible = false

    private var mLayManLinear: LinearLayoutManager? = null
    //todo dont forget to add grid and staggered grid layout manager!!!!

    override fun setLayoutManager(layout: LayoutManager?)
    {
        super.setLayoutManager(layout)

        layout?.let {
            when(it)
            {
                is LinearLayoutManager -> mLayManLinear = it
                //todo dont forget to add grid and staggered grid layout manager!!!!
            }
        }
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

                    //todo what about HORIZONTAL recycler view? should probably use dx

                    when
                    {
                        //user is dragging up, so list is scrolling down.
                        dy > 0 ->
                            onScrollingDownListener?.let { invokeScrollUpDownListener(dy, it) }
                        //user is dragging down, so list is scrolling up.
                        dy < 0 ->
                            onScrollingUpListener?.let { invokeScrollUpDownListener(dy, it) }
                    }

                    //todo don't forget about staggered grid!!!!!!!
                    mLayManLinear?.apply {
                        //todo when documenting, note the order of the callbacks!!!
                        firstItemVisibilityListener?.let {
                            val firstPos = findFirstVisibleItemPosition()

                            //if no items, we can immediately return
                            if (firstPos == NO_POSITION)
                                return@apply

                            when
                            {
                                firstPos == 0 ->
                                {
                                    if (!notifiedFirstVisible)
                                    {
                                        it.onVisible()
                                        notifiedFirstVisible = true
                                        notifiedFirstInvisible = false
                                    }
                                }

                                //if we get here, firstPos is NOT 0
                                !notifiedFirstInvisible ->
                                {
                                    it.onInvisible()
                                    notifiedFirstVisible = false
                                    notifiedFirstInvisible = true
                                }
                            }
                        }

                        lastItemVisibilityListener?.let {
                            val lastPos = findLastVisibleItemPosition()
                            val numItems = adapter?.itemCount

                            //if no items or no adapter is attached, we can immediately return
                            if (lastPos == NO_POSITION || numItems == null)
                                return@apply

                            when
                            {
                                lastPos == (numItems -1) ->
                                {
                                    if (!notifiedLastVisible)
                                    {
                                        it.onVisible()
                                        notifiedLastVisible = true
                                        notifiedLastInvisible = false
                                    }
                                }

                                //if we get here, lastPos is NOT (numItems -1)
                                !notifiedLastInvisible ->
                                {
                                    it.onInvisible()
                                    notifiedLastVisible = false
                                    notifiedLastInvisible = true
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun invokeScrollUpDownListener(dy: Int, pair: scrollUpDownPair)
    {
        if(abs(dy) > pair.first)
            pair.second.invoke()
    }

    override fun onDetachedFromWindow()
    {
        clearOnScrollListeners()
        super.onDetachedFromWindow()
    }
}