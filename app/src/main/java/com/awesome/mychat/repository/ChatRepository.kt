package com.awesome.mychat.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awesome.mychat.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun sendMessage(message: Message) {
        firestore.collection("chats")
            .add(message)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun getMessages(senderId: String, receiverId: String): LiveData<List<Message>> {
        val messagesLiveData = MutableLiveData<List<Message>>()

        firestore.collection("chats")
            .whereIn("senderId", listOf(senderId, receiverId))
            .whereIn("receiverId", listOf(senderId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    messagesLiveData.value = messages
                }
            }
        return messagesLiveData
    }
}
