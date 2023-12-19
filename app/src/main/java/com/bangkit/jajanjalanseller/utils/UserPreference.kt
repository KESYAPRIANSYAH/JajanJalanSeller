package com.bangkit.jajanjalanseller.utils

import android.content.Context
import android.content.SharedPreferences
import com.bangkit.jajanjalanseller.data.local.SellerModel
import com.bangkit.jajanjalanseller.utils.Constants.PREFS_TOKEN_FILE
import com.bangkit.jajanjalanseller.utils.Constants.USER_EMAIL
import com.bangkit.jajanjalanseller.utils.Constants.USER_ID
import com.bangkit.jajanjalanseller.utils.Constants.USER_IMAGE
import com.bangkit.jajanjalanseller.utils.Constants.USER_NAME
import com.bangkit.jajanjalanseller.utils.Constants.USER_ROLE
import com.bangkit.jajanjalanseller.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreference @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)


    fun saveUser(
        userId: String,
        email: String,
        name: String,
        image: String,
        password: String,
        role: String,

        ) {
        val editor = prefs.edit()

        editor.putString(USER_ID, userId)
        editor.putString(USER_EMAIL, email)
        editor.putString(USER_NAME, name)
        editor.putString(USER_IMAGE, image)
        editor.putString(USER_ROLE, role)
        editor.apply()

    }

    fun getUser(): SellerModel? {
        val userId = prefs.getString(USER_ID, null)
        val email = prefs.getString(USER_EMAIL, null)
        val name = prefs.getString(USER_NAME, null)
        val image = prefs.getString(USER_IMAGE, null)
        val role = prefs.getString(USER_ROLE, null)

        // Check if any of the values is null; if so, return null
        if (userId == null || email == null || name == null || image == null || role == null) {
            return null
        }

        // Return a User object with the retrieved information
        return SellerModel(userId, email, name, image, role)
    }

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }


    fun clear() {
        val editor = prefs.edit()
        editor.clear() // Menghapus semua data dari SharedPreferences
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}