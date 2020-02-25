package ru.filit.motiv.app.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.filit.motiv.app.R


import java.util.ArrayList


class PinCodeRoundView(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(mContext, attrs, defStyleAttr) {
    private var mRoundViews: MutableList<ImageView>? = null
    var currentLength: Int = 0
        private set
    private var mEmptyDotDrawableId: Drawable? = null
    private var mFullDotDrawableId: Drawable? = null
    private var mRoundContainer: ViewGroup? = null

    init {
        initializeView(attrs, defStyleAttr)
    }

    private fun initializeView(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null && !isInEditMode) {
            val attributes = mContext.theme.obtainStyledAttributes(
                attrs, R.styleable.PinCodeView,
                defStyleAttr, 0
            )

            mEmptyDotDrawableId = attributes.getDrawable(R.styleable.PinCodeView_lp_empty_pin_dot)
            if (mEmptyDotDrawableId == null) {
                mEmptyDotDrawableId = resources.getDrawable(R.drawable.pin_code_round_empty)
            }
            mFullDotDrawableId = attributes.getDrawable(R.styleable.PinCodeView_lp_full_pin_dot)
            if (mFullDotDrawableId == null) {
                mFullDotDrawableId = resources.getDrawable(R.drawable.pin_code_round_full)
            }

            attributes.recycle()

            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.view_round_pin_code, this) as PinCodeRoundView
            mRoundContainer = view.findViewById(R.id.round_container) as ViewGroup

            mRoundViews = ArrayList()
        }
    }

    /**
     * Refresh the [android.widget.ImageView]s to look like what typed the user
     *
     * @param pinLength the current pin code length typed by the user
     */
    fun refresh(pinLength: Int) {
        currentLength = pinLength
        for (i in mRoundViews!!.indices) {
            if (pinLength - 1 >= i) {
                mRoundViews!![i].setImageDrawable(mFullDotDrawableId)
            } else {
                mRoundViews!![i].setImageDrawable(mEmptyDotDrawableId)
            }
        }
    }

    /**
     * Sets a custom empty dot drawable for the [ImageView]s.
     * @param drawable the resource Id for a custom drawable
     */
    fun setEmptyDotDrawable(drawable: Drawable) {
        mEmptyDotDrawableId = drawable
    }

    /**
     * Sets a custom full dot drawable for the [ImageView]s.
     * @param drawable the resource Id for a custom drawable
     */
    fun setFullDotDrawable(drawable: Drawable) {
        mFullDotDrawableId = drawable
    }

    /**
     * Sets a custom empty dot drawable for the [ImageView]s.
     * @param drawableId the resource Id for a custom drawable
     */
    fun setEmptyDotDrawable(drawableId: Int) {
        mEmptyDotDrawableId = resources.getDrawable(drawableId)
    }

    /**
     * Sets a custom full dot drawable for the [ImageView]s.
     * @param drawableId the resource Id for a custom drawable
     */
    fun setFullDotDrawable(drawableId: Int) {
        mFullDotDrawableId = resources.getDrawable(drawableId)
    }

    /**
     * Sets the length of the pin code.
     *
     * @param pinLength the length of the pin code
     */
    fun setPinLength(pinLength: Int = 4) {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mRoundContainer!!.removeAllViews()
        val temp = ArrayList<ImageView>(pinLength)
        for (i in 0 until pinLength) {
            val roundView: ImageView
            if (i < mRoundViews!!.size) {
                roundView = mRoundViews!![i]
            } else {
                roundView =
                    inflater.inflate(R.layout.view_round, mRoundContainer, false) as ImageView
            }
            mRoundContainer!!.addView(roundView)
            temp.add(roundView)
        }
        mRoundViews!!.clear()
        mRoundViews!!.addAll(temp)
        refresh(0)
    }
}
