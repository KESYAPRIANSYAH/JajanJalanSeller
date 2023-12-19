package com.bangkit.jajanjalanseller.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bangkit.jajanjalanseller.data.local.SellerModel
import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.bangkit.jajanjalanseller.data.remote.response.LoginResponse
import com.bangkit.jajanjalanseller.data.remote.response.Seller
import com.bangkit.jajanjalanseller.data.remote.response.SellerResponse
import com.bangkit.jajanjalanseller.data.remote.retrofit.ApiService
import com.bangkit.jajanjalanseller.utils.UserPreference
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SellerRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: UserPreference
) {

    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()
    private val resultRegister = MediatorLiveData<Result<SellerResponse>>()
    private val _resultUser = MediatorLiveData<Result<SellerResponse>>()
    val resultUser: LiveData<Result<SellerResponse>> get() = _resultUser
    private val _seller = MediatorLiveData<SellerModel>()
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
                        responseData.userInfo?.token?.let { token ->
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

    fun register(
        email: String,
        name: String,
        password: String,
        role: String
    ): MediatorLiveData<Result<SellerResponse>> {
        resultRegister.value = Result.Loading

        apiService.register(Seller(email = email, name = name, password = password, role = role))
            .enqueue(object : Callback<SellerResponse> {
                override fun onResponse(
                    call: Call<SellerResponse>,
                    response: Response<SellerResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()!!
                        resultRegister.value = Result.Success(responseData)
                        responseData.sellerDetail?.token?.let { token ->
                            runBlocking {
                                dataStore.saveToken(token)
                            }
                        }
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


    fun saveUser(
        userId: String,
        email: String,
        name: String,
        image: String,
        password: String,
        role: String

    ) {
        dataStore.saveUser(userId, email, name, image, password, role)
    }

    fun getUser(): SellerModel? {
        return dataStore.getUser()
    }


    suspend fun createToko(
        name: String,
        address: String,
        phone: String,
        lat: Float? = null,
        lon: Float? = null,
        description: String,
        image: MultipartBody.Part
    ): Response<CreateTokoResponse> {
        return apiService.createPenjual( name, address, phone, lat, lon, description, image)
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