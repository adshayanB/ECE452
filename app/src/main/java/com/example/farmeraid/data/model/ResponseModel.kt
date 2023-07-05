package com.example.farmeraid.data.model

import com.example.farmeraid.sign_in.model.SignInModel

class ResponseModel {
    sealed class FAResponse(val error : String? = null) {
        object Success : FAResponse()
        class Error(error: String) : FAResponse(error = error)
    }
    sealed class FAResponseWithData<T>(
        val data : T? = null,
        val error : String? = null,
    ) {
        class Success<T>(data: T?): FAResponseWithData<T>(data = data)
        class Error<T>(error: String): FAResponseWithData<T>(error = error)
    }
}