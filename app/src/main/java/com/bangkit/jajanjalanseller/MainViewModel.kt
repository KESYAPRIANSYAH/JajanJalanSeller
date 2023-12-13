package com.bangkit.jajanjalanseller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.local.SellerModel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SellerRepository) : ViewModel() {

    private val _user = MutableStateFlow<SellerModel?>(null)
    val user: StateFlow<SellerModel?> = _user



    fun logout() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}