package com.example.mobiletomobile.data.admob

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import com.example.mobiletomobile.utils.RemoteDataConfig
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beVisible
import com.example.mobiletomobile.utils.checkIfUserIsPro
import com.example.mobiletomobile.utils.isNetworkAvailable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


class AdmobBanner {

    companion object {

        var isAdLoaded = false
        var adView: AdView? = null

        private var instance: AdmobBanner? = null
        fun getInstance() = instance ?: synchronized(this)
        { instance ?: AdmobBanner().also { instance = it } }
    }

    fun loadAd(
        activity: Activity,
        adUnitId: String,
        loadListener: () -> Unit,
        failListener: () -> Unit
    ) {
        if (adUnitId.isNotEmpty() && activity.isNetworkAvailable() && !isAdLoaded) {

            adView = AdView(activity)
            adView?.adUnitId = adUnitId
            adView?.setAdSize(AdSize.BANNER)

            val adRequest  = AdRequest.Builder().build()


            adView?.loadAd(adRequest)
            adView?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    Log.e("AdmobBanner", "Ad Clicked")
                    loadAd(activity, adUnitId, loadListener, failListener)
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.

                    Log.e("AdmobBanner", "Ad Closed")

                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.

                    Log.e("AdmobBanner", "Ad Error: ${adError.message}")


                    failListener.invoke()
                }

                override fun onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.

                    Log.e("AdmobBanner", "Ad Impression")


                }

                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.e("AdmobBanner", "Ad loaded")

                    isAdLoaded = true
                    loadListener.invoke()

                }


            }
        }

    }


    fun showBannerAd(activity: Activity,frameLayout: FrameLayout) {
        if (isAdLoaded
            && RemoteDataConfig.remoteAdSettings.dash_banner.value == "on"
            && !activity.checkIfUserIsPro()) {
            frameLayout.removeAllViews()
            val params = LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            if (adView?.parent != null) (adView?.parent as? ViewGroup)?.removeView(
                adView
            )
            frameLayout.beVisible()
            frameLayout.addView(adView, params)
            Log.e("AdmobBanner", "showing banner ad")
        }else{
            frameLayout.beGone()
            Log.e("AdmobBanner", "failed to show banner ad")
        }
    }
}