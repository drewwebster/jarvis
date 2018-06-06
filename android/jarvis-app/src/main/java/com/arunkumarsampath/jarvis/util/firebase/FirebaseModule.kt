package com.arunkumarsampath.jarvis.util.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import javax.inject.Named

@Module
class FirebaseModule {

    @Provides
    fun database() = FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }!!

    @Provides
    internal fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Named("Authorization")
    internal fun authorization(firebaseAuth: FirebaseAuth): Single<String> {
        return Single.create<String> { emitter ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                emitter.onError(RuntimeException("User not logged id"))
            } else {
                currentUser.getIdToken(true)
                        .addOnSuccessListener { getTokenResult -> emitter.onSuccess(getTokenResult.token!!) }
                        .addOnFailureListener(emitter::onError)
            }
        }.map { "Bearer $it" }
    }
}
