package com.awesome.mychat.viewModel



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.model.User
import com.awesome.mychat.util.Constants.RemoteConfig
import com.awesome.mychat.util.Constants.Title

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _titleText = MutableLiveData<String>()
    val titleText: LiveData<String> get() = _titleText

    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = _userLiveData

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus



    private val auth = FirebaseAuth.getInstance()

    init {
        setupRemoteConfig()
        fetchRemoteConfig()
    }

    private fun setupRemoteConfig() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    applyFetchedConfig()
                } else {
                    Log.e(RemoteConfig, "Fetch failed")
                }
            }
    }

    private fun applyFetchedConfig() {
        val showTitle = firebaseRemoteConfig.getString(Title)
        _titleText.value = showTitle
        Log.d(RemoteConfig, "Title updated: $showTitle")
    }

    fun fetchCurrentUser() {
        val currentUser = auth.currentUser
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = User(
                            userId = document.getString("userId") ?: "",
                            name = document.getString("name") ?: "",
                            profileImage = document.getString("profileImage") ?: "",
                            fcmToken = document.getString("fcmToken") ?: "",
                            lastSeen = document.getLong("lastSeen") ?: 0L
                        )
                        _userLiveData.postValue(user)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ChatViewModel", "Failed to fetch user data", e)
                    _userLiveData.postValue(null)
                }
        }
    }

    fun updateUserName(newName: String) {
        val currentUser = auth.currentUser
        currentUser?.uid?.let { userId ->
            val userRef = firestore.collection("users").document(userId)

            userRef.update("name", newName)
                .addOnSuccessListener {
                    Log.d("ChatViewModel", "User name updated successfully")
                    _updateStatus.postValue(true)
                }
                .addOnFailureListener { e ->
                    Log.e("ChatViewModel", "Failed to update name", e)
                    _updateStatus.postValue(false)
                }
        }
    }



}
