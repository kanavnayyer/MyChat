package com.awesome.mychat.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awesome.mychat.model.Message
import com.awesome.mychat.util.Constants.chats
import com.awesome.mychat.util.Constants.messages
import com.awesome.mychat.util.Constants.timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun sendMessage(message: Message) {
        val chatId = generateChatId(message.senderId, message.receiverId)
        val chatRef = firestore.collection(chats).document(chatId)
        val messagesRef = chatRef.collection(messages)

        messagesRef.add(message)
            .addOnSuccessListener {
                chatRef.set(
                    hashMapOf(
                        "chatId" to chatId,
                        "lastMessage" to message.message,
                        "lastMessageTimestamp" to message.timestamp,
                        "participants" to listOf(message.senderId, message.receiverId)
                    ),
                    SetOptions.merge()
                )
            }
            .addOnFailureListener { }
    }

    fun getMessages(senderId: String, receiverId: String): LiveData<List<Message>> {
        val messagesLiveData = MutableLiveData<List<Message>>()
        val chatId = generateChatId(senderId, receiverId)

        firestore.collection(chats)
            .document(chatId)
            .collection(messages)
            .orderBy(timestamp, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    messagesLiveData.value = messages
                }
            }
        return messagesLiveData
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }
}
