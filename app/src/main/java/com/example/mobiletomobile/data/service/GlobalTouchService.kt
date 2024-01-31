package com.example.mobiletomobile.data.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.mobiletomobile.ui.customViews.DrawBallView
import com.example.mobiletomobile.utils.beInVisible
import com.example.mobiletomobile.utils.beVisible


class GlobalTouchService : Service() {


    private val TAG = this.javaClass.simpleName


    companion object {
        // window manager
        var mWindowManager: WindowManager? = null
        var isRunning = false
        var mParams: WindowManager.LayoutParams? = null
    }


    var drawBallView: DrawBallView? = null

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()



        isRunning = true

        drawBallView = DrawBallView(this)

        drawBallView!!.currX = 100F
        drawBallView!!.currY = 100F

        mWindowManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        // set layout parameter of window manager
        mParams = WindowManager.LayoutParams()

//        mParams!!.gravity = Gravity.START or Gravity.TOP

        if (Settings.canDrawOverlays(this)){
            mParams?.apply {
                this.width = WindowManager.LayoutParams.MATCH_PARENT
                this.height = WindowManager.LayoutParams.MATCH_PARENT
                try {
                    this.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } catch (e: Exception) {
                    print(e.message)
                }

                //this!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                this.format = PixelFormat.TRANSLUCENT



                mWindowManager?.addView(drawBallView, this)
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Settings.canDrawOverlays(this)){
            if (intent != null) {


                val x = (intent.getFloatExtra("xCoordinate", 0F))
                val y = (intent.getFloatExtra("yCoordinate", 0F))

                // Get the screen dimensions

                val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display: Display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                val screenWidth: Int = size.x
                val screenHeight: Int = size.y

                // Calculate the normalized touch coordinates

                // Calculate the normalized touch coordinates
                val normalizedX = x * screenWidth.toFloat()
                val normalizedY = y * screenHeight.toFloat()

                Log.e(
                    "TouchService",
                    "TouchService: before x = ${(intent.getFloatExtra("xCoordinate", 0F))}"
                )
                Log.e(
                    "TouchService",
                    "TouchService: before y = ${(intent.getFloatExtra("yCoordinate", 0F))}"
                )

                // Normalize the touch coordinates


                Log.e("TouchService", "TouchService: after x = $normalizedX")
                Log.e("TouchService", "TouchService: after y = $normalizedY")


//           Log.d(TAG, "onStartCommand: $senderHeight")
//           Log.d(TAG, "difference: $diff")

                drawBallView?.apply {
                    this.currX = normalizedX
                    this.currY = normalizedY



                    this.ballColor = (intent.getIntExtra("color", Color.BLUE))

                    if ((intent.getIntExtra("action", 0)) == MotionEvent.ACTION_UP) {
                        this.beInVisible()
                    }

                    if ((intent.getIntExtra("action", 0)) == MotionEvent.ACTION_DOWN) {
                        this.beVisible()
                    }
                    Log.d("ball", "onStartCommand: ${intent.getFloatExtra("width", 100F)}")
                    this.width = (intent.getFloatExtra("width", 100F))
                    this.height = (intent.getFloatExtra("height", 100F))
                    this.radius = (intent.getFloatExtra("radius", 35F))

                    this.invalidate()
                }

//        Log.d(TAG, "onStartCommand: "+ (intent.extras?.get("xCoordinate")))
//        Log.d(TAG, "onStartCommand: "+ (intent.extras?.get("yCoordinate")))


                try {
                    mWindowManager!!.updateViewLayout(drawBallView, mParams)
                } catch (e: Exception) {
                    print(e.message)
                }
            }
        }



        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        if (mWindowManager != null) {
            if (drawBallView != null) mWindowManager?.removeView(drawBallView)
        }


        isRunning = false
        super.onDestroy()
    }


}