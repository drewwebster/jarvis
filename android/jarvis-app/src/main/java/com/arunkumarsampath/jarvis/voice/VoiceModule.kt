package com.arunkumarsampath.jarvis.voice

import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.hotword.snowboy.SnowboyHotwordDetector
import dagger.Binds
import dagger.Module

@Module
abstract class VoiceModule {

    @Binds
    abstract fun providesHotwordDetector(snowboyHotwordDetector: SnowboyHotwordDetector): HotwordDetector
}