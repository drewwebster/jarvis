package com.arunkumarsampath.jarvis.serveraccess.local.api

import io.reactivex.Completable
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalHostApi {

    @GET("/jarvis/command/{command}")
    fun sendCommand(
            @Path("command") command: String
    ): Completable

    companion object {
        const val BASE_URL = "http://localhost:1880"
    }
}