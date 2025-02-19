package com.awesome.mychat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.awesome.mychat.databinding.FragmentSearchScreenBinding
import com.awesome.mychat.ui.adapters.SearchAdapter

import com.awesome.mychat.viewModel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private lateinit var binding: FragmentSearchScreenBinding
    private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())

        usersViewModel.users.observe(viewLifecycleOwner) { userList ->
            binding.recyclerViewUsers.adapter = SearchAdapter(userList) { user ->
                openChatScreen(user)
            }
        }

        usersViewModel.fetchUsers()
    }

    private fun openChatScreen(user: com.awesome.mychat.model.User) {
        val action = UsersFragmentDirections.actionUsersFragmentToChatScreenFragment(user)
        findNavController().navigate(action)
    }
}
