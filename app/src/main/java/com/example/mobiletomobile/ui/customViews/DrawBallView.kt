package com.example.mobiletomobile.ui.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class DrawBallView  // DrawBallView constructor.
    (context: Context?) : View(context) {
    // getter and setter method for currX and currY.
    // Record current ball horizontal ordinate.
    var currX = 100f

    // Record current ball vertical ordinate
    var currY = 100f

    //Record current ball width
    var width = 500F

    //Record current ball height
    var height = 500F

    //record current ball radius
    var radius = 35f
    // This is the ball color.
    var ballColor = Color.GREEN
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Create a new Paint object.
        val paint = Paint()
        // Set paint color.
        paint.color = ballColor
        // Draw a circle in the canvas.
        canvas.drawCircle(currX, currY, 35f, paint)
    }
}