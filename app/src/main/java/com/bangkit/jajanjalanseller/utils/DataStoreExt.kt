package com.bangkit.jajanjalanseller.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val JAJAN_JALAN_DATA_STORE_NAME = "JajanJalanSellerDataStore"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = JAJAN_JALAN_DATA_STORE_NAME)