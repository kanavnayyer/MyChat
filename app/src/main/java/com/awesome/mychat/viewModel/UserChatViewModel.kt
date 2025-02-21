package com.awesome.mychat.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    // ✅ Modified to support both text & image messages
    fun sendMessage(message: Message) {
        val chatId = generateChatId(message.senderId, message.receiverId)
        val chatRef = firestore.collection("chats").document(chatId)
        val messagesRef = chatRef.collection("messages")

        chatRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val chatData = hashMapOf(
                    "chatId" to chatId,
                    "lastMessage" to message.message,
                    "lastMessageTimestamp" to message.timestamp,
                    "participants" to listOf(message.senderId, message.receiverId)
                )
                chatRef.set(chatData, SetOptions.merge())
            }
            sendMessageToSubcollection(messagesRef, message, chatRef)
        }
    }

    private fun sendMessageToSubcollection(
        messagesRef: CollectionReference,
        message: Message,
        chatRef: DocumentReference
    ) {
        messagesRef.add(message)
            .addOnSuccessListener {
                val chatUpdates = mapOf(
                    "lastMessage" to message.message,
                    "lastMessageTimestamp" to message.timestamp
                )
                chatRef.update(chatUpdates)
            }
            .addOnFailureListener { e ->
                Log.e("UserChatViewModel", "Failed to send message", e)
            }
    }



    fun fetchMessages(senderId: String, receiverId: String) {
        val chatId = generateChatId(senderId, receiverId)

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messageList = snapshot?.documents?.mapNotNull { doc -> doc.toObject(Message::class.java) } ?: emptyList()
                _messages.value = messageList
            }
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }
}
