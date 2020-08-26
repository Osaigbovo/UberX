package com.osai.uberx.domain

import com.osai.uberx.data.Result
import javax.inject.Inject

class NameUseCase @Inject constructor(private val nameRepository: NameRepository) {
    suspend operator fun invoke(): Result<String> {
        return when (val result = nameRepository.getName()) {
            is Result.Success -> {
                val name = result.data
                Result.Success(name)
            }
            is Result.Error -> {
                result
            }
        }
    }
}
