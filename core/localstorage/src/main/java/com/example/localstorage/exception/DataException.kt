package com.example.localstorage.exception

sealed class DataException(errorCode: String, message: String) : Throwable() {
    class NullValueException :
        DataException(
            errorCode = "error_null",
            message = "Expected a not null value",
        )
}
