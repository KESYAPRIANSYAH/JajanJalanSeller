package com.bangkit.jajanjalanseller.data

sealed class Result <out R> private constructor() {
    data class Success <out T>(val data: T): Result<T>()
    data class Errorr (val error: String): Result<Nothing>()
    data class Error(val message: String?, val code: Int? = null) : Result<Nothing>()
    data object Loading: Result<Nothing>()
}


