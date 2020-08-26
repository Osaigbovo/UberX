package co.metalab.tech.interview.data

import com.squareup.moshi.Moshi
import dagger.Lazy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitService {

    companion object {
        private val builder: Retrofit.Builder = Retrofit.Builder()
            //.baseUrl(PokeAPIService.ENDPOINT)

        @JvmStatic
        fun <S> createService(moshi: Moshi, okHttpClient: Lazy<OkHttpClient>, serviceClass: Class<S>): S {
            val retrofit = builder
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .callFactory(okHttpClient.get()) // Deferred OkHttp Initialization
                .build()
            return retrofit.create(serviceClass)
        }
    }
}