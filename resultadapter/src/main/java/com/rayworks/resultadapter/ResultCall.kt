package com.rayworks.resultadapter

import com.rayworks.resultadapter.internal.CallDelegate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/***
 * A customized [Call] with its result wrapped by [Result]
 */
class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {
    override fun executeImpl(): Response<Result<T>> {

        try {
            val response = proxy.execute()
            return Response.success(extractNetworkData(response))

        } catch (e: Exception) {
            e.printStackTrace()
            return Response.success(Result.NetworkError)
        }
    }

    override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = extractNetworkData(response)

            callback.onResponse(this@ResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            t.printStackTrace()

            val result = if (t is IOException) {
                Result.NetworkError
            } else {
                Result.Failure(-1, "unknown error")
            }

            callback.onResponse(this@ResultCall, Response.success(result))
        }
    })

    /***
     * Extracts data from the HTTP response
     *
     * @return The actual data object or error info
     */
    private fun extractNetworkData(response: Response<T>): Result<T> {
        val code = response.code()
        val result = when (code) {
            in 200 until 300 -> {
                val body = response.body()
                val successResult: Result<T> = Result.Success(body)
                successResult
            }

            in 400 until 500, in 500 until 600 -> {
                // client / server errors
                var err = ""
                response.errorBody()?.let {
                    it.charStream().use { reader ->
                        err = reader.readText()
                    }
                }
                Result.Failure(code, err)
            }

            else -> {
                Result.Failure(code)
            }
        }
        return result
    }

    override fun cloneImpl() = ResultCall(proxy.clone())
}