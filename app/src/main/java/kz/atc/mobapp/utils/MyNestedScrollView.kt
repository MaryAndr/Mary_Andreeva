package kz.atc.mobapp.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import java.lang.reflect.Field
import kotlin.math.abs


class MyNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private val mScroller: OverScroller?
    var isFling = false

    private val overScroller: OverScroller?
        get() {
            var fs: Field? = null
            try {
                fs = this.javaClass.superclass.getDeclaredField("mScroller")
                fs!!.setAccessible(true)
                return fs!!.get(this) as OverScroller
            } catch (t: Throwable) {
                return null
            }

        }

    init {
        mScroller = overScroller
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY)

        if (childCount > 0) {
            ViewCompat.postInvalidateOnAnimation(this)
            isFling = true
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        if (isFling) {
            if (abs(t - oldt) <= 3 || t == 0 || t == getChildAt(0).measuredHeight - measuredHeight) {
                isFling = false
                mScroller?.abortAnimation()
            }
        }
    }

}