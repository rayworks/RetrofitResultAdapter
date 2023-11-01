package com.rayworks.resultadapter.network

import com.google.gson.annotations.SerializedName
import com.rayworks.resultadapter.error.ErrorMessage

data class Bar(
    @SerializedName("foo")
    val foo: String
)

class ErrorMsg() : ErrorMessage() {
    @SerializedName(value = "code", alternate = ["bizCode"])
    var bizCode: Int = 0
    var msg: String? = null

    constructor(bizCode: Int, msg: String?) : this() {
        this.bizCode = bizCode
        this.msg = msg
    }

    override fun toString(): String {
        return "ErrorMsg(bizCode=$bizCode, msg=$msg)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErrorMsg

        if (bizCode != other.bizCode) return false
        if (msg != other.msg) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bizCode
        result = 31 * result + (msg?.hashCode() ?: 0)
        return result
    }

}