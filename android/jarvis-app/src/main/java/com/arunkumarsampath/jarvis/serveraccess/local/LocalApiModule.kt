package com.arunkumarsampath.jarvis.serveraccess.local

import com.arunkumarsampath.jarvis.serveraccess.local.api.LocalHostApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton


@Module
class LocalApiModule {

    @Provides
    @Singleton
    fun createLocalhostApi(okHttpClient: OkHttpClient): LocalHostApi {
        val retrofit = Retrofit.Builder().baseUrl(LocalHostApi.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(LocalHostApi::class.java)
    }
}