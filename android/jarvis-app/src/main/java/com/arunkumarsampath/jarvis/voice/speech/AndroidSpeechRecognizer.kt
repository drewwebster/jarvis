package com.arunkumarsampath.jarvis.voice.speech

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent.*
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.support.v7.app.AppCompatActivity
import com.arunkumarsampath.jarvis.di.scopes.PerActivity
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerActivity
class AndroidSpeechRecognizer
@Inject constructor(
        val activity: AppCompatActivity,
        val sp: SchedulerProvider
) {

    private val recognizerIntent = Intent(ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
        putExtra(EXTRA_CALLING_PACKAGE, activity.packageName)
        putExtra(EXTRA_MAX_RESULTS, 3)
        putExtra(EXTRA_PREFER_OFFLINE, true)
    }

    val speechDetections: Single<String>
        get() {
            return Single.create<String> { emitter ->
                createSpeechRecognizer(activity).apply {
                    setRecognitionListener(object : RecongnitionListenerAdapter() {
                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(RESULTS_RECOGNITION)
                            if (matches != null && matches.isNotEmpty()) {
                                emitter.onSuccess(matches.first())
                            } else {
                                emitter.onError(IllegalStateException("No result received"))
                            }
                        }

                        override fun onError(error: Int) {
                            emitter.onError(IllegalStateException("Recognition error"))
                        }
                    })

                    startListening(recognizerIntent)

                    // Cancellation stuff
                    emitter.setCancellable {
                        destroy()
                    }
                }
            }.doOnError(Timber::e)
                    .subscribeOn(sp.ui())
                    .timeout(6, TimeUnit.SECONDS)
                    .onErrorReturn { NO_COMMAND }
        }

    internal open class RecongnitionListenerAdapter : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onResults(results: Bundle?) {}
    }

    companion object {
        const val NO_COMMAND = "no-command"
    }
}