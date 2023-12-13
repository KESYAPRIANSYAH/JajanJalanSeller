package com.bangkit.jajanjalanseller.ui.toko

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.Result
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.bangkit.jajanjalanseller.data.remote.response.SellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTokoViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _createStoreStatus = MutableLiveData<Result<CreateTokoResponse>>()
    val createStoreStatus get() = _createStoreStatus
    fun createToko(name: String, address: String, phone: String) {
        _createStoreStatus.postValue(Result.Loading)

        viewModelScope.launch {
            try {
                val response = sellerRepository.createToko(name, address, phone)

                _createStoreStatus.postValue(response.value)
            } catch (e: Exception) {
                val errorMessage = "Gagal membuat toko: ${e.localizedMessage}"
                _createStoreStatus.postValue(Result.Error(errorMessage))
            }
        }
    }
}
