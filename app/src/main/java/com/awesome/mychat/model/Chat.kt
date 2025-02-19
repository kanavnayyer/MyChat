package com.awesome.mychat.model


data class Chat(
    val userId: String = "",
    val userName: String = "",
    val profileImage: String = "",
    val lastMessage: String = "",
    val timestamp: String = ""
)
