package com.example.oboepitch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PitchWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val pitchLabels = listOf(
        "C", "C#", "D", "D#", "E", "F",
        "F#", "G", "G#", "A", "A#", "B"
    )

    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 40f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (min(width, height) / 2f) - 60f

        pitchLabels.forEachIndexed { i, label ->
            val angleRad = Math.toRadians((i * 360.0 / 12) - 90.0) // Start at top
            val x = (centerX + cos(angleRad) * radius).toFloat()
            val y = (centerY + sin(angleRad) * radius + 15f) // Shift down for text height
            canvas.drawText(label, x, y.toFloat(), textPaint)
        }
    }
}