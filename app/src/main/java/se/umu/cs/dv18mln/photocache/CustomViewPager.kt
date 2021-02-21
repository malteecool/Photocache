package se.umu.cs.dv18mln.photocache

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Class to represent a custom viewpager. The class is made to handle
 * when the viewpager should be "swipable" and not.
 */
class CustomViewPager(context: Context, attrs: AttributeSet): ViewPager(context, attrs){
    private var swipeEnabled = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onTouchEvent(event)
            false -> false
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onInterceptTouchEvent(event)
            false -> false
        }
    }

    /**
     * Changes the "swiping" condition.
     */
    fun setSwipePagingEnabled(swipeEnabled: Boolean) {
        this.swipeEnabled = swipeEnabled
    }

}