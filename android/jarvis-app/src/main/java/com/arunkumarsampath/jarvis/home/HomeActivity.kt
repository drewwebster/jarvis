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

package com.arunkumarsampath.jarvis.home

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent.EXTRA_RESULTS
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.arunkumarsampath.jarvis.R
import com.arunkumarsampath.jarvis.di.activity.ActivityComponent
import com.arunkumarsampath.jarvis.di.viewmodel.ViewModelFactory
import com.arunkumarsampath.jarvis.extensions.hide
import com.arunkumarsampath.jarvis.extensions.hideKeyboard
import com.arunkumarsampath.jarvis.extensions.show
import com.arunkumarsampath.jarvis.extensions.watch
import com.arunkumarsampath.jarvis.home.conversation.ConversationAdapter
import com.arunkumarsampath.jarvis.util.Util
import com.arunkumarsampath.jarvis.util.common.base.BaseActivity
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector.HotwordEvent.Detected
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jakewharton.rxbinding2.view.RxView
import com.petarmarijanovic.rxactivityresult.RxActivityResult
import com.tbruyelle.rxpermissions2.RxPermissions
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class HomeActivity : BaseActivity() {
    override val layoutRes = R.layout.activity_main

    override fun inject(activityComponent: ActivityComponent) = activityComponent.inject(this)

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var factory: ViewModelFactory
    @Inject
    lateinit var rxPermission: RxPermissions
    @Inject
    lateinit var hotwordDetector: HotwordDetector
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    lateinit var rxActivityResult: RxActivityResult

    private var isLoggedIn = false
        get() = auth.currentUser != null

    private val conversationAdapter = ConversationAdapter()

    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this, factory).get(HomeViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxActivityResult = RxActivityResult(this)

        setSupportActionBar(toolbar)
        setupChatUi()
        observeViewModel()

        setupRecognition()
    }

    private fun setupRecognition() {
        if (isLoggedIn) {
            subs.add(rxPermission
                    .requestEach(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE)
                    .subscribe { permission ->
                        if (permission.granted) {
                            hotwordDetector.start()
                        }
                    })
            subs.add(hotwordDetector.hotwordEvents
                    .observeOn(schedulerProvider.ui())
                    .filter { it is Detected }
                    .doOnNext { hotwordDetector.stop() }
                    .flatMapSingle {
                        rxActivityResult.start(Util.getRecognizerIntent(this))
                                .map { result ->
                                    if (result.data != null) {
                                        val results: ArrayList<out String>? = result.data.getStringArrayListExtra(EXTRA_RESULTS)
                                        return@map results!![0]
                                    } else return@map NO_COMMAND
                                }
                    }.doOnNext { hotwordDetector.start() }
                    .filter { it != NO_COMMAND }
                    .doOnNext { homeViewModel.sendPush(it) }
                    .subscribe())
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isLoggedIn) {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        } else {
            homeViewModel.loadConversations()
        }
    }

    override fun onResume() {
        super.onResume()
        hotwordDetector.start()
    }

    override fun onPause() {
        hotwordDetector.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    fireBaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun observeViewModel() {
        homeViewModel.conversationItemsLiveData.watch(this) { list ->
            if (list != null) {
                conversationAdapter.currentList = list
                if (list.isNotEmpty()) {
                    chatRecyclerView.apply {
                        postDelayed({ smoothScrollToPosition(conversationAdapter.currentList.size - 1) }, 100)
                    }
                }
            }
        }
    }


    private fun setupChatUi() {
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity).apply { stackFromEnd = true }
            adapter = conversationAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    val visibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (visibleItem == adapter.itemCount - 1) {
                        if (messageEditText.visibility != View.VISIBLE) {
                            TransitionManager.beginDelayedTransition(bottomCard)
                            messageEditText.show()
                            messageSend.show()
                        }
                    } else {
                        if (messageEditText.visibility != View.INVISIBLE) {
                            TransitionManager.beginDelayedTransition(bottomCard)
                            messageEditText.hide()
                            messageSend.hide()
                        }
                    }
                }
            })
        }

        subs.add(RxView.clicks(messageSend)
                .toFlowable(BackpressureStrategy.LATEST)
                .map { messageEditText.text.trim().toString() }
                .filter { it.isNotEmpty() }
                .doOnNext {
                    homeViewModel.sendPush(it)
                    messageEditText.run {
                        setText("")
                        hideKeyboard()
                    }
                }.subscribe())
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        subs.add(RxFirebaseAuth.signInWithCredential(auth, credential)
                .subscribeOn(Schedulers.io())
                .map { authResult -> authResult.user != null }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ logged ->
                    Timber.d("Logged $logged")
                    homeViewModel.loadConversations()
                    setupRecognition()
                }, Timber::e))
    }

    private fun onHotwordDetected() {
        Timber.d("Hotword detected")
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val NO_COMMAND = "no-command"

    }


}
