package com.bangkit.jajanjalanseller.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val repository: SellerRepository
): ViewModel() {

    private val _user = MutableStateFlow(false)
    val user: StateFlow<Boolean> = _user

    fun checkUserAuthentication() {
        viewModelScope.launch {
            repository.getUser()
                .collect { user ->
                    _user.value = !user.userId.isNullOrEmpty()
                }
        }
    }
}