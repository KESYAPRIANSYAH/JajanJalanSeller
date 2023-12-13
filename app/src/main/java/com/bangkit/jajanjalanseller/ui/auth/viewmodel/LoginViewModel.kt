package com.bangkit.jajanjalanseller.ui.auth.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.jajanjalanseller.data.SellerRepository
import com.bangkit.jajanjalanseller.data.remote.response.SellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bangkit.jajanjalanseller.data.Result
import com.bangkit.jajanjalanseller.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: SellerRepository
): ViewModel() {
    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()
    val loginResponse: LiveData<Result<LoginResponse>> = resultLogin




    val resultUser: LiveData<Result<SellerResponse>> get() = repository.resultUser
    fun login(email: String, password : String) = repository.login(email, password)

    fun saveUser(userId: String, email: String, name: String, image: String , password: String, role :String ,token:String) {
        viewModelScope.launch {
            repository.saveUser(userId, email, name, image, password, role,token)
        }
    }
    fun getDetailUser(userId: String): LiveData<Result<SellerResponse>> {
        viewModelScope.launch {
            repository.getDetailUser(userId)
        }
        return resultUser
    }


}

