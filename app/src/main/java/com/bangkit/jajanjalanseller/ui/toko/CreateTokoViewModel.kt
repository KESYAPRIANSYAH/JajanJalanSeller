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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class CreateTokoViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {
    private val _createStore = MutableLiveData<Result<CreateTokoResponse>>()
    val createStore: LiveData<Result<CreateTokoResponse>> = _createStore


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun createToko(

        name: String,
        address: String,
        phone: String,
        lat: Float? = null,
        lon: Float? = null,
        description: String,
        image: MultipartBody.Part,
    ){
        viewModelScope.launch {
            try {
                val response: Response<CreateTokoResponse> = sellerRepository.createToko(
                    name,
                    address,
                    phone,
                    lat,
                    lon,
                    description,
                    image
                )

                if (response.isSuccessful) {
                    _isLoading.value=false
                    _createStore.value = Result.Success(response.body()!!)

                } else {
                    _isLoading.value = false
                    _createStore.value = Result.Error("Gagal membuat toko, status code: ${response.code()}")
                }
            } catch (e: Exception) {


                _isLoading.postValue(false)


                Log.d(TAG, "Upload File Error")
            }
        }
    }

}


