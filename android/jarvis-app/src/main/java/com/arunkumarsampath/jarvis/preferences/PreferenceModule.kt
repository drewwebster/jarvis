package com.arunkumarsampath.jarvis.preferences

import android.app.Application
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class PreferenceModule {

    @Provides
    fun providesSharedPreferences(application: Application) = PreferenceManager.getDefaultSharedPreferences(application)!!
}