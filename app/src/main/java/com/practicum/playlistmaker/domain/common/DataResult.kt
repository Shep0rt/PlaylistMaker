package com.practicum.playlistmaker.domain.common

data class DataResult<T>(
    val isSuccess: Boolean,
    val data: T? = null,
    val error: ErrorType = ErrorType.NONE,
    val httpCode: Int? = null
)