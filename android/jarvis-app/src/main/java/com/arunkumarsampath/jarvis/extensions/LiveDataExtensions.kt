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

package com.arunkumarsampath.jarvis.extensions

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import arun.com.chromer.util.ActivityLifeCycleCallbackAdapter

inline fun <T> LiveData<T>.watch(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}

inline fun <T> LiveData<T>.watchNonNull(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    this.observe(owner, Observer { it?.let { observer(it) } })
}

inline fun <T> LiveData<T>.observeUntilOnDestroy(sourceActivity: Activity?, crossinline observer: (T?) -> Unit) {
    val valueObserver: Observer<T> = Observer {
        observer(it)
    }
    this.observeForever(valueObserver)
    sourceActivity?.application?.registerActivityLifecycleCallbacks(object : ActivityLifeCycleCallbackAdapter() {
        override fun onActivityDestroyed(activity: Activity?) {
            if (activity == sourceActivity) {
                activity.application?.unregisterActivityLifecycleCallbacks(this)
                this@observeUntilOnDestroy.removeObserver(valueObserver)
            }
        }
    })
}