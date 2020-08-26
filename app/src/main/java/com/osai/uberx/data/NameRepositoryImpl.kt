package com.osai.uberx.data

import com.osai.uberx.domain.NameRepository
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NameRepositoryImpl @Inject constructor() :
    NameRepository {

    override suspend fun getName(): Result<String> {
        return try {
            val response = Response.success("Osaigbovo")
            getResult(
                response = response,
                onError = {
                    Result.Error(
                        IOException("Error getting name ${response.code()} ${response.message()}")
                    )
                }
            )
        } catch (e: Exception) {
            Result.Error(IOException("Error getting name", e))
        }
    }
}

private inline fun getResult(
    response: Response<String>,
    onError: () -> Result.Error
): Result<String> {
    if (response.isSuccessful) {
        val body = response.body()
        if (body != null) return Result.Success(body)
    }
    return onError.invoke()
}
