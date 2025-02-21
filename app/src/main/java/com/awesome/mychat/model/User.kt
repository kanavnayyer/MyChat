package com.awesome.mychat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: String = "",
    val name: String = "",
    val profileImage: String = "",
    val fcmToken: String = "",
    var lastSeen: Long = 0L
) : Parcelable
