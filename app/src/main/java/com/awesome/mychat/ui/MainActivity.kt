package com.awesome.mychat.ui
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.awesome.mychat.R
import com.awesome.mychat.util.Constants.MAINactivity
import com.awesome.mychat.viewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupGoogleSignIn()


        authViewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                checkNotificationPermission()
//                startActivity(Intent(this, ChatActivity::class.java))
//                finish()
            }
        }


        authViewModel.authResult.observe(this) { (success, errorMessage) ->
            if (!success) {
                Toast.makeText(this,
                    getString(R.string.authentication_failed, errorMessage), Toast.LENGTH_LONG).show()
            }
        }


        val signInButton = findViewById<LinearLayout>(R.id.linlay)
        signInButton.setOnClickListener {
            Log.d(MAINactivity, getString(R.string.sign_in_button_clicked))
            signIn()
        }

    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    goToChatScreen()  // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // User previously denied, show a message before requesting
                    Toast.makeText(this, "Allow notifications for better experience", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            goToChatScreen()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            goToChatScreen()
        }
    private fun goToChatScreen() {
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            authViewModel.signInWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}
