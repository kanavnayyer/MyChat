package com.awesome.mychat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.mychat.databinding.FragmentChatBinding
import com.awesome.mychat.model.Chat
import com.awesome.mychat.model.User
import com.awesome.mychat.ui.adapters.ChatAdapter
import com.awesome.mychat.viewModel.RecentUsersChatViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel: RecentUsersChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()


        chatViewModel.chatUsers.observe(viewLifecycleOwner) { chatList ->
            chatAdapter.submitList(chatList)
        }


        chatViewModel.listenForChatUpdates(currentUserId)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter { chat ->

            val user = User(
                userId = chat.userId,
                name = chat.userName,
                profileImage = chat.profileImage,
                fcmToken = "",
                lastSeen = chat.timestamp
            )
            val action = ChatFragmentDirections.actionChatFragmentToChatScreenFragment(user)
            findNavController().navigate(action)
        }

        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }


}
