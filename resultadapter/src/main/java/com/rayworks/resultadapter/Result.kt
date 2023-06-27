package com.rayworks.resultadapter

sealed class Result<out T> {
    data class Success<T>(val data: T?) : Result<T>()
    data class Failure(val code: Int, val msg: String? = "") : Result<Nothing>() // generic msg
    object NetworkError : Result<Nothing>() // or parsing failure
}
