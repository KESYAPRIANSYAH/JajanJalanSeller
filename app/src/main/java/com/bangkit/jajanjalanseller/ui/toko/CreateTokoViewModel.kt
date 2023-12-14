package com.bangkit.jajanjalanseller.ui.toko

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class CreateTokoViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _createStore = MutableLiveData<CreateTokoResponse>()
    val createStore: LiveData<CreateTokoResponse> = _createStore
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val errorMessage = MutableLiveData<String>()
    val getErrorMessage: LiveData<String> = errorMessage

  fun createToko(
        token: String,
        name: RequestBody,
        address: RequestBody,
        phone: RequestBody,
        lat: Float? = null,
        lon: Float? = null,
        description: RequestBody,
        image: MultipartBody.Part,

        ) {

       val response = sellerRepository.createToko(token,name,address,phone,lat,lon,description,image).enqueue(object : Callback<CreateTokoResponse>{
           override fun onResponse(
               call:Call<CreateTokoResponse>,
               response: Response<CreateTokoResponse>
           ) {
               _isLoading.value = true

               if (response.isSuccessful) {
                  _createStore.value = response.body()
               }

           }

           override fun onFailure(call: Call<CreateTokoResponse>, t: Throwable) {
               _isLoading.value = true
               val error = "Terjadi kesalahan: ${t.message.toString()}"
               errorMessage.postValue(error)
           }

       })
    }
    fun getUser() = sellerRepository.getUser().asLiveData()
}


