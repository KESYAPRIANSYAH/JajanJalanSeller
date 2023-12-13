package com.bangkit.jajanjalanseller.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit.jajanjalanseller.MainActivity
import com.bangkit.jajanjalanseller.databinding.ActivityAuthBinding
import com.bangkit.jajanjalanseller.ui.auth.viewmodel.AuthViewModel
import com.bangkit.jajanjalanseller.ui.toko.CreateTokoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserAuthentication()
        lifecycleScope.launch {
            viewModel.user.collect { isAuthenticated ->
                // Update UI with user data
                if (isAuthenticated) {
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        Intent(this, CreateTokoActivity::class.java).also {intent->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}