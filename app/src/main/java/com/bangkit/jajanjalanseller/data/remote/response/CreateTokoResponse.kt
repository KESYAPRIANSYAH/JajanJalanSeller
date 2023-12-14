package com.bangkit.jajanjalanseller.data.remote.response

import com.google.gson.annotations.SerializedName

data class CreateTokoResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null

)
