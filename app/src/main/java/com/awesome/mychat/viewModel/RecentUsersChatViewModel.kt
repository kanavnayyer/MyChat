package com.awesome.mychat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.model.Chat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentUsersChatViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _chatUsers = MutableLiveData<List<Chat>>()
    val chatUsers: LiveData<List<Chat>> get() = _chatUsers

    private var listenerRegistration: ListenerRegistration? = null

    fun listenForChatUpdates(currentUserId: String) {

        listenerRegistration?.remove()

        listenerRegistration = firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RecentChatVM", "Error listening for chat updates", error)
                    return@addSnapshotListener
                }
                if (snapshot == null || snapshot.isEmpty) {
                    Log.d("RecentChatVM", "No chat documents found for user $currentUserId")
                    _chatUsers.postValue(emptyList())
                    return@addSnapshotListener
                }

                Log.d("RecentChatVM", "Total chat documents: ${snapshot.documents.size}")
                val chatsList = mutableListOf<Chat>()
                var processedCount = 0
                val totalDocs = snapshot.documents.size

                for (document in snapshot.documents) {
                    Log.d("RecentChatVM", "Chat document: ${document.data}")

                    val chatId = document.getString("chatId") ?: document.id
                    val lastMessage = document.getString("lastMessage") ?: ""
                    val lastMessageTimestamp = document.getLong("lastMessageTimestamp") ?: 0L
                    val lastMessageType = document.getString("lastMessageType") ?: "text"
                    val participants = document.get("participants") as? List<String> ?: emptyList()

                    val lastMessageDisplay = if (lastMessageType == "image") "\uD83D\uDCF7 Image" else lastMessage

                    val otherUserId = participants.firstOrNull { it != currentUserId }
                    if (otherUserId == null) {
                        Log.w("RecentChatVM", "No other participant found in chat: $chatId")
                        processedCount++
                        if (processedCount == totalDocs) {
                            _chatUsers.postValue(chatsList.sortedByDescending { it.timestamp })
                        }
                        continue
                    }

                    firestore.collection("users").document(otherUserId).get()
                        .addOnSuccessListener { userDoc ->
                            val userName = userDoc.getString("name") ?: "Unknown"
                            val profileImage = userDoc.getString("profileImage") ?: ""
                            val chat = Chat(
                                chatId = chatId,
                                userId = otherUserId,
                                userName = userName,
                                profileImage = profileImage,
                                lastMessage = lastMessageDisplay,
                                timestamp = lastMessageTimestamp
                            )
                            chatsList.add(chat)
                            processedCount++
                            if (processedCount == totalDocs) {
                                _chatUsers.postValue(chatsList.sortedByDescending { it.timestamp })
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("RecentChatVM", "Failed to fetch user info for $otherUserId", e)
                            processedCount++
                            if (processedCount == totalDocs) {
                                _chatUsers.postValue(chatsList.sortedByDescending { it.timestamp })
                            }
                        }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
