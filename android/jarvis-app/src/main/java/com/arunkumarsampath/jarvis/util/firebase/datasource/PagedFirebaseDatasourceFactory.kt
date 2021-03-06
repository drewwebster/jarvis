package com.arunkumarsampath.jarvis.util.firebase.datasource

import android.arch.paging.DataSource
import com.arunkumarsampath.jarvis.util.common.firebase.KeyProvider
import com.arunkumarsampath.jarvis.util.firebase.SnapshotParser
import com.google.firebase.database.DatabaseReference

class PagedFirebaseDatasourceFactory<T : KeyProvider>(
        private val snapshotParser: SnapshotParser<T>,
        private val databaseReference: DatabaseReference
) : DataSource.Factory<String, T>() {

    override fun create(): DataSource<String, T> {
        return FirebaseKeyedDataSource(snapshotParser, databaseReference)
    }
}