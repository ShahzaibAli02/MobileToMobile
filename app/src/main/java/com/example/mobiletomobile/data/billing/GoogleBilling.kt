package com.example.mobiletomobile.data.billing

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.android.billingclient.api.*
import com.example.mobiletomobile.BuildConfig
import com.example.mobiletomobile.utils.SharedPrefs
import com.example.mobiletomobile.utils.saveIsProInSharedPreferences
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class GoogleBilling(private val activity: Activity) : PurchasesUpdatedListener {


    companion object{
        lateinit var billingClient: BillingClient
        var mSubsSkuDetailsList: List<SkuDetails> = emptyList()
        var mInAppSkuDetailsList: List<SkuDetails> = emptyList()

        var subMonthlyPrice : String? = null
        var subYearlyPrice : String? = null
        var inAppAllPrice : String? = null

    }

    var billingDisconnectedCounter = 0
    fun init() {
        billingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The billing client is ready. You can query purchases here.
                    querySkuDetails()
                } else {
                    Log.e("billing", "Failed to set up billing client: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                billingDisconnectedCounter++
                if (billingDisconnectedCounter < 3){
                    billingClient.startConnection(this)
                }
            }
        })
    }

    private fun querySkuDetails() {
        mSubsSkuDetailsList = ArrayList<SkuDetails>()
        mInAppSkuDetailsList = ArrayList<SkuDetails>()
        val subsSkuList = ArrayList<String>()
        val inAppSkuList = ArrayList<String>()

        if (BuildConfig.DEBUG){
            subsSkuList.add("android.test.purchased")
            subsSkuList.add("android.test.purchased")
            inAppSkuList.add("android.test.purchased")
        }else{
            subsSkuList.add("your_yearly_id_here")
            subsSkuList.add("youe_monthly_id_here")
            inAppSkuList.add("your_unlimited_id_here")
        }

        val subsParams = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.SUBS)
            .setSkusList(subsSkuList)
            .build()
        billingClient.querySkuDetailsAsync(subsParams) { billingResult, subsSkuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && subsSkuDetailsList != null) {
                subsSkuDetailsList.forEach{
                    Log.e("billing", "querySkuDetails: ${it.sku}", )
                }
                mSubsSkuDetailsList = subsSkuDetailsList

                subMonthlyPrice = mSubsSkuDetailsList[0].price
                subYearlyPrice = mSubsSkuDetailsList[1].price
                mSubsSkuDetailsList.forEach {
                    Log.e("billing", "querySkuDetails: ${it.sku}")
                }
            } else {
                Log.e("billing", "Failed to query subscription SKU details: ${billingResult.debugMessage}")
            }
        }


        val inAppParams = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(inAppSkuList)
            .build()
        billingClient.querySkuDetailsAsync(inAppParams) { billingResult, inAppSkuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && inAppSkuDetailsList != null) {
                mInAppSkuDetailsList = inAppSkuDetailsList
                inAppAllPrice = mInAppSkuDetailsList[0].price
            } else {
                Log.e("billing", "Failed to query in-app SKU details: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchSubsBillingFlow(activity: Activity, skuIndex: Int) {

        Log.e("billing", "launchSubsBillingFlow: $mSubsSkuDetailsList", )
        if (skuIndex >= 0 && skuIndex < mSubsSkuDetailsList.size) {
            val subsSkuDetails = mSubsSkuDetailsList[skuIndex]
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(subsSkuDetails)
                .build()
            val responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
            if (responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e("billing", "Failed to launch subscription billing flow: $responseCode")
            }
        } else {
            Log.e("billing", "Invalid index: $skuIndex")
        }

    }

    fun launchInAppBillingFlow(activity: Activity) {

        if (mInAppSkuDetailsList.isNotEmpty()) {
            val inAppSkuDetails = mInAppSkuDetailsList[0]
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(inAppSkuDetails)
                .build()
            val responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
            if (responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e("billing", "Failed to launch in-app billing flow: $responseCode")
            }
        }else {
            Log.e("billing", "in App empty, plan not available")
        }

    }
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            if (BuildConfig.DEBUG){
                for (purchase in purchases) {
                    for (skuId in purchase.skus) {
                        when (skuId) {

                            "android.test.purchased" -> handleInAppSkuForAllPurchase(purchase)
                            "android.test.purchased" -> handleSubSkuForMonthlyPurchase(purchase)
                            "android.test.purchased" ->  handleSubSkuForYearlyPurchase(purchase)
                        }
                    }
                }
            }else{
                for (purchase in purchases) {
                    for (skuId in purchase.skus) {
                        when (skuId) {

                            BuildConfig.APPLICATION_ID -> handleInAppSkuForAllPurchase(purchase) //for all ads purchase
                            "monthly_id_here" -> handleSubSkuForMonthlyPurchase(purchase) //for week purchase
                            "yearly_id_here" -> handleSubSkuForYearlyPurchase(purchase) //for month purchase
                        }
                    }
                }
            }

        } else {
            Log.e("billing", "Failed to update purchases: ${billingResult.debugMessage}")
        }
    }

    private fun handleSubSkuForYearlyPurchase(purchase: Purchase) {
        Log.e("billing", "handleSubSku2Purchase: ${purchase.isAcknowledged}")
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                val delayDays :Long = 365L
                val currentTimeMillis = System.currentTimeMillis()
                val delayMillis = TimeUnit.DAYS.toMillis(delayDays)

                val targetTimeMillis = currentTimeMillis + delayMillis
                SharedPrefs(activity).setPurchase(targetTimeMillis)
                billingClient.endConnection()
                val intent = activity.packageManager.getLaunchIntentForPackage(
                    activity.packageName
                )
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } else {
            /* Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()*/
        }
    }

    private fun handleSubSkuForMonthlyPurchase(purchase: Purchase) {

        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                val delayDays :Long = 30L
                val currentTimeMillis = System.currentTimeMillis()
                val delayMillis = TimeUnit.DAYS.toMillis(delayDays)

                val targetTimeMillis = currentTimeMillis + delayMillis
                SharedPrefs(activity).setPurchase(targetTimeMillis)
                billingClient.endConnection()
                val intent = activity.packageManager.getLaunchIntentForPackage(
                    activity.packageName
                )
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } else {
            /* Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()*/
        }
        Log.e("billing", "handleSubSku1Purchase: ${purchase.isAcknowledged}")

    }



    private fun handleInAppSkuForAllPurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                saveIsProInSharedPreferences(isPro = true, activity)
                billingClient.endConnection()
                val intent = activity.packageManager.getLaunchIntentForPackage(
                    activity.packageName
                )
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } else {
            /* Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()*/
        }
    }


}