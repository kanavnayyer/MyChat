package com.awesome.mychat.model

import com.awesome.mychat.util.Constants.Text


data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val type: String = Text
)
