package com.awesome.mychat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.mychat.databinding.FragmentChatScreenBinding
import com.awesome.mychat.model.Message
import com.awesome.mychat.ui.adapters.MessageAdapter
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

        userChatViewModel.fetchMessages(FirebaseAuth.getInstance().currentUser?.uid?:"", user.userId)

        userChatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
        }

        binding.btnSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                val message = Message(
                    senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    receiverId = user.userId,
                    messageText = messageText,
                    timestamp = System.currentTimeMillis()
                )
                userChatViewModel.sendMessage(message)
                binding.editTextMessage.text.clear()
            }
        }
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

}
