package com.practicum.playlistmaker.presentation.player.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.practicum.playlistmaker.R
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.min
import kotlin.math.roundToInt

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var isPlaying = false
    private var tintColor: Int? = null
    private val playRect = RectF()
    private val pauseRect = RectF()

    init {
        isClickable = true
        context.withStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        ) {
            val playResId = getResourceId(
                R.styleable.PlaybackButtonView_playImage,
                0
            )
            val pauseResId = getResourceId(
                R.styleable.PlaybackButtonView_pauseImage,
                0
            )
            tintColor = getColor(R.styleable.PlaybackButtonView_playbackTint, 0)
                .takeIf { hasValue(R.styleable.PlaybackButtonView_playbackTint) }
            playDrawable = loadDrawable(playResId)
            pauseDrawable = loadDrawable(pauseResId)
        }
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying == playing) return
        isPlaying = playing
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = if (isPlaying) pauseDrawable else playDrawable
        val dest = if (isPlaying) pauseRect else playRect
        if (drawable == null || dest.isEmpty) return

        drawable.bounds = Rect(
            dest.left.roundToInt(),
            dest.top.roundToInt(),
            dest.right.roundToInt(),
            dest.bottom.roundToInt()
        )
        drawable.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                toggleState()
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = playDrawable ?: pauseDrawable
        val desiredWidth = (drawable?.intrinsicWidth?.takeIf { it > 0 } ?: 0) + paddingLeft + paddingRight
        val desiredHeight = (drawable?.intrinsicHeight?.takeIf { it > 0 } ?: 0) + paddingTop + paddingBottom
        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateRect(playDrawable, playRect, w, h)
        updateRect(pauseDrawable, pauseRect, w, h)
    }

    private fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }

    private fun loadDrawable(resId: Int): Drawable? {
        if (resId == 0) return null
        val drawable = AppCompatResources.getDrawable(context, resId)?.mutate() ?: return null
        val wrapped = DrawableCompat.wrap(drawable)
        tintColor?.let { DrawableCompat.setTint(wrapped, it) }
        return wrapped
    }

    private fun updateRect(drawable: Drawable?, rect: RectF, width: Int, height: Int) {
        if (drawable == null) {
            rect.setEmpty()
            return
        }
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        if (availableWidth <= 0 || availableHeight <= 0) {
            rect.setEmpty()
            return
        }
        val drawableWidth = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
        val drawableHeight = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
        val scale = min(
            availableWidth.toFloat() / drawableWidth,
            availableHeight.toFloat() / drawableHeight
        )
        val scaledWidth = drawableWidth * scale
        val scaledHeight = drawableHeight * scale
        val left = paddingLeft + (availableWidth - scaledWidth) / 2f
        val top = paddingTop + (availableHeight - scaledHeight) / 2f
        rect.set(left, top, left + scaledWidth, top + scaledHeight)
    }

}
