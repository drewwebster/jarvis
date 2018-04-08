package com.arunkumarsampath.jarvis.util.firebase

import com.google.firebase.database.DataSnapshot

interface SnapshotParser<out R> {
    fun parse(dataSnapshot: DataSnapshot): R
}