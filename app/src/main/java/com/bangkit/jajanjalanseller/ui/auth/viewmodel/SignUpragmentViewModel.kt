package com.bangkit.jajanjalanseller.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.jajanjalanseller.data.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpragmentViewModel @Inject constructor (
    private val repository: SellerRepository
): ViewModel() {

    fun register(email: String, name: String, password : String, role: String) = repository.register(email, name, password, role)
}