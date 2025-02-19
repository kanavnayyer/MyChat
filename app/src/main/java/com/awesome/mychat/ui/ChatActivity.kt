package com.awesome.mychat.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.awesome.mychat.R
import com.awesome.mychat.util.Constants.CHATActivity
import com.awesome.mychat.viewModel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        window.statusBarColor = getColor(android.R.color.white)

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        val titleTextView = findViewById<TextView>(R.id.titletv)

        chatViewModel.titleText.observe(this, Observer { title ->
            titleTextView.text = title
        })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
            Log.d(CHATActivity, getString(R.string.navcontroller_initialized_successfully))
        } else {
            Log.e(CHATActivity, getString(R.string.navhostfragment_is_null_check_activity_chat_xml_for_nav_host_fragment_id))
        }
    }



    fun navigateTo(view: View) {
        navController.navigate(R.id.action_chatFragment_to_usersFragment)
    }


}
