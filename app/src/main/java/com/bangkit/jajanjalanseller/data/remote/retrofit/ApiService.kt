package com.bangkit.jajanjalanseller.data.remote.retrofit


import com.bangkit.jajanjalanseller.data.remote.response.CreateTokoResponse
import com.bangkit.jajanjalanseller.data.remote.response.LoginResponse
import com.bangkit.jajanjalanseller.data.remote.response.Seller
import com.bangkit.jajanjalanseller.data.remote.response.SellerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/users/register")
    fun register(
        @Body user: Seller
    ): Call<SellerResponse>

    @POST("users/login")
    fun login(
        @Body user: Seller
    ): Call<LoginResponse>



    @POST("/penjual/create")
    @Multipart
    suspend fun createPenjual(
        @Part("name") name: String,
        @Part("address") address: String,
        @Part("phone") phone: String,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Part("description") description: String,
        @Part image: MultipartBody.Part,
    ) :Response<CreateTokoResponse>


    @FormUrlEncoded
    @POST("users/logout")
    fun logout(
        @Field("user_id") userId: String,
    )

    @GET("users/{id}")
    suspend fun getUserDetail(
        @Path("id") id: String
    ): Response<SellerResponse>

    @Multipart
    @PATCH("users/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Part("name") name: String,
        @Part("password") password: String,
        @Part("image") image: MultipartBody.Part,
    )


}