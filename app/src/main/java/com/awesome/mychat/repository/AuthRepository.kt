package com.awesome.mychat.repository

import android.util.Log
import com.awesome.mychat.model.User
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
                                name = user.displayName ?: "Unknown",
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
        Log.d("AuthRepository", " saveUserToFirestore called for user: ${user.userId}")

        firestore.collection("users").document(user.userId).get()
            .addOnSuccessListener { document ->
                Log.d("AuthRepository", " Firestore document exists: ${document.exists()}")

                if (!document.exists()) {
                    firestore.collection("users").document(user.userId).set(user)
                        .addOnSuccessListener {
                            Log.d("AuthRepository", " User saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("AuthRepository", " Error saving user", e)
                        }
                } else {
                    Log.d("AuthRepository", " User already exists, no need to save again")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthRepository", " Error fetching document", e)
            }
    }

}
