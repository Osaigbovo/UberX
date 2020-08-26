package com.osai.uberx.data

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
