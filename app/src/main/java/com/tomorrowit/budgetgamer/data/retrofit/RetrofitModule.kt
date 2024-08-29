package com.tomorrowit.budgetgamer.data.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tomorrowit.budgetgamer.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().callTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build()
        } else {
            OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()
        }
    }

    @Provides
    @Singleton
    fun provideConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideRestClient(retrofit: Retrofit): RestClient {
        return retrofit.create(RestClient::class.java)
    }

}