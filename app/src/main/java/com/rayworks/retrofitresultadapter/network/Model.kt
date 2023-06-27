package com.rayworks.retrofitresultadapter.network

import com.google.gson.annotations.SerializedName

data class Bar(
    @SerializedName("foo")
    val foo: String
)

data class ErrorMsg(
    @SerializedName(value = "code", alternate = ["bizCode"])
    val bizCode: Int, val msg: String? = null
)