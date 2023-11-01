package com.rayworks.resultadapter.internal

import com.rayworks.resultadapter.Result
import com.rayworks.resultadapter.ResultCall
import com.rayworks.resultadapter.error.ErrorMessage
import com.rayworks.resultadapter.error.ErrorMessageConverter
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultAdapter<E : ErrorMessage>(
    private val type: Type,
    private val converter: ErrorMessageConverter<E>
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call, converter)
}