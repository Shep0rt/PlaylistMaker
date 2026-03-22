package com.practicum.playlistmaker.presentation.player.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.practicum.playlistmaker.R
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.min
import androidx.core.graphics.createBitmap

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying = false
    private var tintColor: Int? = null

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
            playBitmap = loadBitmap(playResId)
            pauseBitmap = loadBitmap(pauseResId)
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
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        if (bitmap == null) return

        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        if (availableWidth <= 0 || availableHeight <= 0) return

        val scale = min(
            availableWidth.toFloat() / bitmap.width,
            availableHeight.toFloat() / bitmap.height
        )
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale
        val left = paddingLeft + (availableWidth - scaledWidth) / 2f
        val top = paddingTop + (availableHeight - scaledHeight) / 2f
        val dest = RectF(left, top, left + scaledWidth, top + scaledHeight)

        canvas.drawBitmap(bitmap, null, dest, null)
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
        val bitmap = playBitmap ?: pauseBitmap
        val desiredWidth = (bitmap?.width ?: 0) + paddingLeft + paddingRight
        val desiredHeight = (bitmap?.height ?: 0) + paddingTop + paddingBottom
        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }

    private fun loadBitmap(resId: Int): Bitmap? {
        if (resId == 0) return null
        val drawable = AppCompatResources.getDrawable(context, resId)?.mutate() ?: return null
        val wrapped = DrawableCompat.wrap(drawable)
        tintColor?.let { DrawableCompat.setTint(wrapped, it) }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        wrapped.setBounds(0, 0, width, height)
        wrapped.draw(canvas)
        return bitmap
    }

}