package com.bangkit.jajanjalanseller.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_IMAGE = stringPreferencesKey("user_image")
        val USER_ROLE = stringPreferencesKey("user_role") // Tambahan
        val USER_PASSWORD = stringPreferencesKey("user_password") // Opsional
        val USER_TOKEN = stringPreferencesKey("user_token")


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
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[USER_IMAGE] = image
            preferences[USER_ROLE] = role
            preferences[USER_TOKEN] = token

        }
    }

//    fun getSession(): Flow<SellerModel> {
//        return dataStore.data.map { preferences ->
//            SellerModel(
//                preferences[USER_ID] ?: "",
//                preferences[USER_EMAIL] ?: "",
//                preferences[USER_NAME] ?: "",
//                preferences[USER_IMAGE] ?: "",
//                preferences[USER_IMAGE] ?: "",
//                preferences[USER_PASSWORD] ?: "",
//            )
//        }
//    }

    val getUser: Flow<SellerModel> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("DataStoreManager", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userId = preferences[USER_ID] ?: ""
            val email = preferences[USER_EMAIL] ?: ""
            val name = preferences[USER_NAME] ?: ""
            val image = preferences[USER_IMAGE] ?: ""
            val password = preferences[USER_PASSWORD] ?: ""
            val token = preferences[USER_TOKEN] ?: ""
            SellerModel(userId, email, name, image, password, token)
        }


    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }


    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN]
        }
    }

}