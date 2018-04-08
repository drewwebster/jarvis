package com.arunkumarsampath.jarvis.data.util

import android.arch.paging.DataSource
import com.arunkumarsampath.jarvis.data.conversation.KeyProvider
import com.google.firebase.database.DatabaseReference

class PagedFirebaseDatasourceFactory<T : KeyProvider>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : DataSource.Factory<String, T>() {

    override fun create(): DataSource<String, T> {
        return FirebaseKeyedDataSource(snapshotParser, databaseReference)
    }
}