package co.metalab.tech.interview.data

import androidx.annotation.ColorRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeAPIService {

//    @GET("/pokemon")
//    suspend fun getPokemons(): Response<List<Pokemon>>
//
//    @GET("/pokemon/{id}/evolutions")
//    suspend fun getEvolutions(@Path("id") id: Int): Response<List<Evolution>>
//
//    companion object {
//        const val ENDPOINT = "https://hiring-test-api.herokuapp.com"
//    }
}

data class Name(
    val id: Int,
    val identifier: String,
    val image_url: String
)