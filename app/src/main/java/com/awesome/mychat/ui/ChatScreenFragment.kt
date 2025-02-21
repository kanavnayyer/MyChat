package com.awesome.mychat.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.mychat.R
import com.awesome.mychat.databinding.FragmentChatScreenBinding
import com.awesome.mychat.model.Message
import com.awesome.mychat.ui.adapters.MessageAdapter
import com.awesome.mychat.util.Constants.CAMERA_PERMISSION_CODE
import com.awesome.mychat.viewModel.UserChatViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class ChatScreenFragment : Fragment() {

    private lateinit var binding: FragmentChatScreenBinding
    private val args: ChatScreenFragmentArgs by navArgs()
    private val userChatViewModel: UserChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.user
        binding.user = user

        setupRecyclerView()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        userChatViewModel.fetchMessages(currentUserId, user.userId)

        userChatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages) {
                binding.recyclerViewMessages.post {
                    binding.recyclerViewMessages.smoothScrollToPosition(messages.size - 1) // 🔥 Smoothly scrolls to last message
                }
            }
        }


        binding.btnSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString().trim()

            if (messageText.isNotEmpty()) {
                val message = Message(
                    senderId = currentUserId,
                    receiverId = user.userId,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
                userChatViewModel.sendMessage(message)
                binding.editTextMessage.text.clear()
            }
        }
        binding.btnCam.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

                val action = ChatScreenFragmentDirections.actionChatScreenFragmentToCameraFragment(user = args.user)
                findNavController().navigate(action)

            } else {
                requestCameraPermission()
            }
        }

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }
    private fun setupRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        messageAdapter = MessageAdapter(currentUserId)

        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               
                findNavController().navigate(R.id.action_chatScreenFragment_to_cameraFragment)
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
