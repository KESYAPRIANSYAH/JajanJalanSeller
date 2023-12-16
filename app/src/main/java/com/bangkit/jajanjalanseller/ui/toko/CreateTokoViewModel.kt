package com.bangkit.jajanjalanseller.ui.toko

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.Result
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class CreateTokoViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _createStore = MutableLiveData<Result<CreateTokoResponse>>()
    val createStore: LiveData<Result<CreateTokoResponse>> = _createStore

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val errorMessage = MutableLiveData<String>()
    val getErrorMessage: LiveData<String> = errorMessage

    val token = "Bearer ${sellerRepository.getToken()}"
    fun createToko(
        name: String,
        address: String,
        phone: String,
        lat: Float? = null,
        lon: Float? = null,
        description: String,
        image: MultipartBody.Part,
    ) {

        _isLoading.value = true
        viewModelScope.launch {

            try {
                val response =
                    sellerRepository.createToko(token,name, address, phone, lat, lon, description, image)
                _createStore.postValue(Result.Success(response))
                _isLoading.value = false
            } catch (e: HttpException) {

                Log.d(TAG, "CREATE TOKO FAIL: $errorMessage")

            }
        }

    }

}