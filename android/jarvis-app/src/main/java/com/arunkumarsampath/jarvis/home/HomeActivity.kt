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
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import com.arunkumarsampath.jarvis.R
import com.arunkumarsampath.jarvis.di.activity.ActivityComponent
import com.arunkumarsampath.jarvis.di.viewmodel.ViewModelFactory
import com.arunkumarsampath.jarvis.extensions.clicks
import com.arunkumarsampath.jarvis.extensions.gone
import com.arunkumarsampath.jarvis.extensions.show
import com.arunkumarsampath.jarvis.extensions.watchNonNull
import com.arunkumarsampath.jarvis.home.conversation.ConversationAdapter
import com.arunkumarsampath.jarvis.util.common.base.BaseActivity
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector.HotwordEvent.Detected
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector.HotwordStatus.Started
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector.HotwordStatus.Stopped
import com.arunkumarsampath.jarvis.voice.speech.AndroidSpeechRecognizer
import com.arunkumarsampath.jarvis.voice.speech.AndroidSpeechRecognizer.Companion.NO_COMMAND
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.SwitchDrawerItem
import com.tbruyelle.rxpermissions2.RxPermissions
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
    lateinit var androidSpeechRecognizer: AndroidSpeechRecognizer
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var conversationAdapter: ConversationAdapter

    private var speechRecognizerSubject = PublishSubject.create<Int>()

    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this, factory).get(HomeViewModel::class.java) }

    private var isLoggedIn = false
        get() = auth.currentUser != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDrawer()
        setupChatUi()
        observeViewModel()
        setupRecognition()
    }

    private fun setupDrawer() {
        setSupportActionBar(toolbar)
        toolbar.setSubtitleTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
        with(DrawerBuilder()) {
            withActivity(this@HomeActivity)
            withActionBarDrawerToggle(false)
            withToolbar(toolbar)
            addDrawerItems(
                    SwitchDrawerItem().apply {
                        withName(R.string.docked)
                        withChecked(homeViewModel.isDeviceDocked)
                        withOnCheckedChangeListener { _, _, isChecked -> homeViewModel.isDeviceDocked = isChecked }
                    }
            )
            build()
        }
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
                    .doOnNext { speechRecognizerSubject.onNext(0) }
                    .subscribe())

            subs.add(Observable.merge(speechRecognizerSubject, voiceCommandButton.clicks().map { 0 })
                    .doOnNext { hotwordDetector.stop() }
                    .flatMapSingle { androidSpeechRecognizer.speechDetections }
                    .delay(300, TimeUnit.MILLISECONDS)
                    .doOnNext { hotwordDetector.start() }
                    .filter { it != NO_COMMAND }
                    .doOnNext { homeViewModel.sendCommand(it) }
                    .subscribe())

            subs.add(hotwordDetector.hotwordStatus
                    .observeOn(schedulerProvider.ui())
                    .subscribe { status ->
                        TransitionManager.beginDelayedTransition(toolbar)
                        when (status) {
                            is Started -> toolbar.setSubtitle(R.string.listening)
                            is Stopped -> toolbar.subtitle = null
                        }
                    })
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
        val owner = this
        homeViewModel.run {
            conversationItemsLiveData.watchNonNull(owner) { list ->
                conversationAdapter.currentList = list
                if (list.isNotEmpty()) {
                    chatRecyclerView.apply {
                        postDelayed({ smoothScrollToPosition(conversationAdapter.currentList.size - 1) }, 100)
                    }
                }
            }
            conversationLoadingLiveData.watchNonNull(owner) { loading ->
                TransitionManager.endTransitions(bottomCard)
                TransitionManager.beginDelayedTransition(bottomCard)
                if (loading) {
                    messageSendButton.gone()
                    progressBar.show()
                } else {
                    messageSendButton.show()
                    progressBar.gone()
                }
            }
        }
    }


    private fun setupChatUi() {
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity).apply { stackFromEnd = true }
            adapter = conversationAdapter
        }

        subs.add(messageSendButton.clicks()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { messageEditText.text.trim().toString() }
                .filter { it.isNotEmpty() }
                .doOnNext {
                    homeViewModel.sendCommand(it)
                    messageEditText.setText("")
                }.subscribe())
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        subs.add(RxFirebaseAuth.signInWithCredential(auth, credential)
                .subscribeOn(schedulerProvider.io())
                .map { authResult -> authResult.user != null }
                .observeOn(schedulerProvider.ui())
                .subscribe({ logged ->
                    Timber.d("Logged $logged")
                    homeViewModel.loadConversations()
                    setupRecognition()
                }, Timber::e))
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
