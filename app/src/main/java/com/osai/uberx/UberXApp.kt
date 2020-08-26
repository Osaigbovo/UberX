package com.osai.uberx

import android.app.Application
import android.content.Context
import com.osai.uberx.dagger.AppComponent
import com.osai.uberx.dagger.AppModule
import com.osai.uberx.dagger.DaggerAppComponent

class UberXApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeComponent()
    }

    fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent
            .factory()
            .create(AppModule(this))
    }

    companion object {
        @JvmStatic
        fun appComponent(context: Context) =
            (context.applicationContext as UberXApp).initializeComponent()
    }
}
