package com.xhan.musicplayer.domain.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun exceptionOrNull(): Exception? = when (this) {
        is Success -> null
        is Error -> exception
    }
}

fun <T> successResult(data: T): Result<T> = Result.Success(data)

fun errorResult(exception: Exception): Result<Nothing> = Result.Error(exception)