package com.arunkumarsampath.jarvis.serveraccess.join

import com.arunkumarsampath.jarvis.serveraccess.join.api.JoinMessagingApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton


@Module
class JoinApiModule {

    @Provides
    @Singleton
    fun createJoinApi(okHttpClient: OkHttpClient): JoinMessagingApi {
        // Split this later
        val retrofit = Retrofit.Builder().baseUrl(JoinMessagingApi.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(JoinMessagingApi::class.java)
    }
}