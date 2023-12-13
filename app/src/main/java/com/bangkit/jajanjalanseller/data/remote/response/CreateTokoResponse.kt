package com.bangkit.jajanjalanseller.data.remote.response

import com.google.gson.annotations.SerializedName

data class CreateTokoResponse(
	@field:SerializedName("data")
	val createToko: Shop? = null,
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Shop(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,



)
