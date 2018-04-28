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

package com.arunkumarsampath.jarvis.util.common.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arunkumarsampath.jarvis.Jarvis
import com.arunkumarsampath.jarvis.di.activity.ActivityComponent
import com.arunkumarsampath.jarvis.di.activity.ActivityModule
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {
    private var unbinder: Unbinder? = null

    protected var activityComponent: ActivityComponent? = null
    protected val subs = CompositeDisposable()

    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent = (application as Jarvis)
                .appComponent
                .newActivityComponent(ActivityModule(this))
        inject(activityComponent!!)

        super.onCreate(savedInstanceState)

        @LayoutRes val layoutRes = layoutRes
        if (layoutRes != 0) {
            setContentView(layoutRes)
            unbinder = ButterKnife.bind(this)
        }
    }

    internal abstract fun inject(activityComponent: ActivityComponent)

    override fun onDestroy() {
        subs.clear()
        unbinder?.unbind()
        activityComponent = null
        super.onDestroy()
    }
}
