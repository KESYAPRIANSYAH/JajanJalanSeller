package com.bangkit.jajanjalanseller.ui.toko

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bangkit.jajanjalanseller.databinding.ActivityCreateTokoBinding
import com.bangkit.jajanjalanseller.data.Result
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateTokoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTokoBinding
    private val viewModel: CreateTokoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnConfirm.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val address = binding.edRegisterAddres.text.toString().trim()
            val phone = binding.edRegisterPhone.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.createToko(name, address, phone)
            }
        }

        viewModel.createStoreStatus.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.btnConfirm.isEnabled = false
                }

                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnConfirm.isEnabled = true
                    Toast.makeText(this, "Toko berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    // Go to another activity or finish this one
                }

                is Result.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnConfirm.isEnabled = true
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
