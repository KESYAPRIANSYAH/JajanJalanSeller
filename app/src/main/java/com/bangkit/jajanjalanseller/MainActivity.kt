package com.bangkit.jajanjalanseller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import androidx.navigation.NavController
import com.bangkit.jajanjalanseller.databinding.ActivityMainBinding
import com.bangkit.jajanjalanseller.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnLogout.setOnClickListener {
                viewModel.logout()
                navigateToAuthActivity()
            }
        }

    }

    private fun navigateToAuthActivity() {
        Intent(this, AuthActivity::class.java).also { intent->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}