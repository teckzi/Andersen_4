package com.example.andersen4

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class CustomClock @JvmOverloads constructor(context: Context, private val attrs:AttributeSet? = null, defStyle:Int = 0):
    View(context,attrs,defStyle) {

    private var clockPadding = 0
    private var clockFontSize = 0f
    private var clockArrowLength = 0
    private var clockHourArrowLength = 0
    private var clockRadius = 0
    private var clockAdded = false
    private val clockNumbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()
    private val typedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomClock)
    private var defaultColor = context.resources.getColor(R.color.Coral,null)

    /** Цвет стрелок */
    private var hourArrowColor = typedArray.getColor(R.styleable.CustomClock_hourArrowColor,defaultColor)
    private var minuteArrowColor = typedArray.getColor(R.styleable.CustomClock_minuteArrowColor,defaultColor)
    private var secondsArrowColor = typedArray.getColor(R.styleable.CustomClock_secondsArrowColor,defaultColor)
    /** Центральный круг */
    private var centerCircleColor = typedArray.getColor(R.styleable.CustomClock_centerColor,defaultColor)
    /** Цифры */
    private var numbersColor = typedArray.getColor(R.styleable.CustomClock_numbersColor,defaultColor)
    private var numbersSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, typedArray.getDimension(R.styleable.CustomClock_fontSize,13f),
        resources.displayMetrics)
    /** Рамка */
    private var circleColor = typedArray.getColor(R.styleable.CustomClock_clockColor,defaultColor)
    private var circleIsAntiAlias:Boolean? = true
    private var circleStrokeWidth:Float? = typedArray.getDimension(R.styleable.CustomClock_strokeWidth,10f)
    private var circleBackgroundColor = typedArray.getColor(R.styleable.CustomClock_clockBackgroundColor,Color.WHITE)
    /** Электронные часы */
    private var showDigitalClock = typedArray.getBoolean(R.styleable.CustomClock_showDigitalClock,false)
    private var digitalClockColor = typedArray.getColor(R.styleable.CustomClock_digitalClockColor,defaultColor)

    private val paint:Paint = Paint()

    init {
        typedArray.recycle()
    }

    private fun addClock(){
        clockPadding = 50
        clockFontSize = numbersSize
        val minValue = height.coerceAtMost(width)
        clockRadius = minValue / 2 - clockPadding
        clockArrowLength = minValue / 20
        clockHourArrowLength = minValue / 7
        clockAdded = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!clockAdded){
            addClock()
        }
        drawCircle(canvas)
        drawCircleBound(canvas)
        drawCenterCircle(canvas)
        if (showDigitalClock) drawDigitalClock(canvas)
        drawNumeral(canvas)
        drawArrows(canvas)

        postInvalidateDelayed(10)
    }

    private fun drawCircle(canvas: Canvas){
        paint.run {
            reset()
            isAntiAlias = circleIsAntiAlias!!
            style = Paint.Style.FILL
            color = circleBackgroundColor
        }
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (clockRadius + clockPadding - 10).toFloat(),paint)
    }

    private fun drawCircleBound(canvas: Canvas){
        paint.run {
            color = circleColor
            strokeWidth = circleStrokeWidth!!
            style = Paint.Style.STROKE
        }

        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (clockRadius + clockPadding - 10).toFloat(),paint)
    }

    private fun drawCenterCircle(canvas: Canvas){
        paint.style = Paint.Style.FILL
        paint.color = centerCircleColor
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 12f, paint)
    }

    private fun drawNumeral(canvas: Canvas) {
        paint.run {
            textSize = clockFontSize
            color = numbersColor
            style = Paint.Style.FILL
        }
        for (i in clockNumbers) {
            val number = i.toString()
            paint.getTextBounds(number, 0, number.length, rect)
            val angle = Math.PI / 6 * (i - 3)
            val x = (width / 2 + cos(angle) * clockRadius - rect.width() / 2).toInt()
            val y = (height / 2 + sin(angle) * clockRadius + rect.height() / 2).toInt()
            canvas.drawText(number, x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun drawArrow(canvas: Canvas, loc: Double, isHour: Boolean = false,isMinute:Boolean = false) {
        when {
            isHour -> paint.color = hourArrowColor
            isMinute -> paint.color = minuteArrowColor
            else -> {paint.color = secondsArrowColor
            paint.strokeWidth = 5f}
        }
        val angle = Math.PI * loc / 30 - Math.PI / 2
        val arrowRadius =
            if (isHour) clockRadius - clockArrowLength - clockHourArrowLength
            else clockRadius - clockArrowLength
        canvas.drawLine(
            (width / 2).toFloat(), (height / 2).toFloat(),
            (width / 2 + cos(angle) * arrowRadius).toFloat(),
            (height / 2 + sin(angle) * arrowRadius).toFloat(),
            paint
        )
    }

    private fun drawArrows(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        var hour = calendar[Calendar.HOUR_OF_DAY].toFloat()
        hour = if (hour > 12) hour - 12 else hour

        drawArrow(canvas, ((hour + calendar[Calendar.MINUTE] / 60) * 5f).toDouble(), true)
        drawArrow(canvas, calendar[Calendar.MINUTE].toDouble(),isMinute = true)
        drawArrow(canvas, calendar[Calendar.SECOND].toDouble())
    }

    private fun drawDigitalClock(canvas: Canvas){
        paint.run {
            textSize = clockFontSize
            color = digitalClockColor
            textAlign = Paint.Align.CENTER
        }
        val sdf = SimpleDateFormat.getTimeInstance()
        val currentDate = sdf.format(Date())
        canvas.drawText(currentDate, (width / 2).toFloat(), (height / 2).toFloat()+150, paint)
    }

}