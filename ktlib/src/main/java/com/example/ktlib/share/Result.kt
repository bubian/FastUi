package com.example.ktlib.share

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

inline fun Result<Any>.doSuccess(success: (Any) -> Unit) {
    if (this is Result.Success) {
        success(data)
    }
}

inline fun Result<Any>.doError(error: (Exception?) -> Unit) {
    if (this is Result.Error) {
        error(exception)
    }
}

fun main() {
    fun main() {
        // 模拟封装枚举的产生
        val result = if (true) {
            Result.Success("Success")
        } else {
            Result.Error(Exception("error"))
        }

        when (result) {
            is Result.Success -> print(result.data)
            is Result.Error -> print(result.exception)
        }
    }
}

sealed class ResultNet<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultNet<T>()
    sealed class Error(val exception: Exception) : ResultNet<Nothing>() {
        class RecoverableError(exception: Exception) : Error(exception)
        class NonRecoverableError(exception: Exception) : Error(exception)
    }
    object InProgress : ResultNet<Nothing>()
}
