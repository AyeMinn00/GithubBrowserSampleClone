package com.amo.githubbrowsersamplev2.util

import androidx.lifecycle.LiveData
import com.amo.githubbrowsersamplev2.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        Timber.e("invoke adapt()")
        return object : LiveData<ApiResponse<R>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {

                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            Timber.e("onResponse callback")
                            postValue(ApiResponse.create(response))
                        }

                        override fun onFailure(call: Call<R>, t: Throwable) {
                            Timber.e("onFailure callback")
                            postValue(ApiResponse.create(t))
                        }
                    })
                }
            }
        }
    }


}