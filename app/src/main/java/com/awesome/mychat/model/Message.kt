package com.awesome.mychat.model


data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val messageText: String = "",
    val timestamp: Long = 0L
)
