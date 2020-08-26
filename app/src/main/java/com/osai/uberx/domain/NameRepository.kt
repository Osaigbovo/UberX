package com.osai.uberx.domain

import com.osai.uberx.data.Result

interface NameRepository {
    suspend fun getName(): Result<String>
}
