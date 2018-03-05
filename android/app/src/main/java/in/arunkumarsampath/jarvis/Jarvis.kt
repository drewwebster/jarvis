package `in`.arunkumarsampath.jarvis

import android.app.Application
import timber.log.Timber

/**
 * Created by arunk on 06-03-2018.
 */
class Jarvis : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}