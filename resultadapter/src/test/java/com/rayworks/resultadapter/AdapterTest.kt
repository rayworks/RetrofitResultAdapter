package com.rayworks.resultadapter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rayworks.resultadapter.error.ErrorMessageConverter
import com.rayworks.resultadapter.network.Bar
import com.rayworks.resultadapter.network.ErrorMsg
import com.rayworks.resultadapter.network.Service
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(JUnit4::class)
class AdapterTest {
    private val gson = Gson()
    private lateinit var service: Service
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    inline fun <reified T> Gson.jsonToObject(str: String): T {
        return fromJson(str, object : TypeToken<T>() {}.type)
    }

    @Before
    fun setUp() {
        val mockInterceptor = MockInterceptor()
        val mockClient = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

        val converter = object : ErrorMessageConverter<ErrorMsg> {
            override fun convert(str: String): ErrorMsg {
                return gson.jsonToObject(str)
            }
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mock.com/")
            .client(mockClient)
            .addCallAdapterFactory(ResultAdapterFactory(converter))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(Service::class.java)
    }

    @Test
    fun testRespResult() = runTest(testDispatcher) {
        launch {
            val bar = service.getBar()
            if (bar is Result.Success) {
                Assert.assertEquals(Bar("baz"), bar.data)
            }

            val bars = service.getBars()
            Assert.assertTrue(bars is Result.Success)
            if (bars is Result.Success) {
                Assert.assertEquals(listOf(Bar("baz1"), Bar("baz2")), bars.data)
            }

            val res = service.getFoobar()
            Assert.assertTrue(res is Result.NetworkError)

            val ans = service.getIllegal()
            Assert.assertTrue(ans is Result.Failure)
            if (ans is Result.Failure) {
                Assert.assertEquals(ErrorMsg(403, "account token expired"), ans.error)
            }
        }

    }

    @Test
    fun testCalls() {
        val data = service.getData()
        val resp = data.execute()

        if (resp.body() is Result.Success<*>) {
            Assert.assertEquals(Bar("foobar007"), (resp.body() as? Result.Success)?.data)
        } else {
            Assert.fail("Object bar not equal")
        }

        val tm = service.getTime().execute()
        Assert.assertTrue(tm.body() is Result.NetworkError)

        service.getData().enqueue(object : Callback<Result<Bar>> {
            override fun onResponse(call: Call<Result<Bar>>, response: Response<Result<Bar>>) {
                if (response.body() is Result.Success<*>) {
                    val barSuccess = response.body() as Result.Success<Bar>
                    Assert.assertEquals(Bar("foobar007"), barSuccess.data)
                } else {
                    Assert.fail("Object bar not equal in async call")
                }
            }

            override fun onFailure(call: Call<Result<Bar>>, t: Throwable) {
                Assert.fail("Fetching Object bar shouldn't fail")
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