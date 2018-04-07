package com.arunkumarsampath.jarvis.data.util

import android.arch.paging.PageKeyedDataSource
import android.support.annotation.CallSuper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.atomic.AtomicInteger

class FirebasePositionalDataSource<T>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : PageKeyedDataSource<String, T>() {

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        loadAtKey(params, callback)
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, T>) {
        val loadSize = params.requestedLoadSize
        databaseReference.limitToFirst(loadSize).apply {
            addValueEventListener(object : InvalidateAwareListener() {

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.onResult(ArrayList(), null, null)
                }

                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val (items, currentKey) = parseChildren(dataSnapshot)
                    callback.onResult(items, null, currentKey)
                }
            })
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        loadAtKey(params, callback)
    }

    private fun loadAtKey(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        val key = params.key
        val loadSize = params.requestedLoadSize
        databaseReference.orderByKey()
                .startAt(key)
                .limitToFirst(loadSize)
                .addValueEventListener(object : InvalidateAwareListener() {
                    override fun onCancelled(databaseError: DatabaseError) {
                        callback.onResult(ArrayList(), null)
                    }

                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        val (items, currentKey) = parseChildren(dataSnapshot)
                        callback.onResult(items, currentKey)
                    }
                })
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

    abstract inner class InvalidateAwareListener : ValueEventListener {
        val version = AtomicInteger(0)

        override fun onCancelled(databaseError: DatabaseError) {
        }

        @CallSuper
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (version.incrementAndGet() > 1) {
                invalidate()
            } else {
                onDataChanged(dataSnapshot)
            }
        }

        abstract fun onDataChanged(dataSnapshot: DataSnapshot)
    }
}