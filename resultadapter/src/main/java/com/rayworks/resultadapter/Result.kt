package com.rayworks.resultadapter

import com.rayworks.resultadapter.error.ErrorMessage

sealed class Result<out T> {
    data class Success<T>(val data: T?) : Result<T>()
    data class Failure(val code: Int, val error: ErrorMessage? = null, val msg: String? = "") :
        Result<Nothing>()

    object NetworkError : Result<Nothing>()
}
