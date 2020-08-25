package com.osai.uberx.dagger

import android.app.Application
import android.content.Context
import com.osai.uberx.utils.CameraUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (private val application: Application){

    @Singleton
    @Provides
    fun provideContext(): Context = application

    /*@Singleton
    @Provides
    fun provideRetrofitService(moshi: Moshi, okHttpClient: Lazy<OkHttpClient>): PokeAPIService =
        RetrofitService.createService(moshi, okHttpClient, PokeAPIService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)

        return clientBuilder.build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Singleton
    @Provides
    internal fun provideMoshi(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }*/

    @Singleton
    @Provides
    fun provideCameraUtils(context: Context): CameraUtils =
        CameraUtils(context)

}