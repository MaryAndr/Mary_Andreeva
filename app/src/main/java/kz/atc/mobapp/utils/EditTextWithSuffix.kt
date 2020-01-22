package kz.atc.mobapp.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.EditText
import kz.atc.mobapp.R
import java.lang.StrictMath.max

class EditTextWithSuffix: EditText {
    private val textPaint = TextPaint()
    private var suffix:String = " Мин"
    private var suffixPadding:Float = 0f

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        getAttributes(context, attrs)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(this.text.isNotEmpty()) {
            val suffixXPosition = textPaint.measureText(this.text.toString()) + paddingLeft + suffixPadding - textPaint.measureText("   ")

            canvas?.drawText(
                suffix,
                max(suffixXPosition, suffixPadding),
                baseline.toFloat(),
                textPaint
            )
        }
    }

    /**
     * Sets the properties of the textPaint to the same as
     * the EditText view
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        textPaint.color = currentTextColor
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.LEFT
    }

    /**
     * Retrieves the attributes from the layout file
     */
    private fun getAttributes(context: Context, attrs: AttributeSet){
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithSuffix, 0, 0)

        val theSuffix = typedArray.getString(R.styleable.EditTextWithSuffix_suffix)
        theSuffix?.let {
            suffix = it
        }

        suffixPadding = typedArray.getDimension(R.styleable.EditTextWithSuffix_suffixPadding, 0f)

        typedArray.recycle()
    }
}