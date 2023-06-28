# RetrofitResultAdapter

The adapter is designed to handle network responses and errors.

When dealing with HTTP network traffic, it is common to encounter various use cases. By leveraging
the popular `Retrofit library` along with [Result concept](https://doc.rust-lang.org/std/result/)
from the Rust programming language, we can use the CallAdapter.Factory to ensure consistent error
handling.

## Usage

* Add the JitPack repository to your root build.gradle:

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

* Add the dependency

```groovy
dependencies {
    implementation 'com.github.rayworks:RetrofitResultAdapter:0.1.0'
}
```

* Define your WebService interface

```kotlin
import com.rayworks.resultadapter.Result

interface Service {
    @GET("bar")
    suspend fun getBar(): Result<Bar>
    
    // ...
}
```

* Install the `ResultAdapterFactory`

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(host)
    .client(client)
    .addCallAdapterFactory(ResultAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

* Process the response or error

```kotlin
viewModelScope.launch(Dispatchers.IO) {
    val ans = service.getAnswers()
    when (ans) {
        is Result.NetworkError ->
            Log.e("Err", "NetworkError")
    
        is Result.Failure -> {
            val errMsg : ErrorMsg = gson.fromJson(ans.msg, ErrorMsg::class.java)
            Log.i("test expired : ", errMsg.toString())
        }
    
        is Result.Success ->
            Log.i(">>>", "data recved : ${ans.data}")
    }
}
```

Also check sample code
in [here](./app/src/main/java/com/rayworks/retrofitresultadapter/MainViewModel.kt)

## Credit

[retrofit-adapters](https://github.com/skydoves/retrofit-adapters)

[android-samples](https://www.github.com/icesmith/android-samples)

[NetworkResponseAdapter](https://github.com/haroldadmin/NetworkResponseAdapter)



