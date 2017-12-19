package com.pocketimps.samples.geosearchwiki.dagger

import com.pocketimps.samples.BuildConfig
import com.pocketimps.samples.R
import com.pocketimps.samples.geosearchwiki.app.App.Companion.app
import com.pocketimps.samples.geosearchwiki.app.NetworkApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Named("NetworkTimeout")
    @Singleton
    fun provideNetworkTimeout() = 10_000L

    @Provides
    @Named("BaseUrl")
    @Singleton
    fun provideBaseUrl() = app().getString(R.string.base_url)!!

    @Provides
    @Named("HttpClient")
    @Singleton
    fun provideClient(@Named("NetworkTimeout") timeout: Long): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                                         else HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder()
                           .addInterceptor(logger)
                           .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                           .readTimeout(timeout, TimeUnit.MILLISECONDS)
                           .build()
    }

    @Singleton
    @Provides
    fun provideApi(@Named("BaseUrl") baseUrl: String,
                   @Named("HttpClient") client: OkHttpClient): NetworkApi {
        return Retrofit.Builder()
                       .baseUrl(baseUrl)
                       .client(client)
                       .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                       .addConverterFactory(GsonConverterFactory.create())
                       .build()
                       .create(NetworkApi::class.java)
    }
}
