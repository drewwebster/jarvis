package com.arunkumarsampath.jarvis.device

import com.arunkumarsampath.jarvis.device.api.JoinMessagingApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton


@Module
class JoinApiModule {

    @Provides
    @Singleton
    fun createJoinApi(): JoinMessagingApi {
        // Split this later
        val retrofit = Retrofit.Builder().baseUrl(JoinMessagingApi.BASE_URL)
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(JoinMessagingApi::class.java)
    }
}