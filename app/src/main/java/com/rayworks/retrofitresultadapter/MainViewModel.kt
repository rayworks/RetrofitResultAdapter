package com.rayworks.retrofitresultadapter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rayworks.resultadapter.Result
import com.rayworks.resultadapter.ResultAdapterFactory
import com.rayworks.retrofitresultadapter.network.Bar
import com.rayworks.retrofitresultadapter.network.ErrorMsg
import com.rayworks.retrofitresultadapter.network.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel : ViewModel() {
    private val service: Service
    private val gson = Gson()

    init {
        val mockInterceptor = MockInterceptor()
        val mockClient = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://mock.com/")
            .client(mockClient)
            .addCallAdapterFactory(ResultAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(Service::class.java)
    }


    fun executeRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val bar = service.getBar()
            Log.i("test bar", bar.toString())

            val bars = service.getBars()
            Log.i("test bars", bars.toString())

            val res = service.getFoobar()
            Log.i("test foobar", res.toString())

            val ans = service.getIllegal()
            when (ans) {
                is Result.NetworkError ->
                    Log.e("Err", "NetworkError")

                is Result.Failure -> {
                    if (!ans.msg.isNullOrEmpty()) {
                        val errMsg: ErrorMsg = gson.fromJson(ans.msg, ErrorMsg::class.java)
                        Log.i("test expired : ", errMsg.toString())
                    } else {
                        Log.i("test expired : ", ans.toString())
                    }
                }

                is Result.Success ->
                    Log.i(">>>", "data recved : ${ans.data}")
            }


            val other = service.getOtherInfo()
            Log.i("test other", other.toString())
        }

        val data = service.getData()
        val resp = data.execute()

        if (resp.body() is Result.Success<*>) {
            Log.i("test getData---OK: ", resp.body().toString())
        } else {
            Log.e("test getData---Err: ", resp.body().toString())
        }

        val tm = service.getTime().execute()
        if (tm.body() is Result.Success<*>) {
            Log.i("test getTime---OK: ", tm.body().toString())
        } else {
            Log.e("test getTime---Err: ", tm.body().toString())
        }

        service.getData().enqueue(object : Callback<Result<Bar>> {
            override fun onResponse(call: Call<Result<Bar>>, response: Response<Result<Bar>>) {
                if (response.body() is Result.Success<*>) {
                    val barSuccess = response.body() as Result.Success<Bar>
                    Log.i("test getData enqueue ---OK: ", barSuccess.data.toString())
                } else {
                    Log.e("test getData enqueue ---Err: ", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Result<Bar>>, t: Throwable) {
                Log.e("test getData enqueue ---Err", t.message.toString())
            }

        })
    }
}

/**
 * A Mock interceptor that returns a test data
 */
class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var statusCode = 200

        val response = when (chain.request().url.encodedPath) {
            "/bar" -> """{"foo":"baz"}"""
            "/bars" -> """[{"foo":"baz1"},{"foo":"baz2"}]"""
            "/test" -> throw UnknownHostException("Unknown")
            "/expired" -> {
                statusCode = 403
                """{"code":403, "msg":"account token expired"}"""
            }

            "/main" -> """{"foo":"foobar007"}"""

            else -> throw SocketTimeoutException("request timeout")
        }

        val mediaType = "application/json".toMediaTypeOrNull()
        val responseBody = response.toResponseBody(mediaType)

        return okhttp3.Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .request(chain.request())
            .code(statusCode)
            .message("")
            .body(responseBody)
            .build()
    }
}