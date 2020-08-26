package com.osai.uberx.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.osai.uberx.R
import com.osai.uberx.data.NameRepositoryImpl
import com.osai.uberx.domain.NameRepository
import com.osai.uberx.utils.CameraUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideCameraUtils(context: Context): CameraUtils =
        CameraUtils(context)

    @Singleton
    @Provides
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.userSharedPref),
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun provideNameRepository(): NameRepository =
        NameRepositoryImpl()
}
