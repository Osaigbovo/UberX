package com.osai.uberx.dagger

import android.app.Application
import com.osai.uberx.MainActivity
import com.osai.uberx.ui.BaseFragment
import com.osai.uberx.ui.gallery.GalleryFragment
import com.osai.uberx.ui.home.HomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, ViewModelModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        // with @BindsInstance, the Context passed in will be available in the graph
        fun create(appModule: AppModule): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(baseFragment: BaseFragment)
    fun inject(galleryFragment: GalleryFragment)

    fun inject(application: Application)
}
