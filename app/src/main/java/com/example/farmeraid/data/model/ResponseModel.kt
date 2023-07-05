package com.example.farmeraid.data.model

import com.example.farmeraid.sign_in.model.SignInModel

class ResponseModel {
    sealed class FAResponse {
        object Success : FAResponse()
        data class Error(val error: String) : FAResponse()
    }
    sealed class FAResponseWithData<T> {
        class Success<T>(val data: T): FAResponseWithData<T>()
        class Error<T>(val error: String): FAResponseWithData<T>()
    }
}