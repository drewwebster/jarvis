package com.arunkumarsampath.jarvis.data.util

import android.arch.paging.PageKeyedDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebasePositionalDataSource<T>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : PageKeyedDataSource<String, T>() {

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        loadAtKey(params, callback)
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, T>) {
        val loadSize = params.requestedLoadSize
        databaseReference
                .limitToFirst(loadSize)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        callback.onResult(ArrayList(), null, null)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val (items, currentKey) = parseChildren(dataSnapshot)
                        callback.onResult(items, null, currentKey)
                        registerInvalidationCheck()
                    }
                })
    }

    private fun registerInvalidationCheck() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!isInvalid) {
                    invalidate()
                }
            }
        })
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
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        callback.onResult(ArrayList(), null)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val (items, currentKey) = parseChildren(dataSnapshot)
                        callback.onResult(items, currentKey)
                    }
                })
    }

    private fun parseChildren(dataSnapshot: DataSnapshot): Pair<List<T>, String> {
        val children = dataSnapshot.children
        val items = children.map(snapshotParser::parse).toList()
        val currentKey = children.last().key
        return Pair(items, currentKey)
    }
}