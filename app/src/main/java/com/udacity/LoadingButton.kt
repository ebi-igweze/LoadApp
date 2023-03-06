package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var textRect = Rect()

    private val backgroundColor: Int
    private val textColor: Int
    private val loadingColor: Int
    private val circularLoaderColor: Int
    private val paint: Paint

    private val valueAnimator: ValueAnimator
    private var animationProgress = 0f
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, old, new ->
        contentDescription = new::class.java.name
        // start loading animation
        if (buttonState == ButtonState.Loading)
            startLoadingAnimation()
    }


    init {
        isClickable = true

        // load custom attributes
        val customAttrs = context.theme.obtainStyledAttributes(
            attrs, R.styleable.LoadingButton, 0, 0)

        // get button background color
        backgroundColor = customAttrs.getColor(
            R.styleable.LoadingButton_backgroundColor,
            ContextCompat.getColor(context, R.color.colorAccent)
        )

        // get button text color
        textColor = customAttrs.getColor(
            R.styleable.LoadingButton_textColor,
            ContextCompat.getColor(context, R.color.white)
        )

        // get button loading color
        loadingColor = customAttrs.getColor(
            R.styleable.LoadingButton_loadingColor,
            ContextCompat.getColor(context, R.color.colorPrimaryDark)
        )

        circularLoaderColor =  ContextCompat.getColor(context, R.color.colorAccent)
        customAttrs.recycle()


        paint =  Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 55.0f
            typeface = Typeface.create( "", Typeface.BOLD)
        }

        // create animator with 0-100 progress
        valueAnimator =  ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = 2000
        // add animator update listener
        valueAnimator.addUpdateListener {
            animationProgress = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = backgroundColor
        // draw button background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val buttonText = resources.getString(buttonState.text)
        if (buttonState == ButtonState.Loading) {
            paint.color = loadingColor
            val progressWidth = width * (animationProgress / 100)
            // show loading background if loading
            canvas.drawRect(0f, 0f, progressWidth, height.toFloat(), paint)

            // show circular loader
            // set circle loader color
            paint.color = circularLoaderColor
            // get the bounds for the text
            paint.getTextBounds(buttonText, 0, buttonText.length - 1, textRect)
            // use button center and text center to get left edge, and some 30f padding
            val left = (width / 2f) + (textRect.right / 2f) + 30f

            // compute the values for the animated arc
            val top = (height / 2f) - 30f
            val right = left + (paint.textSize)
            val bottom = top + (paint.textSize)
            val startAngle = 0f
            val sweepAngle = (360f * animationProgress) / 100f
            // draw circular progress
            canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, true, paint)
        }


        // show button text
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2f), (height + 30f) / 2f, paint)
    }

    private fun startLoadingAnimation() {
        isClickable = false
        valueAnimator.start()
    }

    fun completeDownload() {
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        isClickable  = true
        // invalidate to trigger redraw
        invalidate()
    }

}