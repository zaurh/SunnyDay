package com.example.sunnyday.di

import android.app.Application
import com.example.sunnyday.common.Constants.BASE_URL
import com.example.sunnyday.data.remote.GeoApi
import com.example.sunnyday.data.repository.GeoRepositoryImpl
import com.example.sunnyday.domain.repository.GeoRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideRetrofitApi(retrofit: Retrofit): GeoApi{
        return retrofit.create(GeoApi::class.java)
    }

    //Repository
    @Singleton
    @Provides
    fun provideRepository(api: GeoApi): GeoRepository = GeoRepositoryImpl(api)

}