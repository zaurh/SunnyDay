package com.example.sunnyday.di

import com.example.sunnyday.common.Constants.BASE_URL
import com.example.sunnyday.data.remote.WeatherApi
import com.example.sunnyday.data.repository.WeatherRepositoryImpl
import com.example.sunnyday.domain.repository.WeatherRepository
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
    fun provideRetrofitApi(retrofit: Retrofit): WeatherApi{
        return retrofit.create(WeatherApi::class.java)
    }

    //Repository
    @Singleton
    @Provides
    fun provideRepository(api: WeatherApi): WeatherRepository = WeatherRepositoryImpl(api)

}