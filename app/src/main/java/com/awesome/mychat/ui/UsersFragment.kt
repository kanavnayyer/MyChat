package com.awesome.mychat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.awesome.mychat.databinding.FragmentSearchScreenBinding
import com.awesome.mychat.model.User
import com.awesome.mychat.ui.adapters.SearchAdapter

import com.awesome.mychat.viewModel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private lateinit var binding: FragmentSearchScreenBinding
    private val usersViewModel: UsersViewModel by viewModels()
    private var allUsers: List<User> = emptyList()

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
            allUsers = userList
            binding.recyclerViewUsers.adapter = SearchAdapter(emptyList()) { user ->
                openChatScreen(user)
            }
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()

                val filteredList = if (query.isEmpty()) {
                    emptyList()
                } else {
                    allUsers.filter { user ->
                        user.name.lowercase().contains(query)
                    }
                }
                binding.recyclerViewUsers.adapter = SearchAdapter(filteredList) { user ->
                    openChatScreen(user)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        usersViewModel.fetchUsers()
    }

    private fun openChatScreen(user: User) {
        val action = UsersFragmentDirections.actionUsersFragmentToChatScreenFragment(user)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()


        usersViewModel.fetchUsers()


        usersViewModel.users.observe(viewLifecycleOwner) { userList ->
            allUsers = userList


            val query = binding.searchView.text.toString().trim().lowercase()
            val filteredList = if (query.isNotEmpty()) {
                allUsers.filter { user -> user.name.lowercase().contains(query) }
            }
            else {
                allUsers
            }


            binding.recyclerViewUsers.adapter = SearchAdapter(filteredList) { user ->
                openChatScreen(user)
            }
        }
    }

}
