package com.playsync.mirroring.ui.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


    }




}