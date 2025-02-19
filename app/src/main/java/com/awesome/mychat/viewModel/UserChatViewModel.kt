package com.awesome.mychat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.model.Message
import com.awesome.mychat.util.Constants.Chats
import com.awesome.mychat.util.Constants.ReceiverId
import com.awesome.mychat.util.Constants.SenderId
import com.awesome.mychat.util.Constants.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    fun sendMessage(message: Message) {
        val chatRef = firestore.collection(Chats)

        chatRef.add(message)
            .addOnSuccessListener {
                Log.d("UserChatViewModel", " Message sent successfully: ${message.messageText}")
            }
            .addOnFailureListener { e ->
                Log.e("UserChatViewModel", " Failed to send message", e)
            }
    }

    fun fetchMessages(senderId: String, receiverId: String) {
        firestore.collection(Chats)
            .whereIn(SenderId, listOf(senderId, receiverId))
            .whereIn(ReceiverId, listOf(senderId, receiverId))
            .orderBy(Timestamp)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserChatViewModel", " Error fetching messages", error)
                    return@addSnapshotListener
                }

                val messageList = snapshot?.documents?.map { doc ->
                    doc.toObject(Message::class.java)!!
                } ?: emptyList()

                _messages.value = messageList
                Log.d("UserChatViewModel", " Fetched messages: ${messageList.size}")
            }
    }
}
