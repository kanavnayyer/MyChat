package com.awesome.mychat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesome.mychat.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
    val authResult: LiveData<Pair<Boolean, String?>> = _authResult

    init {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }

    fun checkUserLoggedIn() {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }

    fun signInWithGoogle(idToken: String) {
        authRepository.signInWithGoogle(idToken) { success, errorMessage ->
            _authResult.postValue(Pair(success, errorMessage))
            _isUserLoggedIn.postValue(success)
        }
    }
}
