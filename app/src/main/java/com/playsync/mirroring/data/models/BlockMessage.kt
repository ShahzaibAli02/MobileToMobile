package com.playsync.mirroring.data.models


import com.google.gson.annotations.SerializedName

data class BlockMessage(
    @SerializedName("button_color")
    var buttonColor: String? = null,
    @SerializedName("button_title")
    var buttonTitle: String? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("should_block")
    var shouldBlock: Boolean? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("version")
    var version: Int = 0
)