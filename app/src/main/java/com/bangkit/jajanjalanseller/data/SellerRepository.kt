package com.bangkit.jajanjalanseller.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.jajanjalanseller.data.remote.response.LoginResponse
import com.bangkit.jajanjalanseller.data.local.DataStoreManager
import com.bangkit.jajanjalanseller.data.local.SellerModel
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.bangkit.jajanjalanseller.data.remote.response.Seller
import com.bangkit.jajanjalanseller.data.remote.response.SellerResponse
import com.bangkit.jajanjalanseller.data.remote.response.Shop
import com.bangkit.jajanjalanseller.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SellerRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) {
    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()
    private val resultRegister = MediatorLiveData<Result<SellerResponse>>()

    private val _resultUser = MediatorLiveData<Result<SellerResponse>>()
    val resultUser: LiveData<Result<SellerResponse>> get() = _resultUser
    val _seller = MediatorLiveData<SellerModel>()
    val seller: LiveData<SellerModel> get() = _seller

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        resultLogin.value = Result.Loading

        apiService.login(Seller(email = email, password = password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()!!
                        resultLogin.value = Result.Success(responseData)
                        responseData?.userInfo?.token?.let { token ->
                            runBlocking {
                                dataStore.saveToken(token)
                            }
                        }

                    } else {
                        resultLogin.value = Result.Error(response.message())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    resultLogin.value = Result.Error(t.message.toString())
                }
            })
        return resultLogin
    }

    fun register(email: String, name: String, password: String, role: String): MediatorLiveData<Result<SellerResponse>> {
        resultRegister.value = Result.Loading

        apiService.register(Seller(email = email, name = name, password = password, role = role)).enqueue(object : Callback<SellerResponse> {
            override fun onResponse(call: Call<SellerResponse>, response: Response<SellerResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()!!
                    resultRegister.value =Result.Success(responseData)
                } else {
                    resultRegister.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<SellerResponse>, t: Throwable) {
                resultRegister.value = Result.Error(t.message.toString())
            }
        })
        return resultRegister
    }




  suspend fun saveUser(
        userId: String,
        email: String,
        name: String,
        image: String,
        password: String,
        role: String,
        token: String

    ) {
        dataStore.saveUser(userId, email, name, image, password, token, role)
    }

    fun getUser(): Flow<SellerModel> {
        return dataStore.getUser
    }

//    fun getToken() = runBlocking { dataStore.getUser.first().token }

    suspend fun createToko(
        name: String,
        address: String,
        phone: String
    ): LiveData<Result<CreateTokoResponse>> {
        val result = MutableLiveData<Result<CreateTokoResponse>>()
        result.postValue(Result.Loading)

        try {
            val token = dataStore.getToken().firstOrNull() ?: ""
            val response = withContext(Dispatchers.IO) {
                apiService.createToko(
                    token,
                    Shop(
                        name = name,
                        address = address,
                        phone = phone
                    )
                ).execute()
            }

            if (response.isSuccessful) {
                val responseData = response.body()
                result.postValue(Result.Success(responseData!!))
            } else {
                result.postValue(Result.Error(response.message()))
            }
        } catch (e: Exception) {
            result.postValue(Result.Error(e.message ?: "An error occurred"))
            Log.e("API_CALL_ERROR", "Error during API call", e)
        }

        return result
    }
    suspend fun getDetailUser(userId: String): LiveData<Result<SellerResponse>> {
        val response = apiService.getUserDetail(userId)
        if (response.isSuccessful) {
            val responseData = response.body()
            _resultUser.value = Result.Success(responseData!!)
        } else {
            _resultUser.value = Result.Error(response.message())
        }
        return _resultUser
    }




    suspend fun clear() {
        dataStore.clear()
    }

}





