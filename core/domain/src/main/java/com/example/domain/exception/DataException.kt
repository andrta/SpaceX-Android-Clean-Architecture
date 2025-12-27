package com.example.domain.exception

sealed interface DataException {
    data class NullValueException(
        val errorCode: String = "error_null",
        override val message: String = "Expected a not null value",
    ) : DataException, Throwable()
}
