package com.goni99.smartlibrarysystem.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goni99.smartlibrarysystem.App
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.databinding.FragmentUserBinding
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.goni99.smartlibrarysystem.utils.SharedPreferenceManager
import com.goni99.smartlibrarysystem.viewmodel.LibraryViewModel

class UserFragment:Fragment() {
    private var mBinding: FragmentUserBinding? = null
    private val binding get() = mBinding!!

    private lateinit var libraryViewModel: LibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        libraryViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(LibraryViewModel::class.java)

        mBinding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"UserFragment - onViewCreated() called")

        observeUserData()
        binding.signOutButton.setOnClickListener {
            SharedPreferenceManager.clearUserList()
            activity?.finish()
        }
    }

    private fun observeUserData(){
        libraryViewModel.userList.observe(viewLifecycleOwner, Observer {
            binding.userName.text = it[0].name
            binding.userGender.text = if (it[0].gender) "남자" else "여자"
            binding.userPhoneNumber.text = it[0].phone
            binding.userAge.text = it[0].age.toString()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}