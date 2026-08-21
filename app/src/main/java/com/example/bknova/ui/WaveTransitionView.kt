package com.example.bknova.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveTransitionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        isFocusable = true
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4C5FD5")
        style = Paint.Style.FILL
    }

    private val topPath = Path()
    private val bottomPath = Path()

    /**
     * Progress from 0f (off-screen) to 1f (fully covered/met in the middle)
     */
    var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    private val waveHeight = 100f // The "waviness" amplitude
    private val waveCount = 1.5f   // How many waves across the screen

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (progress <= 0f) return

        val w = width.toFloat()
        val h = height.toFloat()
        val midH = h / 2f
        val amplitude = waveHeight

        // Top Wave: moves from above screen to slightly past middle
        val topStart = -amplitude * 2
        val topEnd = midH + amplitude
        val currentTopY = topStart + (topEnd - topStart) * progress
        drawWave(canvas, topPath, 0f, currentTopY, w, amplitude, isTop = true)

        // Bottom Wave: moves from below screen to slightly past middle
        val bottomStart = h + amplitude * 2
        val bottomEnd = midH - amplitude
        val currentBottomY = bottomStart + (bottomEnd - bottomStart) * progress
        drawWave(canvas, bottomPath, h, currentBottomY, w, amplitude, isTop = false)
    }

    private fun drawWave(
        canvas: Canvas,
        path: Path,
        baseY: Float,
        waveY: Float,
        w: Float,
        amplitude: Float,
        isTop: Boolean
    ) {
        path.reset()
        
        if (isTop) {
            path.moveTo(0f, 0f)
            path.lineTo(0f, waveY)
            
            // Draw wavy edge
            for (i in 0..w.toInt() step 5) {
                val x = i.toFloat()
                val y = waveY + Math.sin(x / w * Math.PI * 2 * waveCount).toFloat() * amplitude * (1f - progress * 0.5f)
                path.lineTo(x, y)
            }
            
            path.lineTo(w, waveY)
            path.lineTo(w, 0f)
        } else {
            path.moveTo(0f, height.toFloat())
            path.lineTo(0f, waveY)
            
            // Draw wavy edge
            for (i in 0..w.toInt() step 5) {
                val x = i.toFloat()
                // Mirror the wave phase for visual interest
                val y = waveY - Math.sin(x / w * Math.PI * 2 * waveCount + Math.PI).toFloat() * amplitude * (1f - progress * 0.5f)
                path.lineTo(x, y)
            }
            
            path.lineTo(w, waveY)
            path.lineTo(w, height.toFloat())
        }
        
        path.close()
        canvas.drawPath(path, paint)
    }
}
