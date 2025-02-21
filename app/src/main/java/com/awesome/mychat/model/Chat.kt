package com.awesome.mychat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val chatId: String = "",  // Unique chat ID (can be Firestore doc ID)
    val userId: String = "",  // Other user's ID
    val userName: String = "",  // Other user's Name
    val profileImage: String = "",  // Other user's Profile Image
    val lastMessage: String = "",  // Last message sent/received
    val timestamp: Long = 0L  // Time of the last message
) : Parcelable
