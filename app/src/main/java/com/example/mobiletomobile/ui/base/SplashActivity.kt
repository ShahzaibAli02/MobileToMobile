package com.example.mobiletomobile.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mobiletomobile.BuildConfig
import com.example.mobiletomobile.R
import com.example.mobiletomobile.data.admob.AdmobInter
import com.example.mobiletomobile.data.billing.GoogleBilling
import com.example.mobiletomobile.databinding.ActivitySplashBinding
import com.example.mobiletomobile.ui.main.DashboardActivity
import com.example.mobiletomobile.ui.main.PremiumActivity
import com.example.mobiletomobile.utils.RemoteDataConfig
import com.example.mobiletomobile.utils.SharedPrefs
import com.example.mobiletomobile.utils.afterDelay
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beVisible
import com.example.mobiletomobile.utils.checkIfUserIsPro
import com.example.mobiletomobile.utils.isNetworkAvailable
import com.example.mobiletomobile.utils.saveIsProInSharedPreferences
import com.example.mobiletomobile.utils.singleClick
import com.example.mobiletomobile.utils.verifyInstallerId
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.system.exitProcess


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var remoteConfig: RemoteDataConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MobileToMobile)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this)
        remoteConfig = RemoteDataConfig()

        if (System.currentTimeMillis() < SharedPrefs(this).isPurchase()) {
            saveIsProInSharedPreferences(isPro = true, this)
        }
        GoogleBilling(this@SplashActivity).init()




        binding.lottieStart.setOnClickListener {
            singleClick {
                startButtonFlow()
            }
        }

        setUpRemote()


    }

    override fun onResume() {
        super.onResume()




    }


    private fun setUpRemote() {

        remoteConfig?.getSplashRemoteConfig {
            Log.e("RemoteConfig", it.toString())

            if (!checkIfUserIsPro()
                && isNetworkAvailable()
                && verifyInstallerId()
            ) {
                AdmobInter.loadInterstitialAd(
                    this@SplashActivity,
                    RemoteDataConfig.getAdmobInterId(),
                    {
                        //load listener
                    },
                    {
                        //failed listener
                    })
            }
        }

    }



    private fun startFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        finishAndRemoveTask()
        exitProcess(0)
    }


    private fun startButtonFlow() {
        if (RemoteDataConfig.remoteAdSettings.premium.value == "on"
            && !checkIfUserIsPro()
        ) {
            startActivity(Intent(this@SplashActivity, PremiumActivity::class.java).apply {
                putExtra("isFromSplash", true)
            })
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java).apply {
                putExtra("isFromSplash", true)
            })
            finish()
        }
    }
}