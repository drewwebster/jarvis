package com.arunkumarsampath.jarvis.data.util

import android.arch.paging.DataSource
import com.google.firebase.database.DatabaseReference

class PagedFirebaseDatasourceFactory<T>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : DataSource.Factory<String, T>() {

    override fun create(): DataSource<String, T> {
        return FirebasePositionalDataSource(snapshotParser, databaseReference)
    }
}