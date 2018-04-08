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

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.arunkumarsampath.jarvis.R
import com.arunkumarsampath.jarvis.common.base.BaseActivity
import com.arunkumarsampath.jarvis.di.activity.ActivityComponent
import com.arunkumarsampath.jarvis.di.viewmodel.ViewModelFactory
import com.arunkumarsampath.jarvis.extensions.watch
import com.arunkumarsampath.jarvis.home.conversation.ConversationAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import durdinapps.rxfirebase2.RxFirebaseAuth
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

    private val conversationAdapter = ConversationAdapter()

    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this, factory).get(HomeViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setupChatUi()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
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
        homeViewModel.conversationItemsLiveData.watch(this, conversationAdapter::submitList)
    }


    private fun setupChatUi() {
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity).apply { stackFromEnd = true }
            adapter = conversationAdapter
        }
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        subs.add(RxFirebaseAuth.signInWithCredential(auth, credential)
                .subscribeOn(Schedulers.io())
                .map { authResult -> authResult.user != null }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ logged -> Timber.d("Logged $logged") }, Timber::e))
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
