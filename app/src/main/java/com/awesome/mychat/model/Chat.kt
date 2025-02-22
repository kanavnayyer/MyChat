package com.awesome.mychat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val chatId: String = "",
    val userId: String = "",
    val userName: String = "",
    val profileImage: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
) : Parcelable
