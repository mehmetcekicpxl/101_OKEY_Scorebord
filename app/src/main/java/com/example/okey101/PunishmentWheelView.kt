package com.example.okey101

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PunishmentWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    
    // Specified punishments
    private val items = listOf("Tatlı", "Çay", "Kahve", "Çerez", "Çorba", "Çiğköfte")
    
    // Colors for segments
    private val colors = listOf(
        Color.parseColor("#FFD700"), // Gold
        Color.parseColor("#2E7D32"), // Green
        Color.parseColor("#C62828"), // Red
        Color.parseColor("#1565C0"), // Blue
        Color.parseColor("#EF6C00"), // Orange
        Color.parseColor("#6A1B9A")  // Purple
    )

    init {
        textPaint.color = Color.WHITE
        textPaint.textSize = 40f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = Math.min(width, height) / 2 * 0.9f
        val cx = width / 2
        val cy = height / 2

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)

        val sweepAngle = 360f / items.size

        for (i in items.indices) {
            paint.color = colors[i % colors.size]
            paint.style = Paint.Style.FILL
            
            val startAngle = i * sweepAngle
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)

            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)

            // Draw Text
            val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val textRadius = radius * 0.7f
            val tx = (cx + textRadius * Math.cos(angleRad)).toFloat()
            val ty = (cy + textRadius * Math.sin(angleRad)).toFloat() + (textPaint.textSize / 3)

            canvas.drawText(items[i], tx, ty, textPaint)
        }
        
        // Draw Center Circle (Hub)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, radius * 0.1f, paint)
    }
    
    fun getItemAtAngle(angle: Float): String {
        // angle is total rotation degrees
        // Normalize angle to [0, 360)
        val normalizedAngle = (angle % 360)
        
        // The pointer is at 270 degrees (Top) or 0 (Right)?
        // Usually, Views rotate clockwise.
        // If we rotate the VIEW, the "Top" fixed pointer stays at 270 relative to the screen.
        // Relative to the view, the pointer moves counter-clockwise.
        
        // Let's assume a pointer at the RIGHT (0 degrees) for simplicity of math, 
        // OR simpler: A pointer at the TOP (270 degrees).
        
        // If View is rotated by R degrees.
        // The item at 270 degrees (Top) is:
        // (270 - R) % 360
        
        var effectiveAngle = (270 - normalizedAngle) % 360
        if (effectiveAngle < 0) effectiveAngle += 360
        
        val sweepAngle = 360f / items.size
        val index = (effectiveAngle / sweepAngle).toInt()
        
        return items[index.coerceIn(0, items.size - 1)]
    }
}
