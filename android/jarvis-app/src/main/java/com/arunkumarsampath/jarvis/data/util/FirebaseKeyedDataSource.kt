package com.arunkumarsampath.jarvis.data.util

import android.arch.paging.ItemKeyedDataSource
import android.support.annotation.CallSuper
import com.arunkumarsampath.jarvis.data.conversation.KeyProvider
import com.google.firebase.database.*
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class FirebaseKeyedDataSource<T : KeyProvider>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : ItemKeyedDataSource<String, T>() {

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<T>) {
        loadAtKey(params, callback)
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<T>) {
        val loadSize = params.requestedLoadSize
        try {
            databaseReference
                    .orderByKey()
                    .limitToFirst(loadSize).apply {
                        addValueEventListener(object : InvalidateAwareListener(this) {

                            override fun onCancelled(databaseError: DatabaseError) {
                                callback.onResult(ArrayList())
                            }

                            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                                val (items, _) = parseChildren(dataSnapshot)
                                callback.onResult(items)
                            }
                        })
                    }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<T>) {
        loadAtKey(params, callback)
    }

    override fun getKey(item: T) = item.key()

    private fun loadAtKey(params: LoadParams<String>, callback: LoadCallback<T>) {
        val key = params.key
        val loadSize = params.requestedLoadSize
        databaseReference
                .orderByKey()
                .startAt(key)
                .limitToFirst(loadSize).apply {
                    addValueEventListener(object : InvalidateAwareListener(this) {
                        override fun onCancelled(databaseError: DatabaseError) {
                            callback.onResult(ArrayList())
                        }

                        override fun onDataChanged(dataSnapshot: DataSnapshot) {
                            val (items, currentKey) = parseChildren(dataSnapshot)
                            callback.onResult(items.subList(1, items.size))
                        }
                    })
                }
    }

    private fun parseChildren(dataSnapshot: DataSnapshot): Pair<List<T>, String> {
        val children = dataSnapshot.children
        var lastKey = ""
        val items: ArrayList<T> = ArrayList()
        for (snapshot in children) {
            items.add(snapshotParser.parse(snapshot))
            lastKey = snapshot.key
        }
        return Pair(items as List<T>, lastKey)
    }

    abstract inner class InvalidateAwareListener(val query: Query) : ValueEventListener {
        val version = AtomicInteger(0)

        @CallSuper
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Timber.d("Version")
            if (version.incrementAndGet() > 1) {
                query.removeEventListener(this)
                invalidate()
            } else {
                onDataChanged(dataSnapshot)
            }
        }

        abstract fun onDataChanged(dataSnapshot: DataSnapshot)
    }
}