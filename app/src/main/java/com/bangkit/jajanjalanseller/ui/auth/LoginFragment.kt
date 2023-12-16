package com.bangkit.jajanjalanseller.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bangkit.jajanjalanseller.data.Result
import com.bangkit.jajanjalanseller.databinding.FragmentLoginBinding
import com.bangkit.jajanjalanseller.ui.auth.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        validateLogin()
    }

    private fun observeLogin(email: String, password: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    Log.d("User Detail", it.data.toString())
                    val userId = it.data.userInfo?.userId.toString()
                    Log.d("User Id", userId)
                    getDetailUser(userId, it.data.userInfo?.token.toString())
                  
                }
                is Result.Error -> {
                    binding.progressIndicator.hide()
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()
                }


                else -> {
                    Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun validateLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (email.isEmpty()) {
                binding.edLoginEmail.error = "Email cannot be empty"
                binding.edLoginEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.edLoginPassword.error = "Password cannot be empty"
                binding.edLoginPassword.requestFocus()

            } else {
                observeLogin(email, password)
            }
        }
    }
    private fun getDetailUser(userId: String, token: String) {
        viewModel.getDetailUser(userId).observe(viewLifecycleOwner) { response ->
            when (response) {

                is Result.Success -> {
                    Log.d("User Login Info", response.data.toString())
                    val user = response.data.sellerDetail!!

                    saveSession(
                        user.id.toString(),
                        user.email.toString(),
                        user.name.toString(),
                        user.image.toString(),
                        user.password.toString(),
                        user.role.toString(),
                        token,
                    )
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun saveSession(
        userId: String,
        email: String,
        name: String,
        image: String,
        password: String,
        role:String,
        token: String
    ) {

        viewModel.saveUser(userId, email, name, image, password,role, token)


    }



    private fun setupAction() {
        binding.tvRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

}