package com.arunkumarsampath.jarvis.voice

import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.hotword.SnowboyHotwordDetector
import dagger.Module
import dagger.Provides

@Module
class VoiceModule {

    @Provides
    fun providesHotwordDetector(snowboyHotwordDetector: SnowboyHotwordDetector): HotwordDetector = snowboyHotwordDetector
}