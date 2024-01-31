package com.example.mobiletomobile.data.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mobiletomobile.R
import com.example.mobiletomobile.ui.main.ScreenSharingActivity
import com.twilio.video.Room

class ScreenSharingForegroundService : Service() {

    companion object {
        var isRunning: Boolean = false
        var startServiceCallback: StartForegroundServiceCallback? = null
        var currentCode: String? = null

        @SuppressLint("StaticFieldLeak")
        var currentRoom: Room? = null
        var isAudioSharing: Boolean = false


    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }


    @SuppressLint("InlinedApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val generateCodeIntent = Intent(this, ScreenSharingActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, generateCodeIntent, PendingIntent.FLAG_MUTABLE)
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Screen is sharing")
            .setContentText("Your screen is being shared.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
        startForeground(101, notification)




        startServiceCallback?.onServiceStarted()

        return START_NOT_STICKY
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_NONE
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        currentRoom = null
        currentCode = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

interface StartForegroundServiceCallback {
    fun onServiceStarted()
}