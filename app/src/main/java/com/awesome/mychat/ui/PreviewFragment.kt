package com.awesome.mychat.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesome.mychat.R
import com.awesome.mychat.databinding.FragmentPreviewBinding
import com.awesome.mychat.model.Message
import com.awesome.mychat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class PreviewFragment : Fragment() {

    private lateinit var binding: FragmentPreviewBinding
    private val args: PreviewFragmentArgs by navArgs()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUri = Uri.parse(args.imageUri)
        val user: User = args.user

        Log.d("PreviewFragment", "Received Image URI: $imageUri")
        Log.d("PreviewFragment", "Received User: ${user.userId}")

        binding.imagePreview.setImageURI(imageUri)

        binding.sendButton.setOnClickListener {
            Log.d("PreviewFragment", "Send button clicked")
            sendMessage(user, imageUri)
        }
    }

    private fun sendMessage(user: User, imageUri: Uri) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        Log.d("PreviewFragment", "Current User ID: $currentUserId")

        if (currentUserId.isEmpty()) {
            Log.e("PreviewFragment", "User not logged in, cannot send message")
            return
        }

        val imagePath = saveImageLocally(imageUri)
        if (imagePath.isEmpty()) {
            Log.e("PreviewFragment", "Failed to save image locally")
            return
        }

        val chatId = generateChatId(currentUserId, user.userId)
        val chatRef = firestore.collection("chats").document(chatId)
        val messagesRef = chatRef.collection("messages")

        val chatMessage = Message(
            senderId = currentUserId,
            receiverId = user.userId,
            message = imagePath.toString(),
            timestamp = System.currentTimeMillis(),
            type = "image"
        )

        messagesRef.add(chatMessage)
            .addOnSuccessListener {
                Log.d("PreviewFragment", "Message sent successfully, navigating back")
                findNavController().popBackStack(R.id.chatScreenFragment, false)
            }
            .addOnFailureListener { e ->
                Log.e("PreviewFragment", "Error sending message: ${e.message}", e)
            }
    }

    private fun saveImageLocally(imageUri: Uri): String {
        val context = requireContext()
        val imagesDir = File(context.filesDir, "chat_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        val imageFile = File(imagesDir, "IMG_${System.currentTimeMillis()}.jpg")

        return try {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("PreviewFragment", "Image saved locally: ${imageFile.absolutePath}")
            imageFile.absolutePath
        } catch (e: Exception) {
            Log.e("PreviewFragment", "Error saving image locally: ${e.message}", e)
            ""
        }
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }
}
