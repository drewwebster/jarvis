package com.arunkumarsampath.jarvis.util.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    fun database() = FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }!!

    @Provides
    internal fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
