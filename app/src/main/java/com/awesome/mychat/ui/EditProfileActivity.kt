package com.awesome.mychat.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.awesome.mychat.R
import com.awesome.mychat.databinding.ActivityEditProfileBinding
import com.awesome.mychat.model.User
import com.awesome.mychat.viewModel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var binding: ActivityEditProfileBinding
    private var selectedImageUri: Uri? = null


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.settingProfileImage.setImageURI(uri)
                }
            } else {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        val user = intent.getParcelableExtra<User>("user")
        binding.user = user

        binding.buttonSave.setOnClickListener { saveUser() }
        binding.settingProfileImage.setOnClickListener { openGallery() }


        chatViewModel.updateStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun saveUser() {
        val newName = binding.editname.text.toString().trim()

        if (newName.length < 3) {
            Toast.makeText(this, "Please enter a longer name", Toast.LENGTH_SHORT).show()
            return
        }

        chatViewModel.updateUserName(newName)
    }
}
