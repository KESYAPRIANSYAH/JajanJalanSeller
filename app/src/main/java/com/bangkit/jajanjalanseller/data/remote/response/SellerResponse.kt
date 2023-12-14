package com.bangkit.jajanjalanseller.data.remote.response

import com.google.gson.annotations.SerializedName

data class SellerResponse(

	@field:SerializedName("data")
	val sellerDetail: Seller? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Seller(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,
	@field:SerializedName("token")
	val token: String? = null,
) {

}
