package com.example.mobiletomobile.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.mobiletomobile.BuildConfig

class SharedPrefs(activity: Activity) {

    private var sf: SharedPreferences = activity.getSharedPreferences(
        "${BuildConfig.APPLICATION_ID}SharedPreferences",
        Context.MODE_PRIVATE
    )

    fun setDrawBallWidth(value: String) {
        sf.edit().putString("drawBallWidth", value).apply()
    }

    fun getDrawBallWidth(): String {
        return sf.getString("drawBallWidth", "100")!!
    }

    fun setDrawBallSizeProgress(value: String) {
        sf.edit().putString("drawBallSizeProgress", value).apply()
    }

    fun getDrawBallSizeProgress(): String {
        return sf.getString("drawBallSizeProgress", "2")!!
    }

    fun setDrawBallRadius(value: String) {
        sf.edit().putString("drawBallRadius", value).apply()
    }
    fun getDrawBallRadius(): String {
        return sf.getString("drawBallRadius", "")!!
    }

    fun setDrawBallHeight(value: String) {
        sf.edit().putString("drawBallHeight", value).apply()
    }
    fun getDrawBallHeight(): String {
        return sf.getString("drawBallHeight", "")!!
    }


    fun setPurchase(value: Long) {
        sf.edit().putLong("Purchase", value).apply()
    }

    fun isPurchase(): Long {
        return sf.getLong("Purchase",0L)
    }


    fun setDrawBallViewColor(value: Int) {
        sf.edit().putInt("DrawBallColor", value).apply()
    }


    fun getDrawBallViewColor(): Int {
        return sf.getInt("DrawBallColor", 5563)

    }









}



