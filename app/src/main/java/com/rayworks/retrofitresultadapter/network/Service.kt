package com.rayworks.retrofitresultadapter.network

import retrofit2.Call
import retrofit2.http.GET
import com.rayworks.resultadapter.Result

interface Service {
    @GET("bar")
    suspend fun getBar(): Result<Bar>

    @GET("bars")
    suspend fun getBars(): Result<List<Bar>>

    @GET("test")
    suspend fun getFoobar(): Result<String>

    @GET("expired")
    suspend fun getIllegal(): Result<String>

    @GET("other")
    suspend fun getOtherInfo() : Result<Int>

    @GET("main")
    fun getData(): Call<Result<Bar>>

    @GET("timeout")
    fun getTime(): Call<Result<Long>>

}