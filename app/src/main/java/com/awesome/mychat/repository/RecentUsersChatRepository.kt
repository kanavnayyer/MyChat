package com.awesome.mychat.repository

import com.awesome.mychat.model.Chat
import com.awesome.mychat.util.Constants.chats
import com.awesome.mychat.util.Constants.lastMessageTimestamp
import com.awesome.mychat.util.Constants.participants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecentUsersChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getRecentChats(currentUserId: String): List<Chat> {
        return try {
            val snapshot = firestore.collection(chats)
                .whereArrayContains(participants, currentUserId)
                .orderBy(lastMessageTimestamp, Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
