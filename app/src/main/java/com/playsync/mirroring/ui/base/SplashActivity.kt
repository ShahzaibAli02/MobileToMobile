package com.playsync.mirroring.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.playsync.mirroring.R
import com.playsync.mirroring.databinding.ActivitySplashBinding
import com.playsync.mirroring.ui.main.DashboardActivity
import com.playsync.mirroring.utils.SharedPrefs
import com.playsync.mirroring.utils.singleClick
import kotlin.system.exitProcess


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MobileToMobile)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

//        if (System.currentTimeMillis() < SharedPrefs(this).isPurchase()) {
//            saveIsProInSharedPreferences(isPro = true, this)
//        }




        binding.lottieStart.setOnClickListener {
            singleClick {
                startButtonFlow()
            }
        }

    }

    override fun onResume() {
        super.onResume()




    }








    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        finishAndRemoveTask()
        exitProcess(0)
    }


    private fun startButtonFlow() {
//        if (RemoteDataConfig.remoteAdSettings.premium.value == "on"
//            && !checkIfUserIsPro()
//        ) {
//            startActivity(Intent(this@SplashActivity, PremiumActivity::class.java).apply {
//                putExtra("isFromSplash", true)
//            })
//            finish()
//        } else {
//            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java).apply {
//                putExtra("isFromSplash", true)
//            })
//            finish()
//        }


        startActivity(Intent(this@SplashActivity, DashboardActivity::class.java).apply {
            putExtra("isFromSplash", true)
        })
        finish()
    }
}