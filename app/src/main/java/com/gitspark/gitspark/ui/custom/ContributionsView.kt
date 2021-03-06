package com.gitspark.gitspark.ui.custom

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Contribution
import java.util.*
import kotlin.math.ceil

private const val START_POS = 48f
private const val SQUARE_SIZE = 75f
private const val PADDING_SQUARE = 16f
private const val PADDING_TEXT = 48f
private const val MONTH_TEXT_SIZE = 40f
private const val PADDING_BETWEEN_MONTHS = 64f

class ContributionsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val monthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = MONTH_TEXT_SIZE
    }
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null
    private val bitmapMatrix = Matrix()

    private var viewWidth = 1
    private var viewHeight = 1

    private val bgColor: Int

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ContributionsView, 0, 0).apply {
            try {
                bgColor = getInteger(R.styleable.ContributionsView_bgColor, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(bgColor)
        bitmap?.let { canvas.drawBitmap(it, bitmapMatrix, bitmapPaint) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewWidth = w
        viewHeight = h
    }

    fun createBitmap(contributions: SortedMap<String, List<Contribution>>) {
        if (contributions.isEmpty()) return

        val mode = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        val darkMode = mode == Configuration.UI_MODE_NIGHT_YES

        if (bitmap == null) {
            var viewHeight = 0f
            contributions.forEach { month ->
                viewHeight += ceil(month.value.size / 7f) *
                        (SQUARE_SIZE + PADDING_SQUARE) + PADDING_BETWEEN_MONTHS * 1.1f
            }

            bitmap = Bitmap.createBitmap(viewWidth, viewHeight.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(checkNotNull(bitmap) { "Bitmap was not created." })

            var monthIndex = 0
            var startY = START_POS
            contributions.forEach { month ->
                val numRows = ceil(month.value.size / 7f)

                monthPaint.color = if (darkMode) Color.parseColor("#F9F9F9") else Color.BLACK
                val textWidth = monthPaint.measureText(month.key)
                val textX = viewWidth - PADDING_TEXT - textWidth
                val textY = startY + (SQUARE_SIZE + PADDING_SQUARE) * numRows
                canvas.drawText(month.key, textX, textY, monthPaint)

                month.value.forEachIndexed { index, contribution ->
                    paint.color = if (darkMode && (contribution.fillColor == "#eeeeee" || contribution.fillColor == "#ebedf0")) {
                        Color.parseColor("#363636")
                    } else {
                        Color.parseColor(contribution.fillColor)
                    }

                    val left = START_POS + ((index % 7) * (SQUARE_SIZE + PADDING_SQUARE))
                    val top = startY + PADDING_SQUARE + ((numRows - 1 - (index / 7)) * (SQUARE_SIZE + PADDING_SQUARE))
                    val right = left + SQUARE_SIZE
                    val bottom = top + SQUARE_SIZE

                    canvas.drawRect(left, top, right, bottom, paint)
                }

                monthIndex++
                startY += numRows * (SQUARE_SIZE + PADDING_SQUARE) + PADDING_BETWEEN_MONTHS
            }
            val lp = layoutParams.apply { height = viewHeight.toInt() }
            layoutParams = lp

            postInvalidate()
        }
    }
}