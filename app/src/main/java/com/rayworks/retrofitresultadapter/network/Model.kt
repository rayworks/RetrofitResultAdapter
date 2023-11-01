package com.rayworks.retrofitresultadapter.network

import com.google.gson.annotations.SerializedName
import com.rayworks.resultadapter.error.ErrorMessage

data class Bar(
    @SerializedName("foo")
    val foo: String
)

class ErrorMsg : ErrorMessage() {
    @SerializedName(value = "code", alternate = ["bizCode"])
    var bizCode: Int = 0
    var msg: String? = null
    override fun toString(): String {
        return "ErrorMsg(bizCode=$bizCode, msg=$msg)"
    }
}