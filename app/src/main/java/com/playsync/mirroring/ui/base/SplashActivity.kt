package com.playsync.mirroring.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.playsync.mirroring.BuildConfig
import com.playsync.mirroring.R
import com.playsync.mirroring.data.admob.AdmobInter
import com.playsync.mirroring.data.billing.GoogleBilling
import com.playsync.mirroring.databinding.ActivitySplashBinding
import com.playsync.mirroring.ui.main.DashboardActivity
import com.playsync.mirroring.utils.RemoteDataConfig
import com.playsync.mirroring.utils.SharedPrefs
import com.playsync.mirroring.utils.gone
import com.playsync.mirroring.utils.visible
import com.playsync.mirroring.utils.checkIfUserIsPro
import com.playsync.mirroring.utils.isNetworkAvailable
import com.playsync.mirroring.utils.saveIsProInSharedPreferences
import com.playsync.mirroring.utils.singleClick
import com.playsync.mirroring.utils.verifyInstallerId
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.playsync.mirroring.data.models.BlockMessage
import com.playsync.mirroring.utils.toObject
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
        binding.progressBar.visible()
        binding.lottieStart.gone()


        remoteConfig?.getSplashRemoteConfig {
            Log.e("RemoteConfig ",RemoteDataConfig.getAdmobInterId())
            val shouldBlock=RemoteDataConfig.remoteAdSettings.block_details.value.toObject(BlockMessage::class.java)
            shouldBlock?.let {
                if(BuildConfig.VERSION_CODE <= it.version && it.shouldBlock == true)
                {
                    showBlockMessage(it)
                    return@getSplashRemoteConfig
                }


            }


            binding.progressBar.gone()
            binding.lottieStart.visible()
            it?.let {
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

    }

    private fun showBlockMessage(it: BlockMessage)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle(it.title)
            .setMessage(it.message)
            .setPositiveButton(it.buttonTitle) { _, _ ->
                kotlin.runCatching {
                    Intent(Intent.ACTION_VIEW, Uri.parse(it.url)).apply {
                            startActivity(this)
                        }
                }
            }.setCancelable(false)
            .show()
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