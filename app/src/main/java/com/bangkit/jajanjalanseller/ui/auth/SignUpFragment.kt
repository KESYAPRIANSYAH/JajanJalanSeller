package com.bangkit.jajanjalanseller.ui.auth

import android.app.AlertDialog
import com.bangkit.jajanjalanseller.data.Result
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bangkit.jajanjalanseller.R
import com.bangkit.jajanjalanseller.databinding.FragmentSignUpfragmentBinding
import com.bangkit.jajanjalanseller.ui.auth.viewmodel.SignUpragmentViewModel
import com.bangkit.jajanjalanseller.utils.UserPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpfragmentBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var userPreference: UserPreference
    private val viewModel: SignUpragmentViewModel by viewModels()
    private  val role = "penjual"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpfragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        validateRegister()
        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeRegister(email: String, name: String, password: String, role: String) {
        viewModel.register(email, name, password, role).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnRegister.isEnabled = false
                }

                is Result.Success -> {
                    Log.d("User Detail", it.data.sellerDetail.toString())
                    binding.progressIndicator.hide()
                    userPreference.saveToken(it.data.sellerDetail?.token.toString())
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Register")
                        setMessage(getString(R.string.register_succeed))
                        setPositiveButton(getString(R.string.continue_login)) { _, _ ->
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        create()
                        show()
                    }
                }

                is Result.Errorr -> {
                    binding.progressIndicator.hide()
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                    Log.d("Error Register", it.error)
                    Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun validateRegister() {

        binding.btnRegister.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val name = binding.edRegisterName.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Register Account")
                    setMessage(getString(R.string.register_empty))
                    setPositiveButton(getString(R.string.ok)) { _, _ -> }
                    create()
                    show()
                }
            } else if (password != confirmPassword) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Register")
                    setMessage(getString(R.string.register_password_not_match))
                    setPositiveButton(getString(R.string.ok)) { _, _ -> }
                    create()
                    show()
                }
            } else {
                Toast.makeText(requireContext(), "$email, $name, $password !", Toast.LENGTH_LONG)
                    .show()
                lifecycleScope.launch {
                    observeRegister(email, name, password, role)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}