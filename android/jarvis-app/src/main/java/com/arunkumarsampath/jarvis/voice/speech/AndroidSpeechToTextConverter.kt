package com.arunkumarsampath.jarvis.voice.speech

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

class AndroidSpeechToTextConverter
@Inject
constructor(private val application: Application) : SpeechToTextConverter, RecognitionListener {
    override var speechEvents: Subject<SpeechToTextConverter.SpeechEvents> = BehaviorSubject.create<SpeechToTextConverter.SpeechEvents>().toSerialized()

    private var isActivated: Boolean = false
    private var speech: SpeechRecognizer? = null

    var recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, application.packageName)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
    }

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(application)) {
            speech = SpeechRecognizer.createSpeechRecognizer(application)
            speech?.setRecognitionListener(this)
        }
    }

    override fun start() {
        Timber.d("Start")
        if (!isActivated) {
            isActivated = true
            speech?.startListening(recognizerIntent)
        }
    }

    override fun stop() {
        Timber.d("Stop")
        isActivated = false
        speech?.stopListening()
    }

    private fun cancelRecognition() {
        speech?.cancel()
    }

    private fun destroyRecognizer() {
        speech?.destroy()
    }

    override fun cleanup() {
        stop()
        cancelRecognition()
        destroyRecognizer()
    }

    override fun onReadyForSpeech(params: Bundle?) {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(error: Int) {
        stop()
        Timber.e(error.toString())
        isActivated = false
        destroyRecognizer()
        initRecognizer()
        speechEvents.onNext(SpeechToTextConverter.SpeechEvents.Error())
    }

    override fun onResults(results: Bundle) {
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
        isActivated = false
        stop()
        speechEvents.onNext(SpeechToTextConverter.SpeechEvents.Detected(matches[0]))
    }
}
