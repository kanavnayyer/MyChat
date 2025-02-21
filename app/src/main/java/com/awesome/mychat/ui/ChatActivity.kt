package com.awesome.mychat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.awesome.mychat.R
import com.awesome.mychat.databinding.ActivityChatBinding
import com.awesome.mychat.databinding.ActivityEditProfileBinding
import com.awesome.mychat.util.Constants.CHATActivity
import com.awesome.mychat.viewModel.ChatViewModel
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        chatViewModel.fetchCurrentUser()
        binding.lifecycleOwner = this
        chatViewModel.userLiveData.observe(this) { user -> binding.user = user }

        window.statusBarColor = getColor(android.R.color.white)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true

        val titleTextView = findViewById<TextView>(R.id.titletv)
        chatViewModel.titleText.observe(this) { title ->
            titleTextView.text = title
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        }





        chatViewModel.updateStatus.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    this,
                    getString(R.string.name_updated_successfully), Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, getString(R.string.failed_to_update_name), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun navigateToEdit(view: View) {
        chatViewModel.fetchCurrentUser()
        chatViewModel.userLiveData.observe(this) { user ->
            user?.let {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra(getString(R.string.user), it)
                startActivity(intent)
            }


        }
    }


}
