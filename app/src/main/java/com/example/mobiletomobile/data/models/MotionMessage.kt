package com.example.mobiletomobile.data.models

class MotionMessage {
    var xCoordinate: Float? = null
    var yCoordinate: Float? = null
    var action: Int? = null
    var color: Int? = null
    var screenHeight: Int? = null
    var height: Float? = null
    var width: Float? = null
    var radius: Float? = null

    constructor() {}
    constructor(
        xCoordinate: Float?,
        yCoordinate: Float?,
        action: Int?,
        color: Int?,
        screenHeight: Int?,
        drawBallHeight: Float?,
        drawBallWidth: Float?,
        drawBallRadius: Float?
    ) {
        this.xCoordinate = xCoordinate
        this.yCoordinate = yCoordinate
        this.action = action
        this.color = color
        this.screenHeight = screenHeight
        this.height = drawBallHeight
        this.width = drawBallWidth
        this.radius = drawBallRadius
    }


    fun getDrawBallHeight(): Float? {
        return height
    }


    fun setDrawBallHeight(drawBallHeight: Float?) {
        this.height = drawBallHeight
    }


    fun getDrawBallWidth(): Float? {
        return width
    }


    fun setDrawBallWidth(drawBallWidth: Float?) {
        this.width = drawBallWidth
    }


    fun getDrawBallRadius(): Float? {
        return radius
    }


    fun setDrawBallRadius(drawBallRadius: Float?) {
        this.radius = drawBallRadius
    }

    @JvmName("getScreenHeight1")
    fun getScreenHeight(): Int? {
        return screenHeight
    }

    @JvmName("setScreenHeight1")
    fun setScreenHeight(screenHeight: Int?) {
        this.screenHeight = screenHeight
    }

    fun getxCoordinate(): Float? {
        return xCoordinate
    }

    fun setxCoordinate(xCoordinate: Float?) {
        this.xCoordinate = xCoordinate
    }

    fun getyCoordinate(): Float? {
        return yCoordinate
    }

    fun setyCoordinate(yCoordinate: Float?) {
        this.yCoordinate = yCoordinate
    }


    @JvmName("getAction1")
    fun getAction(): Int? {
        return action
    }

    @JvmName("setAction1")
    fun setAction(action: Int?) {
        this.action = action
    }


    @JvmName("getColor1")
    fun getColor(): Int? {
        return color
    }


    @JvmName("setColor1")
    fun setColor(color: Int?) {
        this.color = color
    }
}