/*
 * Copyright 2018 Arunkumar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.arunkumarsampath.jarvis.di.app

import android.app.Application
import com.arunkumarsampath.jarvis.di.viewmodel.ViewModelModule
import com.arunkumarsampath.jarvis.preferences.PreferenceModule
import com.arunkumarsampath.jarvis.util.firebase.FirebaseModule
import com.arunkumarsampath.jarvis.util.network.NetworkModule
import com.arunkumarsampath.jarvis.util.scheduler.AppSchedulerProvider
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [
    ViewModelModule::class,
    PreferenceModule::class,
    NetworkModule::class,
    FirebaseModule::class
])
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    internal fun povidesSchedulers(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}