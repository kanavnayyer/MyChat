package com.awesome.mychat.repository

import com.awesome.mychat.model.User
import com.awesome.mychat.util.Constants.users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getUsers(): List<User> {
        return try {
            val snapshot = firestore.collection(users).get().await()
            snapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
