package com.example.prototipo_canino

import com.google.firebase.auth.FirebaseUser

class SnapshotsApplication {

    companion object {
        const val PATH_SNAPSHOTS = "snapshots"
        const val PROPERTY_LIKE_LIST = "likeList"

        lateinit var currentUser: FirebaseUser

    }
}