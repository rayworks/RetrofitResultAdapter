package com.rayworks.resultadapter.internal

import com.rayworks.resultadapter.Result
import com.rayworks.resultadapter.ResultCall
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultAdapter(
    private val type: Type
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
}