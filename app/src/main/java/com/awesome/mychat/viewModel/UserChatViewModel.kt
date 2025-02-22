package com.awesome.mychat.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.model.Message
import com.awesome.mychat.util.Constants
import com.awesome.mychat.util.Constants.chats
import com.awesome.mychat.util.Constants.lastMessage
import com.awesome.mychat.util.Constants.lastMessageTimestamp
import com.awesome.mychat.util.Constants.lastSeen
import com.awesome.mychat.util.Constants.participants
import com.awesome.mychat.util.Constants.timestamp
import com.awesome.mychat.util.Constants.users
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

    fun sendMessage(message: Message) {
        val chatId = generateChatId(message.senderId, message.receiverId)
        val chatRef = firestore.collection(chats).document(chatId)
        val messagesRef = chatRef.collection(Constants.messages)

        chatRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val chatData = hashMapOf(
                    chatId to chatId,
                    lastMessage to message.message,
                    lastMessageTimestamp to message.timestamp,
                    participants to listOf(message.senderId, message.receiverId)
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
                    lastMessage to message.message,
                    lastMessageTimestamp to message.timestamp
                )
                chatRef.update(chatUpdates)


                firestore.collection(users).document(message.senderId)
                    .update(lastSeen, message.timestamp)
            }

    }




    fun fetchMessages(senderId: String, receiverId: String) {
        val chatId = generateChatId(senderId, receiverId)

        firestore.collection(chats)
            .document(chatId)
            .collection(Constants.messages)
            .orderBy(timestamp, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messageList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                } ?: emptyList()

                _messages.value = messageList
            }
    }

    fun updateReceiverLastSeen(receiverId: String) {
        firestore.collection(users).document(receiverId)
            .update(lastSeen, System.currentTimeMillis())
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }
}
