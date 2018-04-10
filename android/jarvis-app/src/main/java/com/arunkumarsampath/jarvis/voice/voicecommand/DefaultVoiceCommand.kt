package com.arunkumarsampath.jarvis.voice.voicecommand

import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.speech.SpeechToTextConverter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class DefaultVoiceCommand
@Inject
constructor(
        private val hotwordDetector: HotwordDetector,
        private val speechToTextConverter: SpeechToTextConverter,
        private val schedulerProvider: SchedulerProvider
) : VoiceCommand {

    override var voiceCommandsEvent: Subject<VoiceCommandEvent> = BehaviorSubject.create<VoiceCommandEvent>().toSerialized()

    private var isActivated: Boolean = false

    private val subs = CompositeDisposable()

    init {
        runVoiceCommandProcessor()
    }

    private fun runVoiceCommandProcessor() {
        subs.add(hotwordDetector.hotwordEvents
                .observeOn(schedulerProvider.ui())
                .subscribe { hotword ->
                    when (hotword) {
                        is HotwordDetector.HotwordEvent.Detected -> {
                            isActivated = true
                            doSpeechRecognition()
                        }
                    }
                })

        subs.add(speechToTextConverter.speechEvents
                .observeOn(schedulerProvider.ui())
                .subscribe { hotword ->
                    when (hotword) {
                        is SpeechToTextConverter.SpeechEvents.Detected -> {
                            voiceCommandsEvent.onNext(VoiceCommandEvent.Command(hotword.string))
                        }
                    }
                    isActivated = false
                    doHotwordRecognition()
                })
    }

    private fun doHotwordRecognition() {
        if (!isActivated) {
            speechToTextConverter.stop()
            hotwordDetector.start()
        }
    }

    private fun doSpeechRecognition() {
        if (isActivated) {
            hotwordDetector.stop()
            speechToTextConverter.start()
        }
    }

    override fun start() {
        if (isActivated) {
            speechToTextConverter.start()
        } else {
            hotwordDetector.start()
        }
    }

    override fun stop() {
        if (isActivated) {
            speechToTextConverter.stop()
        } else {
            hotwordDetector.stop()
        }
    }

    override fun cleanup() {
        subs.clear()
        speechToTextConverter.cleanup()
        hotwordDetector.cleanup()
    }
}