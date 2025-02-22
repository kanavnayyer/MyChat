package com.awesome.mychat.repository

import android.content.Context
import android.util.Log
import com.awesome.mychat.R
import com.awesome.mychat.model.User
import com.awesome.mychat.util.Constants.AuthRepository
import com.awesome.mychat.util.Constants.Unknown
import com.awesome.mychat.util.Constants.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    firebaseUser?.let { user ->
                        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                            val userData = User(
                                userId = user.uid,
                                name = user.displayName ?: Unknown,
                                profileImage = user.photoUrl?.toString() ?: "",
                                fcmToken = fcmToken,
                                lastSeen = System.currentTimeMillis()
                            )

                            saveUserToFirestore(userData)
                        }
                    }
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun saveUserToFirestore(user: User) {
        

        firestore.collection(users).document(user.userId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    firestore.collection(users).document(user.userId).set(user)
                }
            }
    }

}
