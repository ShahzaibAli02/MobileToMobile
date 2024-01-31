package com.playsync.mirroring.utils

import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.playsync.mirroring.BuildConfig
import org.json.JSONObject


class RemoteDataConfig {

    private var remoteConfig: FirebaseRemoteConfig? = null
    private val timeInMillis: Long = if (BuildConfig.DEBUG) 0L else 3600L
    private val remoteTopic = "mobileMirroring"

    companion object {
        var remoteAdSettings = RemoteAdSettings()
        fun getAdmobBannerId() = remoteAdSettings.admob_banner_id.value
        fun getAdmobInterId() = remoteAdSettings.admob_inter_id.value

        fun getTwillioURl() = remoteAdSettings.twillio_api.value

    }


    private fun getInstance(): FirebaseRemoteConfig? {
        remoteConfig?.let {
            return it
        }
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSetting = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(timeInMillis)
            .build()
        remoteConfig?.setConfigSettingsAsync(configSetting)
        remoteConfig?.setDefaultsAsync(
            mapOf(remoteTopic to Gson().toJson(RemoteAdSettings()))
        )
        return remoteConfig
    }


    private fun getRemoteConfig(): RemoteAdSettings {
        return Gson().fromJson(
            getInstance()?.getString(remoteTopic),
            RemoteAdSettings::class.java
        )
    }

    fun getSplashRemoteConfig(listener: (RemoteAdSettings?) -> Unit) {
        getInstance()?.fetchAndActivate()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val value = getRemoteConfig()
                    remoteAdSettings = value
                    listener.invoke(remoteAdSettings)
                }
                else{
                    Log.e("TAG", "ERROR: ")
                    listener.invoke(null)
                }


            }
    }
}

@Keep
data class RemoteAdSettings(


    @SerializedName("admob_banner_id")
    val admob_banner_id: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_inter_id")
    val admob_inter_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("twillio_api")
    val twillio_api: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("premium")
    val premium: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("dash_banner")
    val dash_banner: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("share_dash_inter")
    val share_dash_inter: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("remote_dash_inter")
    val remote_dash_inter: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("block_details")
    val block_details: RemoteAdDetailsJsonObject = RemoteAdDetailsJsonObject()

    ) {
    override fun toString(): String {
        return "$admob_banner_id, $premium"
    }
}

@Keep
data class RemoteAdDetails(
    @SerializedName("value")
    val value: String = "off",
)

@Keep
data class RemoteAdDetailsJsonObject(
    @SerializedName("value")
    val value: JsonObject = JsonObject(),
)