package com.bangkit.jajanjalanseller.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bangkit.jajanjalanseller.data.local.DataStoreManager
import com.bangkit.jajanjalanseller.data.remote.retrofit.ApiService
import com.bangkit.jajanjalanseller.utils.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Singleton
    @Provides
    fun provideApiService(authInterceptor: Interceptor): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://jajanjalan-api-wt3sy4qeta-et.a.run.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideAuthInterceptor(authManager: DataStoreManager): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()

            runBlocking {
                authManager.getToken().let { token ->
                    request.addHeader("Authorization", "Bearer $token")
                }
            }

            val response: Response = chain.proceed(request.build())
            response
        }
    }

    @Singleton
    @Provides
    fun provideDataStorePreference(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(dataStore: DataStore<Preferences>): DataStoreManager {
        return DataStoreManager(dataStore)
    }
}
