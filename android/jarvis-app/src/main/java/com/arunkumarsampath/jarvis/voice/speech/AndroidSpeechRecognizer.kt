package com.arunkumarsampath.jarvis.voice.speech

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import com.arunkumarsampath.jarvis.di.scopes.PerActivity
import com.arunkumarsampath.jarvis.util.Util
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerActivity
class AndroidSpeechRecognizer @Inject constructor(val activity: Activity) {
    private val activityResultSubject = PublishSubject.create<String>()

    val speechDetected: Single<String>
        get() = Observable.just(0)
                .flatMap {
                    activity.startActivityForResult(Util.getRecognizerIntent(activity), REQUEST_CODE)
                    activityResultSubject.timeout(5, TimeUnit.SECONDS)
                }.firstOrError()
                .doOnError { activity.finishActivity(REQUEST_CODE) }
                .onErrorReturn { NO_COMMAND }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE -> if (data != null) {
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                activityResultSubject.onNext(results!![0])
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1002
        const val NO_COMMAND = "no-command"
    }
}