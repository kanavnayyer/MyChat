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
import com.awesome.mychat.util.Constants
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
                Toast.makeText(this,
                    getString(R.string.image_selection_canceled), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        val user = intent.getParcelableExtra<User>(Constants.user)
        binding.user = user

        binding.buttonSave.setOnClickListener { saveUser() }
        binding.settingProfileImage.setOnClickListener { openGallery() }


        chatViewModel.updateStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this,
                    getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this,
                    getString(R.string.failed_to_update_profile), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = getString(R.string.image)
        pickImageLauncher.launch(intent)
    }

    private fun saveUser() {
        val newName = binding.editname.text.toString().trim()

        if (newName.length < 3) {
            Toast.makeText(this, getString(R.string.please_enter_a_longer_name), Toast.LENGTH_SHORT).show()
            return
        }

        chatViewModel.updateUserName(newName)
    }
}
