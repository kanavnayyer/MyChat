package com.awesome.mychat.viewModel



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.R
import com.awesome.mychat.util.Constants.RemoteConfig
import com.awesome.mychat.util.Constants.Title
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private val _titleText = MutableLiveData<String>()
    val titleText: LiveData<String> get() = _titleText

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
                    Log.e(R.string.remoteconfig.toString(), "Fetch failed")
                }
            }
    }

    private fun applyFetchedConfig() {
        val showTitle = firebaseRemoteConfig.getString(Title)
        _titleText.value = showTitle
        Log.d(RemoteConfig, "Title updated: $showTitle")
    }
}
