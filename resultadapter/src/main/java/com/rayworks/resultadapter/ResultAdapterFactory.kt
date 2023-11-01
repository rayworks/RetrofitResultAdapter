package com.rayworks.resultadapter

import com.rayworks.resultadapter.error.ErrorMessage
import com.rayworks.resultadapter.error.ErrorMessageConverter
import com.rayworks.resultadapter.internal.ResultAdapter
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/***
 * Creates custom [CallAdapter] based on return type of the declared service interface methods
 */
class ResultAdapterFactory<T : ErrorMessage>(private val converter: ErrorMessageConverter<T>) :
    CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Result::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType, converter)
                }

                else -> null
            }
        }

        else -> null
    }
}