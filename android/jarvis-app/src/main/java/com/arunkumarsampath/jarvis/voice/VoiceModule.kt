package com.arunkumarsampath.jarvis.voice

import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.hotword.SnowboyHotwordDetector
import com.arunkumarsampath.jarvis.voice.speech.AndroidSpeechToTextConverter
import com.arunkumarsampath.jarvis.voice.speech.SpeechToTextConverter
import com.arunkumarsampath.jarvis.voice.voicecommand.DefaultVoiceCommand
import com.arunkumarsampath.jarvis.voice.voicecommand.VoiceCommand
import dagger.Module
import dagger.Provides

@Module
class VoiceModule {

    @Provides
    fun providesHotwordDetector(snowboyHotwordDetector: SnowboyHotwordDetector): HotwordDetector = snowboyHotwordDetector

    @Provides
    fun speechToTextConverter(speechToTextConverter: AndroidSpeechToTextConverter): SpeechToTextConverter = speechToTextConverter

    @Provides
    fun voiceCommand(
            speechToTextConverter: SpeechToTextConverter,
            hotwordDetector: HotwordDetector,
            schedulerProvider: SchedulerProvider
    ): VoiceCommand {
        return DefaultVoiceCommand(hotwordDetector, speechToTextConverter, schedulerProvider)
    }
}