package com.awesome.mychat.model




data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val type: String = "text"
)
