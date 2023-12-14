package com.bangkit.jajanjalanseller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SellerRepository) : ViewModel() {





    fun logout() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}