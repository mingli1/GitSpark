package com.gitspark.gitspark.ui.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

class RoundedBackgroundSpan(
    private val cornerRadius: Float,
    private val bgColor: Int,
    private val textColor: Int
) : ReplacementSpan() {

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val rect = RectF(x, top.toFloat(), x + measureText(paint, text, start, end), bottom.toFloat())
        paint.color = bgColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int = measureText(paint, text, start, end).roundToInt()

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int) =
        paint.measureText(text, start, end)
}