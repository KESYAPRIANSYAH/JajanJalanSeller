package com.bangkit.jajanjalanseller.ui.toko

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CreateTokoViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _createStore = MutableLiveData<CreateTokoResponse>()
    val createStore: LiveData<CreateTokoResponse> =_createStore
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    fun createToko(
        name: RequestBody,
        address: RequestBody,
        phone: RequestBody,
        lat: Float? = null,
        lon: Float? = null,
        description: RequestBody,
        image: MultipartBody.Part

    ) {

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = sellerRepository.createToko(name, address, phone,lat,lon,description,image)
                _createStore.postValue(response.body())

            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, CreateTokoResponse::class.java)
                val errorMessage = errorBody.message
                _createStore.postValue(errorBody)
                _isLoading.postValue(false)
                Log.d(TAG, "Create Store gagal: $errorMessage")

            }
        }

    }
}


