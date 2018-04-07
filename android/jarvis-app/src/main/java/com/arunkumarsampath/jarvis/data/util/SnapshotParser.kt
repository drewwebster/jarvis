package com.arunkumarsampath.jarvis.data.util

import com.google.firebase.database.DataSnapshot

interface SnapshotParser<out R> {
    fun parse(dataSnapshot: DataSnapshot): R
}