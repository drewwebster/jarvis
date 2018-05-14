package com.arunkumarsampath.jarvis.serveraccess.join.api

import io.reactivex.Completable
import retrofit2.http.GET
import retrofit2.http.Query

interface JoinMessagingApi {

    @GET("/_ah/api/messaging/v1/sendPush")
    fun sendCommand(
            @Query("text") text: String,
            @Query("deviceId") deviceId: String,
            @Query("apikey") apikey: String
    ): Completable

    companion object {
        const val BASE_URL = "https://joinjoaomgcd.appspot.com"
    }
}