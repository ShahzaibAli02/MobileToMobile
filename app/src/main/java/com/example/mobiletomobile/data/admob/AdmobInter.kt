package com.example.mobiletomobile.data.admob

import android.app.Activity
import android.util.Log
import com.example.mobiletomobile.utils.RemoteDataConfig
import com.example.mobiletomobile.utils.isNetworkAvailable
import com.google.android.gms.ads.AdError

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdmobInter {
    companion object {
        @JvmStatic
        var isAdLoaded = false
        var admobInterstitialAd: InterstitialAd? = null
        fun loadInterstitialAd(
            activity: Activity,
            adId: String,
            loadListener: () -> Unit,
            failedListener: () -> Unit
        ) {

            if (adId.isNotEmpty() && activity.isNetworkAvailable() && !isAdLoaded) {

                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                    activity,
                    adId,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(ad: LoadAdError) {

                            Log.e("AdmobInter", "onAdFailedToLoad: ")
                            admobInterstitialAd = null
                            isAdLoaded = false
                            failedListener.invoke()

                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.e("AdmobInter", "onAdLoaded: ")
                            admobInterstitialAd = ad
                            isAdLoaded = true
                            loadListener.invoke()

                        }

                    })
            } else {
                failedListener.invoke()
            }
        }


        fun showAdmobInter(
            activity: Activity,
            impressionListener: () -> Unit,
            dismissedListener: () -> Unit,
            failedListener: () -> Unit,
        ) {

            admobInterstitialAd?.let {
                it.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {

                        this@Companion.admobInterstitialAd = null
                        isAdLoaded = false
                        dismissedListener.invoke()
                        loadInterstitialAd(activity,RemoteDataConfig.getAdmobInterId(),{},{})
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        this@Companion.admobInterstitialAd = null
                        isAdLoaded = false
                        failedListener.invoke()
                        super.onAdFailedToShowFullScreenContent(p0)

                    }

                    override fun onAdImpression() {


                        impressionListener.invoke()


                        super.onAdImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        this@Companion.admobInterstitialAd = null
                        isAdLoaded = false


                        super.onAdShowedFullScreenContent()

                    }
                }

                it.show(activity)
            } ?: run {
                failedListener.invoke()
            }
        }

    }
}